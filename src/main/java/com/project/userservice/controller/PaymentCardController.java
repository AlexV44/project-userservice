package com.project.userservice.controller;

import com.project.userservice.dto.request.PaymentCardRequest;
import com.project.userservice.dto.response.PaymentCardResponse;
import com.project.userservice.dto.response.UserResponse;
import com.project.userservice.service.PaymentCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
public class PaymentCardController {
    private final PaymentCardService cardService;

    @PostMapping("/user/{userId}")
    public ResponseEntity<PaymentCardResponse> addCardToUser(
            @PathVariable UUID userId,
            @Valid @RequestBody PaymentCardRequest request) {
        PaymentCardResponse savedCard = cardService.addCardToUser(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCard);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentCardResponse> getCardById(@PathVariable UUID id) {
        PaymentCardResponse card = cardService.getCardById(id);
        return ResponseEntity.ok(card);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentCardResponse>> getAllCardsByUserId(@PathVariable UUID userId) {
        List<PaymentCardResponse> response = cardService.getAllCardsByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<PaymentCardResponse>> getAllCards(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Page<PaymentCardResponse> cardsPage = cardService.getAllCards(pageable);
        return ResponseEntity.ok(cardsPage);
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<PaymentCardResponse> updateUserActiveStatus(
            @PathVariable UUID id,
            @RequestParam boolean active) {
        PaymentCardResponse updatedCard = cardService.updateCardActiveStatus(id, active);
        return ResponseEntity.ok(updatedCard);
    }

    @DeleteMapping("/user/{userId}/{cardId}")
    public ResponseEntity<Void> deleteCard(
            @PathVariable UUID userId,
            @PathVariable UUID cardId) {
        cardService.deleteCard(userId, cardId);
        return ResponseEntity.noContent().build();
    }
}
