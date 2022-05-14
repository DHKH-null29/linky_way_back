package com.wnis.linkyway.exception.common;

import com.wnis.linkyway.exception.error.ErrorCode;

public class InvalidValueException extends BusinessException {

    public InvalidValueException(String message) {
        super(ErrorCode.INVALID_INPUT_VALUE, message);
    }

}
