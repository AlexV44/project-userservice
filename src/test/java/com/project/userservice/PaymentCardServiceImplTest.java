package com.project.userservice;

import com.project.userservice.dto.request.PaymentCardRequest;
import com.project.userservice.dto.response.PaymentCardResponse;
import com.project.userservice.entity.PaymentCard;
import com.project.userservice.entity.User;
import com.project.userservice.exception.limit.CardLimitExceededException;
import com.project.userservice.exception.notfound.PaymentCardNotFoundException;
import com.project.userservice.exception.notfound.UserNotFoundException;
import com.project.userservice.repository.PaymentCardRepository;
import com.project.userservice.repository.UserRepository;
import com.project.userservice.service.impl.PaymentCardServiceImpl;
import com.project.userservice.service.mapper.PaymentCardMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentCardServiceImplTest {

    @Mock
    private PaymentCardRepository paymentCardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentCardMapper paymentCardMapper;

    @InjectMocks
    private PaymentCardServiceImpl paymentCardService;

    @Test
    void addCardToUser_shouldSuccessfullyAddAndReturnCard() {
        UUID userId = UUID.randomUUID();
        LocalDate expDate = LocalDate.of(2028, 12, 31);
        PaymentCardRequest request = new PaymentCardRequest("1234567812345678", "IVAN IVANOV", expDate);

        User user = new User();
        PaymentCard card = new PaymentCard();
        PaymentCard savedCard = new PaymentCard();
        PaymentCardResponse expectedResponse = new PaymentCardResponse(
                UUID.randomUUID(), "IVAN IVANOV", expDate, true, LocalDateTime.now(), LocalDateTime.now()
        );

        when(paymentCardRepository.countByUserId(userId)).thenReturn(3L);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(paymentCardMapper.toEntity(request)).thenReturn(card);
        when(paymentCardRepository.save(card)).thenReturn(savedCard);
        when(paymentCardMapper.toDto(savedCard)).thenReturn(expectedResponse);

        PaymentCardResponse actualResponse = paymentCardService.addCardToUser(userId, request);

        assertThat(actualResponse).isNotNull();
        verify(paymentCardRepository).countByUserId(userId);
        verify(userRepository).findById(userId);
        verify(paymentCardRepository).save(card);
    }

    @Test
    void addCardToUser_shouldThrowException_whenCardLimitExceeded() {
        UUID userId = UUID.randomUUID();
        PaymentCardRequest request = new PaymentCardRequest("1234567812345678", "IVAN IVANOV", LocalDate.of(2028, 12, 31));

        when(paymentCardRepository.countByUserId(userId)).thenReturn(6L);

        assertThatThrownBy(() -> paymentCardService.addCardToUser(userId, request))
                .isInstanceOf(CardLimitExceededException.class);

        verify(paymentCardRepository, never()).save(any());
    }

    @Test
    void addCardToUser_shouldThrowException_whenUserNotFound() {
        UUID userId = UUID.randomUUID();
        PaymentCardRequest request = new PaymentCardRequest("1234567812345678", "IVAN IVANOV", LocalDate.of(2028, 12, 31));

        when(paymentCardRepository.countByUserId(userId)).thenReturn(2L);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentCardService.addCardToUser(userId, request))
                .isInstanceOf(UserNotFoundException.class);

        verify(paymentCardRepository, never()).save(any());
    }

    @Test
    void getCardById_shouldReturnCardResponse_whenCardExists() {
        UUID cardId = UUID.randomUUID();
        PaymentCard card = new PaymentCard();
        PaymentCardResponse expectedResponse = new PaymentCardResponse(
                cardId, "IVAN IVANOV", LocalDate.of(2028, 12, 31), true, LocalDateTime.now(), LocalDateTime.now()
        );

        when(paymentCardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(paymentCardMapper.toDto(card)).thenReturn(expectedResponse);

        PaymentCardResponse actualResponse = paymentCardService.getCardById(cardId);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.id()).isEqualTo(cardId);
    }

    @Test
    void getCardById_shouldThrowException_whenCardNotFound() {
        UUID cardId = UUID.randomUUID();
        when(paymentCardRepository.findById(cardId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentCardService.getCardById(cardId))
                .isInstanceOf(PaymentCardNotFoundException.class);
    }

    @Test
    void getAllCardsByUserId_shouldReturnListOfCards_whenUserExists() {
        UUID userId = UUID.randomUUID();
        PaymentCard card = new PaymentCard();
        PaymentCardResponse expectedResponse = new PaymentCardResponse(
                UUID.randomUUID(), "IVAN IVANOV", LocalDate.of(2028, 12, 31), true, LocalDateTime.now(), LocalDateTime.now()
        );

        when(userRepository.existsById(userId)).thenReturn(true);
        when(paymentCardRepository.findAllByUserId(userId)).thenReturn(List.of(card));
        when(paymentCardMapper.toDto(card)).thenReturn(expectedResponse);

        List<PaymentCardResponse> result = paymentCardService.getAllCardsByUserId(userId);

        assertThat(result).hasSize(1);
        verify(paymentCardRepository).findAllByUserId(userId);
    }

    @Test
    void getAllCardsByUserId_shouldThrowException_whenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> paymentCardService.getAllCardsByUserId(userId))
                .isInstanceOf(UserNotFoundException.class);

        verify(paymentCardRepository, never()).findAllByUserId(any());
    }

    @Test
    void getAllCards_shouldReturnPagedCardResponses() {
        Pageable pageable = PageRequest.of(0, 10);
        PaymentCard card = new PaymentCard();
        Page<PaymentCard> cardPage = new PageImpl<>(List.of(card), pageable, 1);
        PaymentCardResponse expectedResponse = new PaymentCardResponse(
                UUID.randomUUID(), "IVAN IVANOV", LocalDate.of(2028, 12, 31), true, LocalDateTime.now(), LocalDateTime.now()
        );

        when(paymentCardRepository.findAll(pageable)).thenReturn(cardPage);
        when(paymentCardMapper.toDto(card)).thenReturn(expectedResponse);

        Page<PaymentCardResponse> resultPage = paymentCardService.getAllCards(pageable);

        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getTotalElements()).isEqualTo(1);
        verify(paymentCardRepository).findAll(pageable);
    }

    @Test
    void updateCardActiveStatus_shouldUpdateAndReturnCard() {
        UUID cardId = UUID.randomUUID();
        PaymentCard card = new PaymentCard();
        PaymentCardResponse expectedResponse = new PaymentCardResponse(
                cardId, "IVAN IVANOV", LocalDate.of(2028, 12, 31), false, LocalDateTime.now(), LocalDateTime.now()
        );

        when(paymentCardRepository.updateCardActiveStatus(cardId, false)).thenReturn(1);
        when(paymentCardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(paymentCardMapper.toDto(card)).thenReturn(expectedResponse);

        PaymentCardResponse actualResponse = paymentCardService.updateCardActiveStatus(cardId, false);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse.active()).isFalse();
        verify(paymentCardRepository).updateCardActiveStatus(cardId, false);
    }

    @Test
    void updateCardActiveStatus_shouldThrowException_whenCardNotFound() {
        UUID cardId = UUID.randomUUID();
        when(paymentCardRepository.updateCardActiveStatus(cardId, true)).thenReturn(0);

        assertThatThrownBy(() -> paymentCardService.updateCardActiveStatus(cardId, true))
                .isInstanceOf(PaymentCardNotFoundException.class);
    }

    @Test
    void deleteCard_shouldSuccessfullyDeleteCard() {
        UUID userId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        PaymentCard card = new PaymentCard();
        User user = new User();

        when(paymentCardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(paymentCardRepository).delete(card);

        paymentCardService.deleteCard(userId, cardId);

        verify(paymentCardRepository).findById(cardId);
        verify(userRepository).findById(userId);
        verify(paymentCardRepository).delete(card);
    }

    @Test
    void deleteCard_shouldThrowException_whenUserNotFound() {
        UUID userId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        PaymentCard card = new PaymentCard();

        when(paymentCardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentCardService.deleteCard(userId, cardId))
                .isInstanceOf(UserNotFoundException.class);

        verify(paymentCardRepository, never()).delete(any());
    }
}