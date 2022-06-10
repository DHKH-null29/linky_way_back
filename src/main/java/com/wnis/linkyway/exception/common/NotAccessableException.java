package com.wnis.linkyway.exception.common;

import com.wnis.linkyway.exception.error.ErrorCode;

public class NotAccessableException extends BusinessException {

    public NotAccessableException(String message) {
        super(ErrorCode.UNAUTHORIZED, message);
    }
}
