package ru.otp.service;

import lombok.extern.slf4j.Slf4j;
import org.smpp.Connection;
import org.smpp.Session;
import org.smpp.TCPIPConnection;
import org.smpp.pdu.BindResponse;
import org.smpp.pdu.BindTransmitter;
import org.smpp.pdu.SubmitSM;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.otp.entities.User;

@Component
@Slf4j
public class PhoneService {

    @Value("${spring.smpp.host}")
    private String host;
    @Value("${spring.smpp.port}")
    private Integer port;
    @Value("${spring.smpp.system_id}")
    private String systemId;
    @Value("${spring.smpp.password}")
    private String password;
    @Value("${spring.smpp.system_type}")
    private String systemType;
    @Value("${spring.smpp.source_addr}")
    private String sourceAddress;

    public void sendOtpMessage(User currentUser, String otpCode) {
        Connection connection;
        Session session;

        try {
            // 1. Установка соединения
            connection = new TCPIPConnection(host, port);
            session = new Session(connection);
            // 2. Подготовка Bind Request
            BindTransmitter bindRequest = new BindTransmitter();
            bindRequest.setSystemId(systemId);
            bindRequest.setPassword(password);
            bindRequest.setSystemType(systemType);
            bindRequest.setInterfaceVersion((byte) 0x34); // SMPP v3.4
            bindRequest.setAddressRange(sourceAddress);
            // 3. Выполнение привязки
            BindResponse bindResponse = session.bind(bindRequest);
            if (bindResponse.getCommandStatus() != 0) {
                throw new Exception("Bind failed: " + bindResponse.getCommandStatus());
            }
            // 4. Отправка сообщения
            SubmitSM submitSM = new SubmitSM();
            submitSM.setSourceAddr(sourceAddress);
            submitSM.setDestAddr(currentUser.getPhoneNumber());
            submitSM.setShortMessage("Your code: " + otpCode);
            session.submit(submitSM);
            log.info("Success SMPP code");
        } catch (Exception e) {
            log.error("Error during SMPP code");
        }
    }
}
