package com.project.userservice.dto.response;

import com.project.userservice.entity.enums.UserStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String surname,
        String email,
        UserStatus status,
        LocalDate birthday,
        List<PaymentCardResponse> paymentCards,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
