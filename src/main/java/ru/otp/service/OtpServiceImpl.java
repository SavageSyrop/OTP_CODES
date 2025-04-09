package ru.otp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.otp.dao.OtpConfigDao;
import ru.otp.entities.OtpConfig;

@Component
public class OtpServiceImpl implements OtpService {
    @Autowired
    private OtpConfigDao otpConfigDao;

    @Override
    public OtpConfig save(OtpConfig newConfig) {
        return otpConfigDao.save(newConfig);
    }

    @Override
    public void deleteConfig(OtpConfig oldConfig) {
        otpConfigDao.save(oldConfig);
    }

    @Override
    public OtpConfig getConfig() {
        return otpConfigDao.findAll().getFirst();
    }
}
