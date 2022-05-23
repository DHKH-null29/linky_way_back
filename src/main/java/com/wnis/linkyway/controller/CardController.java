package com.wnis.linkyway.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wnis.linkyway.dto.Response;
import com.wnis.linkyway.dto.card.CardRequest;
import com.wnis.linkyway.service.CardService;
import com.wnis.linkyway.validation.ValidationSequence;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping
    public ResponseEntity<Response> addNewCard(
            @Validated(ValidationSequence.class) @RequestBody CardRequest cardRequest) {

        Long cardId = cardService.addCard(cardRequest);

        return ResponseEntity.created(URI.create("/card/" + cardId))
                             .body(Response.builder()
                                           .code(HttpStatus.CREATED.value())
                                           .data(cardId)
                                           .build());
    }
}
