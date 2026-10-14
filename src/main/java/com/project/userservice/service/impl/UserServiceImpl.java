package com.project.userservice.service.impl;

import com.project.userservice.dto.request.UserCreateRequest;
import com.project.userservice.dto.request.UserUpdateRequest;
import com.project.userservice.dto.response.UserResponse;
import com.project.userservice.entity.User;
import com.project.userservice.entity.enums.UserStatus;
import com.project.userservice.exception.conflict.EmailAlreadyExistsException;
import com.project.userservice.exception.notfound.UserNotFoundException;
import com.project.userservice.repository.UserRepository;
import com.project.userservice.repository.specification.UserSpecification;
import com.project.userservice.service.UserService;
import com.project.userservice.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest dto) {
        if(userRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyExistsException(dto.email());
        }

        var user = userMapper.toEntity(dto);
        user.setStatus(UserStatus.PENDING);

        var savedUser = userRepository.save(userMapper.toEntity(dto));

        return userMapper.toDto(savedUser);
    }

    @Override
    public UserResponse getUserById(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    @Override
    public Page<UserResponse> searchUsers(String name, String surname, Pageable pageable) {
        var spec = Specification
                .where(UserSpecification.hasName(name))
                .and(UserSpecification.hasSurname(surname));
        return userRepository.findAll(spec, pageable).map(userMapper::toDto);
    }

    @Override
    @Transactional
    public UserResponse updateUser(UUID id, UserUpdateRequest dto) {
        var user = findUserByIdOrThrow(id);

        userMapper.updateUserFromDto(dto, user);

        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserResponse updateUserStatus(UUID id, UserStatus status) {
        var user = findUserByIdOrThrow(id);
        user.setStatus(status);
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        var user = findUserByIdOrThrow(id);
        user.setStatus(UserStatus.DEACTIVATED);
    }

    private User findUserByIdOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}
