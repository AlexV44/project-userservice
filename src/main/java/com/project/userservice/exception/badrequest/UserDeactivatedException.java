package com.project.userservice.exception.badrequest;

import java.util.UUID;

public class UserDeactivatedException extends BadRequestException {
    public UserDeactivatedException(UUID id) {
        super("User " + id + " has status DEACTIVATED");
    }
}
