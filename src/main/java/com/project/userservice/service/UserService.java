package com.project.userservice.service;

import com.project.userservice.dto.request.UserCreateRequest;
import com.project.userservice.dto.request.UserUpdateRequest;
import com.project.userservice.dto.response.UserResponse;
import com.project.userservice.entity.enums.UserStatus;
import com.project.userservice.repository.specification.UserSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    UserResponse createUser(UserCreateRequest dto);
    UserResponse getUserById(UUID id);
    UserResponse getUserByEmail(String email);
    Page<UserResponse> searchUsers(String name, String surname, Pageable pageable);
    UserResponse updateUser(UUID id, UserUpdateRequest dto);
    UserResponse updateUserStatus(UUID id, UserStatus status);
    void deleteUser(UUID id);

}
