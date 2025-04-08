package ru.otp.service;


import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
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

    @Autowired
    private VelocityEngine velocityEngine;

    public void sendActivationEmail(User user) throws Exception {
        MimeMessage mailMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mailMessage, "utf-8");

        helper.setFrom(serviceName);
        helper.setTo(user.getUsername());
        helper.setSubject("Account activation");
        VelocityContext context = new VelocityContext();
        context.put("username", user.getUsername());
        context.put("activationCode", user.getActivationCode());
        StringWriter stringWriter = new StringWriter();
        velocityEngine.mergeTemplate("activation.vm", "UTF-8", context, stringWriter);
        String text = stringWriter.toString();
        helper.setText(text, true);
        mailSender.send(mailMessage);
        log.info("Email sent! To: " + user.getUsername() + ". Time: " + LocalDateTime.now());

    }

    public void sendForgotPasswordEmail(User user) throws Exception {
        MimeMessage mailMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mailMessage, "utf-8");

        helper.setFrom(serviceName);
        helper.setTo(user.getUsername());
        helper.setSubject("Password restore");

        VelocityContext context = new VelocityContext();
        context.put("username", user.getUsername());
        context.put("restoreCode", user.getResetPasswordCode());
        StringWriter stringWriter = new StringWriter();
        velocityEngine.mergeTemplate("forgotPassword.vm", "UTF-8", context, stringWriter);
        String text = stringWriter.toString();
        helper.setText(text, true);
        mailSender.send(mailMessage);
        log.info("Email sent! To: " + user.getUsername() + ". Time: " + LocalDateTime.now());

    }
}
