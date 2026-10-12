package com.project.userservice.dto.request;

import com.project.userservice.entity.enums.UserStatus;
import jakarta.validation.constraints.NotNull;

public record UserStatusUpdateRequest(
        @NotNull(message = "Status cannot be null")
        UserStatus status
) {}
