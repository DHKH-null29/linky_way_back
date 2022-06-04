package com.wnis.linkyway.exception.common;

public class LimitDepthException extends ResourceConflictException{
    public LimitDepthException(String message) {
        super(message);
    }
}
