package com.project.userservice.service;

import com.project.userservice.dto.request.PaymentCardRequest;
import com.project.userservice.dto.response.PaymentCardResponse;
import com.project.userservice.entity.enums.PaymentCardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PaymentCardService {
    PaymentCardResponse addCardToUser(UUID userID, PaymentCardRequest dto);
    PaymentCardResponse getCardById(UUID id);
    List<PaymentCardResponse> getAllCardsByUserId(UUID userId);
    Page<PaymentCardResponse> getAllCards(Pageable pageable);
    PaymentCardResponse updateCardStatus(UUID id, PaymentCardStatus status);
    void deleteCard(UUID userId, UUID cardId);
}
