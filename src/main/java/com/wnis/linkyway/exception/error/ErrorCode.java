package com.wnis.linkyway.exception.error;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // 4xx
    INVALID_INPUT_VALUE(400, "올바르지 않은 입력 요청"),
    INVALID_TYPE_VALUE(400, "올바르지 않은 타입 요청"),
    RESOURCE_NOT_FOUND(400, "존재하지 않는 리소스에 대한 요청"),
    UNAUTHORIZED(401, "인증 자격 없음"),
    FORBIDDEN(403, "요청 거절됨"),
    METHOD_NOT_ALLOWED(405, "올바르지 않은 HTTP 요청"),
    CONFLICT(409, "요청 충돌됨"),
    INTERNAL_SERVER_ERROR(500, "서버 내부 오류");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}