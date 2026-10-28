package com.project.userservice;

import com.project.userservice.dto.request.PaymentCardRequest;
import com.project.userservice.dto.request.UserCreateRequest;
import com.project.userservice.dto.response.PaymentCardResponse;
import com.project.userservice.dto.response.UserResponse;
import com.project.userservice.repository.PaymentCardRepository;
import com.project.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@Import(TestcontainersConfiguration.class)
public class UserIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentCardRepository paymentCardRepository;

    @BeforeEach
    void setUp() {
        paymentCardRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldExecuteFullUserJourneySuccessfully() {
        UserCreateRequest createUserRequest = new UserCreateRequest(
                "Алексей",
                "Смирнов",
                "alex.journey@test.com",
                LocalDate.of(1992, 10, 15)
        );

        ResponseEntity<UserResponse> createResponse = restTemplate.postForEntity(
                "/api/v1/users",
        createUserRequest,
                UserResponse.class
        );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        UserResponse createdUser = createResponse.getBody();
        assertThat(createdUser).isNotNull();
        UUID userId = createdUser.id();
        assertThat(userId).isNotNull();
        assertThat(createdUser.email()).isEqualTo("alex.journey@test.com");

        PaymentCardRequest createCardRequest = new PaymentCardRequest(
                "4200111122223333",
                "ALEXEY SMIRNOV",
                LocalDate.of(2028, 12, 31)
        );

        ResponseEntity<PaymentCardResponse> cardResponse = restTemplate.postForEntity(
                "/api/v1/cards/user/" + userId,
        createCardRequest,
                PaymentCardResponse.class
        );

        assertThat(cardResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        PaymentCardResponse createdCard = cardResponse.getBody();
        assertThat(createdCard).isNotNull();
        UUID cardId = createdCard.id();
        assertThat(cardId).isNotNull();
        assertThat(createdCard.active()).isTrue();

        ResponseEntity<UserResponse> getProfileResponse = restTemplate.getForEntity(
                "/api/v1/users/" + userId,
        UserResponse.class
        );

        assertThat(getProfileResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        UserResponse profile = getProfileResponse.getBody();
        assertThat(profile).isNotNull();
        assertThat(profile.paymentCards()).hasSize(1);
        assertThat(profile.paymentCards().get(0).id()).isEqualTo(cardId);

        ResponseEntity<PaymentCardResponse> updateCardResponse = restTemplate.exchange(
                "/api/v1/cards/" + cardId + "/active?active=false",
        HttpMethod.PATCH,
                HttpEntity.EMPTY,
                PaymentCardResponse.class
        );

        assertThat(updateCardResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateCardResponse.getBody()).isNotNull();
        assertThat(updateCardResponse.getBody().active()).isFalse();


        restTemplate.delete("/api/v1/cards/user/" + userId + "/" + cardId);
        assertThat(paymentCardRepository.findAll()).isEmpty();

        restTemplate.delete("/api/v1/users/" + userId);
        assertThat(userRepository.findAll()).isEmpty();
    }
}