package com.project.userservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PaymentCardRequest(
        @NotBlank(message = "Gateway token is required")
        String gatewayToken,

        @NotBlank(message = "Last 4 digits are required")
        @Size(min = 4, max = 4, message = "Must be exactly 4 digits")
        String last4,

        @NotBlank(message = "Cardholder name is required")
        String holder,

        @NotNull(message = "Expiration date is required")
        LocalDate expirationDate
) {}
