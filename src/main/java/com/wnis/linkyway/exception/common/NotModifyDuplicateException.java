package com.wnis.linkyway.exception.common;

public class NotModifyDuplicateException extends ResourceConflictException {
    public NotModifyDuplicateException(String message) {
        super(message);
    }
}
