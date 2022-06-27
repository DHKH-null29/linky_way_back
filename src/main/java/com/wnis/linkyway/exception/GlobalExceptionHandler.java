package com.wnis.linkyway.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.wnis.linkyway.exception.common.BusinessException;
import com.wnis.linkyway.exception.error.ErrorCode;
import com.wnis.linkyway.exception.error.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MissingServletRequestParameterException.class)
    private ResponseEntity<ErrorResponse> handleMethodMissingServletRequestParameterException(MissingServletRequestParameterException e, HttpServletRequest request) {
        log.error(e.getClass().getSimpleName(), e.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, request.getRequestURI());
        return ResponseEntity.badRequest().body(errorResponse);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error(e.getClass().getSimpleName(), e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, e.getBindingResult(), request.getRequestURI());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    private ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        log.error(e.getClass().getSimpleName(), e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(e, request.getRequestURI());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    private ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
        log.error(e.getClass().getSimpleName());

        if (e.getRootCause() instanceof InvalidFormatException) {
            InvalidFormatException invalidFormatException = (InvalidFormatException) e.getRootCause();
            // NPE 발생 가능성 있음
            return ResponseEntity.badRequest()
                    .body(ErrorResponse.of(invalidFormatException, request.getRequestURI()));
        }

        return ResponseEntity.badRequest()
                .body(ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, request.getRequestURI()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    private ResponseEntity<ErrorResponse> handleMethodNotAllowedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.error(e.getClass().getSimpleName(), e);

        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.METHOD_NOT_ALLOWED, "Allow: " + Arrays.toString(e.getSupportedMethods()), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException e, HttpServletRequest request) {
        log.error(e.getClass().getName(), e.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(ErrorCode.UNAUTHORIZED, e.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedExceptionException(AccessDeniedException e, HttpServletRequest request) {
        log.error(e.getClass().getName(), e.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.of(ErrorCode.FORBIDDEN, e.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(BusinessException.class)
    private ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.error(e.getClass().getSimpleName(), e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(e.getErrorCode().getCode()).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    private ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest request) {
        log.error(e.getClass().getName(), e);

        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, request.getRequestURI());
        return ResponseEntity.internalServerError().body(errorResponse);
    }

}
