package com.project.userservice.exception.notfound;

import java.util.UUID;

public class PaymentCardNotFoundException extends NotFoundException {
    public PaymentCardNotFoundException(UUID id) {
        super("Payment card with id " + id + " not found");
    }
}
