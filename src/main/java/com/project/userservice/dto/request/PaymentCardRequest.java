package com.project.userservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PaymentCardRequest(
        @NotBlank(message = "Number is required")
        String number,

        @NotBlank(message = "Cardholder name is required")
        String holder,

        @NotNull(message = "Expiration date is required")
        LocalDate expirationDate
) {}
