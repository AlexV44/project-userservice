package com.project.userservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UserUpdateRequest(

        @Size(min = 2, max = 30)
        String name,

        @Size(min = 2, max = 50)
        String surname,

        @Email(message = "Invalid email format")
        String email,

        @Past(message = "Birthday must be in the past")
        LocalDate birthday
) {}
