package com.project.userservice.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UserCreateRequest(
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 30)
        String name,

        @NotBlank(message = "Surname is required")
        @Size(min = 2, max = 50)
        String surname,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotNull(message = "Birthday is required")
        @Past(message = "Birthday must be in the past")
        LocalDate birthday
) {}