package com.wnis.linkyway.exception.common;

import lombok.Getter;

@Getter
public class NotFoundEntityException extends ResourceNotFoundException {
    
    public NotFoundEntityException(String message) {
        super(message);
    }
}
