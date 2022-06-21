package com.wnis.linkyway.redis.values;

import com.wnis.linkyway.exception.common.EmailSendException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class EmailVerificationValueTest {


    @Nested
    @DisplayName("이메일 인증 정보 갱신 테스트")
    class EmailVerificationUpdateTest {

        private static final int RETRY_COUNT = 5;
        private static final String CODE = "code";

        @Test
        @DisplayName("이메일 인증 정보를 성공적으로 갱신")
        void updateRequestShouldSuccess() {
            EmailVerificationValue emailVerificationValue = new EmailVerificationValue();
            emailVerificationValue.updateVerificationInfo(CODE);
            assertThat(emailVerificationValue.getCurrentCount()).isEqualTo(1);
            assertThat(emailVerificationValue.getCode()).isEqualTo(CODE);
        }

        @Test
        @DisplayName("제한 횟수 초과로 이메일 인증 정보 갱신 요청은 예외를 반환")
        void updateRequestThrowsEmailSendExceptionWithExceededRetryCountLimit() {
            EmailVerificationValue emailVerificationValue =
                    new EmailVerificationValue(RETRY_COUNT, CODE);
            assertThatThrownBy(()-> emailVerificationValue.updateVerificationInfo("newCode"))
                    .isInstanceOf(EmailSendException.class);
        }

    }
}
