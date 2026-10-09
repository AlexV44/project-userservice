package com.project.userservice.exception.notfound;

import com.project.userservice.exception.BaseHttpException;
import org.springframework.http.HttpStatus;

public abstract class NotFoundException extends BaseHttpException {
    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
