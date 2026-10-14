package com.project.userservice.dto.response;

import com.project.userservice.entity.enums.PaymentCardStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentCardResponse(
        UUID id,
        String holder,
        LocalDate expirationDate,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
