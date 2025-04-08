package ru.otp.service;


import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import ru.otp.entities.User;

import java.io.StringWriter;
import java.time.LocalDateTime;


@Component
@Slf4j
public class MailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String serviceName;

    public void sendOtpMail(User user, String code) throws Exception {
        MimeMessage mailMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mailMessage, "utf-8");
        helper.setFrom(serviceName);
        helper.setTo(user.getEmail());
        helper.setSubject("Account activation");
        helper.setText(code);
        mailSender.send(mailMessage);
        log.info("Email sent! To: " + user.getEmail() + ". Time: " + LocalDateTime.now());
    }

    public void sendOtpTg(User user, String code) throws Exception {
        MimeMessage mailMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mailMessage, "utf-8");
        helper.setFrom(serviceName);
        helper.setTo(user.getEmail());
        helper.setSubject("Account activation");
        helper.setText(code);
        mailSender.send(mailMessage);
        log.info("Email sent! To: " + user.getEmail() + ". Time: " + LocalDateTime.now());
    }

    public void sendOtpPhone(User user, String code) throws Exception {
        MimeMessage mailMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mailMessage, "utf-8");
        helper.setFrom(serviceName);
        helper.setTo(user.getEmail());
        helper.setSubject("Account activation");
        helper.setText(code);
        mailSender.send(mailMessage);
        log.info("Email sent! To: " + user.getEmail() + ". Time: " + LocalDateTime.now());
    }
}
