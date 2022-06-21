package com.wnis.linkyway.exception.common;

import com.wnis.linkyway.exception.error.ErrorCode;

public class EmailSendException extends BusinessException {

    public EmailSendException(String message) {
        super(ErrorCode.EMAIL_SEND_ERROR, message);
    }

}
