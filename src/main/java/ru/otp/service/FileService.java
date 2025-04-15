package ru.otp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.otp.entities.User;
import ru.otp.exceptions.OtpValidationException;

import java.io.*;

@Component
@Slf4j
public class FileService {
    public void sendOtpMessage(User currentUser, String otpCode) {
        String filename = currentUser.getId() + ".otp";
        File file = new File(filename);
        if (file.length() != 0) {
            file.delete();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(otpCode + ":" + System.currentTimeMillis());
            log.info("Successfully added otp info to file");
        } catch (IOException e) {
            log.info("Error during writing otp in file: " + e.getMessage());
        }
    }

    public boolean validate(User currentUser, String code, Long exipesAfterMillis) {
        String filename = currentUser.getId() + ".otp";
        File file = new File(filename);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String fileData = reader.readLine();
                String[] splitData = fileData.split(":");
                String fileCode = splitData[0];
                String creatingTime = splitData[1];
                long expiresIn = Long.parseLong(creatingTime) + exipesAfterMillis;
                if (fileCode.equals(code) && expiresIn > System.currentTimeMillis()) {
                    reader.close();
                    file.delete();
                    return true;
                } else {
                    throw new OtpValidationException("Code is wrong or expired");
                }
            } catch (IOException e) {
                log.info("Error during reading file: " + e.getMessage());
            }
        } else {
            throw new OtpValidationException("Code not found");
        }
        return false;
    }
}
