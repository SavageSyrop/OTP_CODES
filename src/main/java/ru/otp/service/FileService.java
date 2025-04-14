package ru.otp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.otp.entities.User;

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
                if (fileCode.equals(code) && Integer.parseInt(creatingTime) + exipesAfterMillis > System.currentTimeMillis()) {
                    file.delete();
                    return true;
                } else {
                    return false;
                }
            } catch (IOException e) {
                log.info("Error during reading file: " + e.getMessage());
            }
        } else {
            return false;
        }
        return false;
    }
}
