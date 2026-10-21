package com.project.userservice;

import com.project.userservice.dto.request.UserCreateRequest;
import com.project.userservice.dto.request.UserUpdateRequest;
import com.project.userservice.dto.response.UserResponse;
import com.project.userservice.entity.User;
import com.project.userservice.exception.conflict.EmailAlreadyExistsException;
import com.project.userservice.exception.notfound.UserNotFoundException;
import com.project.userservice.repository.UserRepository;
import com.project.userservice.service.impl.UserServiceImpl;
import com.project.userservice.service.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.cache.CacheManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_shouldSuccessfullyCreateAndReturnUser() {
        UUID userId = UUID.randomUUID();
        LocalDate birthday = LocalDate.of(1995, 5, 12);
        UserCreateRequest request = new UserCreateRequest("Иван", "Иванов", "ivan@test.com", birthday);

        User userEntity = new User();
        User savedUser = new User();
        savedUser.setId(userId);

        UserResponse expectedResponse = new UserResponse(
                userId, "Иван", "Иванов", "ivan@test.com",
                true, birthday, List.of(), LocalDateTime.now(), LocalDateTime.now()
        );

        when(userRepository.existsByEmail("ivan@test.com")).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(expectedResponse);

        UserResponse actualResponse = userService.createUser(request);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.id()).isEqualTo(userId);
        verify(userRepository).existsByEmail("ivan@test.com");
        verify(userRepository).save(userEntity);
    }

    @Test
    void createUser_shouldThrowException_whenEmailAlreadyExists() {
        UserCreateRequest request = new UserCreateRequest("Петр", "Петров", "existing@test.com", LocalDate.of(1990, 1, 1));
        when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserById_shouldReturnUserResponse_whenUserExists() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        UserResponse expectedResponse = new UserResponse(
                userId, "Иван", "Иванов", "ivan@test.com",
                true, LocalDate.of(1995, 5, 12), List.of(), LocalDateTime.now(), LocalDateTime.now()
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(expectedResponse);

        UserResponse actualResponse = userService.getUserById(userId);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.id()).isEqualTo(userId);
    }

    @Test
    void getUserById_shouldThrowException_whenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void getUserByEmail_shouldReturnUserResponse_whenUserExists() {
        String email = "test@test.com";
        User user = new User();
        UserResponse expectedResponse = new UserResponse(
                UUID.randomUUID(), "Иван", "Иванов", email,
                true, LocalDate.of(1995, 5, 12), List.of(), LocalDateTime.now(), LocalDateTime.now()
        );

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(expectedResponse);

        UserResponse actualResponse = userService.getUserByEmail(email);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.email()).isEqualTo(email);
    }

    @Test
    void getUserByEmail_shouldThrowException_whenUserNotFound() {
        String email = "missing@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByEmail(email))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void searchUsers_shouldReturnPagedUserResponses() {
        String name = "Иван";
        String surname = "Иванов";
        Pageable pageable = PageRequest.of(0, 10);

        User user = new User();
        UUID userId = UUID.randomUUID();
        user.setId(userId);

        Page<User> userPage = new PageImpl<>(List.of(user), pageable, 1);
        UserResponse expectedResponse = new UserResponse(
                userId, name, surname, "ivan@test.com",
                true, LocalDate.of(1995, 5, 12), List.of(), LocalDateTime.now(), LocalDateTime.now()
        );

        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(userPage);
        when(userMapper.toDto(user)).thenReturn(expectedResponse);

        Page<UserResponse> resultPage = userService.searchUsers(name, surname, pageable);

        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getTotalElements()).isEqualTo(1);
        assertThat(resultPage.getContent().get(0).name()).isEqualTo(name);
        verify(userRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void updateUser_shouldUpdateAndReturnUser() {
        UUID userId = UUID.randomUUID();
        LocalDate birthday = LocalDate.of(1995, 5, 12);

        UserUpdateRequest request = new UserUpdateRequest(
                "НовоеИмя",
                "НоваяФамилия",
                "new@test.com",
                birthday
        );

        User user = new User();
        user.setId(userId);

        UserResponse expectedResponse = new UserResponse(
                userId,
                "НовоеИмя",
                "НоваяФамилия",
                "new@test.com",
                true,
                birthday,
                List.of(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userMapper).updateUserFromDto(request, user);
        when(userMapper.toDto(user)).thenReturn(expectedResponse);

        UserResponse actualResponse = userService.updateUser(userId, request);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.name()).isEqualTo("НовоеИмя");
        assertThat(actualResponse.surname()).isEqualTo("НоваяФамилия");
        verify(userMapper).updateUserFromDto(request, user);
    }

    @Test
    void updateUserActiveStatus_shouldUpdateStatusAndReturnUser() {
        UUID userId = UUID.randomUUID();
        boolean newActiveStatus = false;
        User user = new User();
        user.setId(userId);

        UserResponse expectedResponse = new UserResponse(
                userId, "Иван", "Иванов", "ivan@test.com",
                newActiveStatus, LocalDate.of(1995, 5, 12), List.of(), LocalDateTime.now(), LocalDateTime.now()
        );

        when(userRepository.updateUserActiveStatus(userId, newActiveStatus)).thenReturn(1);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(expectedResponse);

        UserResponse actualResponse = userService.updateUserActiveStatus(userId, newActiveStatus);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.active()).isEqualTo(newActiveStatus);
        verify(userRepository).updateUserActiveStatus(userId, newActiveStatus);
    }

    @Test
    void updateUserActiveStatus_shouldThrowException_whenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.updateUserActiveStatus(userId, false)).thenReturn(0);

        assertThatThrownBy(() -> userService.updateUserActiveStatus(userId, false))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository, never()).findById(any());
    }

    @Test
    void deleteUser_shouldDeleteUser_whenUserExists() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(userId);

        userService.deleteUser(userId);

        verify(userRepository).findById(userId);
        verify(userRepository).deleteById(userId);
    }
}