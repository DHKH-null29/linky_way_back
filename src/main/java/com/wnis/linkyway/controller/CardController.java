package com.wnis.linkyway.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wnis.linkyway.dto.Response;
import com.wnis.linkyway.dto.card.AddCardResponse;
import com.wnis.linkyway.dto.card.CardRequest;
import com.wnis.linkyway.dto.card.CardResponse;
import com.wnis.linkyway.service.card.CardService;
import com.wnis.linkyway.validation.ValidationSequence;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping
    public ResponseEntity<Response> addCard(
            @Validated(ValidationSequence.class) @RequestBody CardRequest cardRequest) {
        
        AddCardResponse addCardResponse = cardService.addCard(cardRequest);

        return ResponseEntity.created(URI.create("/card/" + addCardResponse))
                             .body(Response.builder()
                                           .code(HttpStatus.CREATED.value())
                                           .data(addCardResponse)
                                           .message("카드가 생성되었습니다.")
                                           .build());
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<Response> findCardByCardId(Long cardId) {

        CardResponse cardResponse = cardService.findCardByCardId(cardId);

        return ResponseEntity.ok()
                             .body(Response.builder()
                                           .code(HttpStatus.OK.value())
                                           .data(cardResponse)
                                           .message("카드를 찾았습니다.")
                                           .build());
    }

    @PutMapping("/{cardId}")
    public ResponseEntity<Response> updateCard(@PathVariable Long cardId,
            @Validated(ValidationSequence.class) @RequestBody CardRequest cardRequest) {
        
        cardService.updateCard(cardId, cardRequest);
        
        return ResponseEntity.ok()
                .body(Response.builder()
                        .code(HttpStatus.OK.value())
                        .message("카드 변경 완료")
                        .build());
    }
    
    @DeleteMapping("/{cardId}")
    public ResponseEntity<Response> deleteCard(@PathVariable Long cardId) {
        
        cardService.deleteCard(cardId);
        
        return ResponseEntity.ok()
                .body(Response.builder()
                        .code(HttpStatus.OK.value())
                        .message("카드 삭제 완료")
                        .build());
    }
}
