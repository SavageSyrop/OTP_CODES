package ru.otp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.otp.dao.OtpCodesDao;
import ru.otp.dao.OtpConfigDao;
import ru.otp.entities.OtpCode;
import ru.otp.entities.OtpConfig;
import ru.otp.entities.User;
import ru.otp.entities.UserPrincipal;
import ru.otp.enums.OtpType;

import java.util.List;
import java.util.Random;

import static ru.otp.enums.OtpStatus.*;

@Component
public class OtpServiceImpl implements OtpService {
    @Autowired
    private OtpConfigDao otpConfigDao;
    @Autowired
    private OtpCodesDao otpCodesDao;
    @Autowired
    private TgService tgService;
    @Autowired
    private MailService mailService;
    @Autowired
    private PhoneService phoneService;
    @Autowired
    private FileService fileService;

    @Override
    public OtpConfig saveConfig(OtpConfig newConfig) {
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

    @Override
    public boolean validate(String code, OtpType otpType) {
        User currentUser = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        List<OtpCode> otpCodes = otpCodesDao.findAllByOtpTypeAndUser(otpType, currentUser);
        for (OtpCode otpCode : otpCodes) {
            if (ACTIVE.equals(otpCode.getOtpCodeStatus())) {
                long currentTime = System.currentTimeMillis();
                OtpConfig config = otpConfigDao.findFirst().get();
                if (currentTime - otpCode.getCreationTime() - config.getExipesAfterMillis() > 0) {
                    if (otpCode.getOtpCode().equals(code)) {
                        otpCode.setOtpCodeStatus(USED);
                    }
                } else {
                    otpCode.setOtpCodeStatus(EXPIRED);
                    otpCodesDao.save(otpCode);
                    continue;
                }
                otpCodesDao.save(otpCode);
                return true;
            }
        }
        return false;
    }

    @Override
    public void createOtp(OtpType otpType) throws Exception {
        OtpCode code = new OtpCode();
        long currentTime = System.currentTimeMillis();
        OtpConfig config = otpConfigDao.findFirst().get();
        User currentUser = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        code.setOtpType(otpType);
        code.setOtpCodeStatus(ACTIVE);
        code.setOtpCode(generateOtpCode(config.getOtpCodeLength()));
        code.setUser(currentUser);
        code.setCreationTime(System.currentTimeMillis());
        switch (otpType) {
            case TG: {
                tgService.sendOtpMessage(currentUser, code.getOtpCode());
                break;
            }
            case PHONE: {
                phoneService.sendOtpMessage(currentUser, code.getOtpCode());
                break;
            }
            case MAIL: {
                mailService.sendOtpMessage(currentUser, code.getOtpCode());
                break;
            }
            case FILE: {
                fileService.sendOtpMessage(currentUser, code.getOtpCode());
                break;
            }
        }
        otpCodesDao.save(code);
    }

    @Override
    public boolean validateFile(String code) {
        OtpConfig config = otpConfigDao.findFirst().get();
        User currentUser = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        return fileService.validate(currentUser, code, config.getExipesAfterMillis());
    }

    private String generateOtpCode(Long otpCodeLength) {
        int rand = new Random().nextInt((int) Math.pow(10, otpCodeLength), (int) Math.pow(10, otpCodeLength + 1) - 1);
        return Integer.toString(rand);
    }
}
