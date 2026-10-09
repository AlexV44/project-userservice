package com.project.userservice.dto.response;

import com.project.userservice.entity.enums.UserStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record UserResponse(
        Long id,
        String name,
        String surname,
        String email,
        UserStatus status,
        LocalDate birthday,
        List<PaymentCardResponse> paymentCards,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
