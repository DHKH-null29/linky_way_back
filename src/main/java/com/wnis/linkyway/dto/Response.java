package com.wnis.linkyway.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Response<T> {

    private int code;
    private T data;
    private String message;

    public static <T> Response<T> of(HttpStatus httpStatus, T data, String message) {
        return new Response<>(httpStatus.value(), data, message);
    }

}
