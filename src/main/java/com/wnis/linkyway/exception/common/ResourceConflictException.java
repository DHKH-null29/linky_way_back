package com.wnis.linkyway.exception.common;

import com.wnis.linkyway.exception.error.ErrorCode;
import org.springframework.http.HttpStatus;

public class ResourceConflictException extends BusinessException {
    
    public ResourceConflictException(String message) {
        super(ErrorCode.CONFLICT, message);
    }
}
