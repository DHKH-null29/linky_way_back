package com.wnis.linkyway.exception.common;

import com.wnis.linkyway.exception.error.ErrorCode;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String message) {
        super(ErrorCode.RESOURCE_NOT_FOUND, message);
    }

}
