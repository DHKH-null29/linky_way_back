package com.wnis.linkyway.exception.common;

import lombok.Getter;

@Getter
public class NotModifyEmptyEntityException extends ResourceConflictException {

    public NotModifyEmptyEntityException(String message) {
        super(message);
    }
}
