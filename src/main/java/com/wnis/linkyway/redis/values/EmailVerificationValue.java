package com.wnis.linkyway.redis.values;

import com.wnis.linkyway.exception.common.EmailSendException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailVerificationValue {

    private static final int RETRY_COUNT = 5;

    private int currentCount;
    private String code;

    public EmailVerificationValue() {
        this.currentCount = 0;
        this.code = "";
    }

    public void updateVerificationInfo(String code) {
        checkRetryCount();
        this.currentCount++;
        this.code = code;
    }

    public void checkSameCode(String code) {
        if (!this.code.equals(code)) {
            throw new EmailSendException("잘못된 인증 코드입니다.");
        }
    }

    private void checkRetryCount() {
        if (currentCount >= RETRY_COUNT) {
            throw new EmailSendException(
                    "연속 " + RETRY_COUNT + "회 인증 요청으로 일시적으로 메일 전송이 제한되었습니다. 잠시후 다시 시도해주세요");
        }
    }

}
