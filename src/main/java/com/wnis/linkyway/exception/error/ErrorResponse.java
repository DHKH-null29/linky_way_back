package com.wnis.linkyway.exception.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

    private int code;

    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String details;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String path;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<FieldError> errors;

    private ErrorResponse(ErrorCode errorCode, String path) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.path = path;
    }

    private ErrorResponse(ErrorCode errorCode, String details, String path) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.details = details;
        this.path = path;
    }

    private ErrorResponse(ErrorCode errorCode, List<FieldError> errors, String path) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.errors = errors;
        this.path = path;
    }

    public static ErrorResponse of(ErrorCode errorCode, String path) {
        return new ErrorResponse(errorCode, path);
    }

    public static ErrorResponse of(ErrorCode errorCode, String details, String path) {
        return new ErrorResponse(errorCode, details, path);
    }

    public static ErrorResponse of(ErrorCode errorCode, BindingResult bindingResult, String path) {
        return new ErrorResponse(errorCode, FieldError.ofBindResults(bindingResult), path);
    }

    public static ErrorResponse of(MethodArgumentTypeMismatchException e, String path) {
        return new ErrorResponse(ErrorCode.INVALID_TYPE_VALUE, Collections.singletonList(FieldError.ofTypeMisMatch(e)), path);
    }

    public static ErrorResponse of(InvalidFormatException e, String path) {
        return new ErrorResponse(ErrorCode.INVALID_TYPE_VALUE, Collections.singletonList(FieldError.ofInvalidFormat(e)), path);
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FieldError {

        private String field;
        private String value;
        private String msg;

        private static FieldError ofTypeMisMatch(MethodArgumentTypeMismatchException e) {
            return FieldError.builder()
                    .field(e.getName())
                    .value(getRejectValue(e.getValue()))
                    .msg(e.getErrorCode())
                    .build();
        }

        private static FieldError ofInvalidFormat(InvalidFormatException e) {
            return FieldError.builder()
                    .field(e.getPath().get(0).getFieldName())
                    .value(e.getValue() + "")
                    .msg("'" + e.getTargetType().getSimpleName() + "' Type 을 원해요")
                    .build();
        }

        private static List<FieldError> ofBindResults(BindingResult bindingResult) {
            return bindingResult.getFieldErrors()
                    .stream()
                    .map(e -> FieldError.builder()
                            .field(e.getField())
                            .value(getRejectValue(e.getRejectedValue()))
                            .msg(e.getDefaultMessage())
                            .build())
                    .collect(Collectors.toList());
        }

        private static String getRejectValue(Object rejectValue) {

            return rejectValue == null ? "" : rejectValue.toString();
        }

    }

}
