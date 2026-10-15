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
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CacheManager cacheManager;

    @Override
    @Transactional
    @CachePut(value = "users", key = "#result.id")
    public UserResponse createUser(UserCreateRequest dto) {
        if(userRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyExistsException(dto.email());
        }

        var user = userMapper.toEntity(dto);
        user.setActive(true);

        var savedUser = userRepository.save(userMapper.toEntity(dto));

        return userMapper.toDto(savedUser);
    }

    @Override
    @Cacheable(value = "users", key = "#id")
    public UserResponse getUserById(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    @Cacheable(value = "users", key = "#email")
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
    @CachePut(value = "users", key = "#id")
    public UserResponse updateUser(UUID id, UserUpdateRequest dto) {
        var user = findUserByIdOrThrow(id);

        String oldEmail = user.getEmail();

        userMapper.updateUserFromDto(dto, user);

        if (oldEmail != null && !oldEmail.equals(user.getEmail())) {
            Optional.ofNullable(cacheManager.getCache("users"))
                    .ifPresent(cache -> cache.evict(oldEmail));
        }

        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    @CachePut(value = "users", key = "#id")
    public UserResponse updateUserActiveStatus(UUID id, boolean active) {
        int updatedCount = userRepository.updateUserActiveStatus(id, active);

        if (updatedCount == 0) {
            throw new UserNotFoundException(id);
        }

        var user = findUserByIdOrThrow(id);

        Optional.ofNullable(cacheManager.getCache("users"))
                .ifPresent(cache -> cache.evict(user.getEmail()));

        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(UUID id) {
        var user = findUserByIdOrThrow(id);

        String email = user.getEmail();

        userRepository.deleteById(user.getId());

        Optional.ofNullable(cacheManager.getCache("users"))
                .ifPresent(cache -> cache.evict(email));
    }

    private User findUserByIdOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}
