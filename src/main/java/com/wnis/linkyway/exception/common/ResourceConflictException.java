package com.wnis.linkyway.exception.common;

import com.wnis.linkyway.exception.error.ErrorCode;

public class ResourceConflictException extends BusinessException {

    public ResourceConflictException(String message) {
        super(ErrorCode.CONFLICT, message);
    }
}
