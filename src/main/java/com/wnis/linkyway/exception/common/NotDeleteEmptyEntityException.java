package com.wnis.linkyway.exception.common;

import lombok.Getter;

@Getter
public class NotDeleteEmptyEntityException extends ResourceConflictException {

    public NotDeleteEmptyEntityException(String message) {
        super(message);
    }
}
