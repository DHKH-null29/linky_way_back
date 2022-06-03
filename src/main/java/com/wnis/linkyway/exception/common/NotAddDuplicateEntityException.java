package com.wnis.linkyway.exception.common;

import lombok.Getter;

@Getter
public class NotAddDuplicateEntityException extends ResourceConflictException {
    

    
    public NotAddDuplicateEntityException(String message) {
        super(message);
    }
}
