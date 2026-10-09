package com.project.userservice.exception.limit;

import com.project.userservice.exception.BaseHttpException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class CardLimitExceededException extends BaseHttpException {
    public CardLimitExceededException(UUID userId) {
        super("User with id " + userId + " cannot have more than 5 cards", HttpStatus.BAD_REQUEST);
    }
}
