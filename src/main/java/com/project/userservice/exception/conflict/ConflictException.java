package com.project.userservice.exception.conflict;

import com.project.userservice.exception.BaseHttpException;
import org.springframework.http.HttpStatus;

public abstract class ConflictException extends BaseHttpException {
    public ConflictException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
