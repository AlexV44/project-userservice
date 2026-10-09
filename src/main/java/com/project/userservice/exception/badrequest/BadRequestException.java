package com.project.userservice.exception.badrequest;

import com.project.userservice.exception.BaseHttpException;
import org.springframework.http.HttpStatus;

public class BadRequestException extends BaseHttpException {
    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
