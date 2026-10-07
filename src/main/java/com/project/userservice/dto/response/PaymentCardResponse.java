package com.project.userservice.dto.response;

import com.project.userservice.entity.enums.PaymentCardStatus;

import java.time.LocalDate;

public record PaymentCardResponse(
        Long id,
        String last4,
        String holder,
        LocalDate expirationDate,
        PaymentCardStatus status
) {}
