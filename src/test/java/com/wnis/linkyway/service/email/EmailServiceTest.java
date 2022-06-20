package com.wnis.linkyway.service.email;

import com.wnis.linkyway.exception.common.EmailSendException;
import com.wnis.linkyway.redis.RedisProvider;
import com.wnis.linkyway.redis.values.EmailVerificationValue;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private RedisProvider redisProvider;

    @Mock
    private JavaMailSender javaMailSender;

    private final String EMAIL = "ehd0309@naver.com";

    @BeforeEach
    void initMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("이메일 전송 요청 시 이메일 전송 메소드가 동작해야 한다.")
    void sendEmailRequestShouldDoSendEmailOfJavaEmailSender() throws Exception {
        emailService.sendEmail(EMAIL, "제목", "보낼 내용");
        verify(javaMailSender).send(any(SimpleMailMessage.class));
    }

    @Nested
    @DisplayName("인증 코드 메일 전송 테스트")
    class SendVerificationCodeTest {

        @Test
        @DisplayName("첫 인증 요청자를 대상으로 정상적으로 인증 코드를 발송한다.")
        void SendEmailRequestFirstTimeShouldDoSendEmail() throws Exception {
            emailService.sendVerificationCode(EMAIL);
            verify(javaMailSender).send(any(SimpleMailMessage.class));
        }

        @Test
        @DisplayName("이미 인증했던 요청자를 대상으로 정상적으로 인증 코드를 재발송한다.")
        void SendEmailRequestShouldDoSendEmail() throws Exception {
            EmailVerificationValue emailVerificationValue =
                    new EmailVerificationValue(3, "CODE");
            given(redisProvider.getData(EMAIL, EmailVerificationValue.class))
                    .willReturn(emailVerificationValue);
            emailService.sendVerificationCode(EMAIL);
            assertThat(emailVerificationValue.getCurrentCount()).isEqualTo(4);
            assertThat(emailVerificationValue.getCode()).isNotEqualTo("CODE");
        }

        @Test
        @DisplayName("인증 횟수를 초과한 요청자를 대상으로 예외를 발생하고 인증 코드를 발송하지 않는다")
        void SendEmailRequestWithExceededCountShouldThrowsEmailSendException() throws Exception {
            EmailVerificationValue emailVerificationValue =
                    new EmailVerificationValue(999, "CODE");
            given(redisProvider.getData(EMAIL, EmailVerificationValue.class))
                    .willReturn(emailVerificationValue);
            assertThatThrownBy(() -> emailService.sendVerificationCode(EMAIL))
                    .isInstanceOf(EmailSendException.class);
        }
    }

}
