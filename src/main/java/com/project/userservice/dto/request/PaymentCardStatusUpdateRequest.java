package com.project.userservice.dto.request;

import com.project.userservice.entity.enums.PaymentCardStatus;
import jakarta.validation.constraints.NotNull;

public record PaymentCardStatusUpdateRequest(
        @NotNull(message = "Status cannot be null")
        PaymentCardStatus status
) {}
