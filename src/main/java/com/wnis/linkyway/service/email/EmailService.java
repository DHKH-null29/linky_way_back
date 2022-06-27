package com.wnis.linkyway.service.email;

import com.wnis.linkyway.exception.common.EmailSendException;
import com.wnis.linkyway.redis.RedisConstants;
import com.wnis.linkyway.redis.RedisProvider;
import com.wnis.linkyway.redis.values.EmailVerificationValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static com.wnis.linkyway.redis.RedisConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;
    private final RedisProvider redisProvider;

    private static final String VERIFICATION_SUBJECT = "LinkyWay 에서 이메일 인증코드 발송";

    public void confirmVerificationCode(String email, String code) {
        EmailVerificationValue emailVerification = redisProvider.getData(email, EmailVerificationValue.class);
        if (emailVerification == null) {
            throw new EmailSendException("인증 코드 발송된 적 없음");
        }
        emailVerification.checkSameCode(code);
        redisProvider.deleteData(email);
    }

    public void sendVerificationCode(String email) {
        try {
            EmailVerificationValue emailVerification = getEmailVerification(email);
            emailVerification.updateVerificationInfo(createCode());
            sendEmail(email, VERIFICATION_SUBJECT, setVerificationMailText(emailVerification.getCode()));
            redisProvider.setDataWithExpiration(email, emailVerification, EMAIL_VALIDATION_EXPIRATION_TIME);
        } catch (MailException e) {
            log.error("", e);
            throw new EmailSendException("알 수 없는 이유로 메일 전송에 실패했습니다");
        }
    }

    public void sendEmail(String receiver, String subject, String text) throws MailException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(receiver);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    private String setVerificationMailText(String code) {
        return "인증번호: " + code;
    }

    private String createCode() {
        return UUID.randomUUID().toString().split("-")[0];
    }

    private EmailVerificationValue getEmailVerification(String email) {
        return Optional.ofNullable(redisProvider.getData(email, EmailVerificationValue.class))
                .orElse(new EmailVerificationValue());
    }

}
