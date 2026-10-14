package com.project.userservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BaseHttpException extends RuntimeException {

    private final HttpStatus status;
    public BaseHttpException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
