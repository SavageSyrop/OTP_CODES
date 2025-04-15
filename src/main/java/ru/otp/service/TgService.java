package ru.otp.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.otp.entities.User;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class TgService {
    @Value("${spring.tg.apiUrl}")
    private String telegramApiUrl;

    @Value("${spring.tg.chatId}")
    private String chatId;

    public void sendOtpMessage(User currentUser, String otpCode) {
        String message = String.format(currentUser.getTgId() + ", your confirmation code is: %s", otpCode);
        String url = String.format("%s?chat_id=%s&text=%s",
                telegramApiUrl,
                chatId,
                urlEncode(message));

        sendTelegramRequest(url);
    }

    private void sendTelegramRequest(String url) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    log.error("Telegram API error. Status code: {}", statusCode);
                } else {
                    log.info("Telegram message sent successfully");
                }
            }
        } catch (IOException e) {
            log.error("Error sending Telegram message: {}", e.getMessage());
        }
    }

    private static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
