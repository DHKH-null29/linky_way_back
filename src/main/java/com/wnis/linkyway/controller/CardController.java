package com.wnis.linkyway.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.wnis.linkyway.dto.Response;
import com.wnis.linkyway.dto.card.AddCardResponse;
import com.wnis.linkyway.dto.card.CardRequest;
import com.wnis.linkyway.dto.card.CardResponse;
import com.wnis.linkyway.security.annotation.Authenticated;
import com.wnis.linkyway.security.annotation.CurrentMember;
import com.wnis.linkyway.service.card.CardService;
import com.wnis.linkyway.validation.ValidationSequence;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping
    @Authenticated
    public ResponseEntity<Response> addCard(@CurrentMember Long memberId,
            @Validated(ValidationSequence.class) @RequestBody CardRequest cardRequest) {

        AddCardResponse addCardResponse = cardService.addCard(memberId, cardRequest);

        return ResponseEntity.created(URI.create("/card/" + addCardResponse))
                             .body(Response.builder()
                                           .code(HttpStatus.CREATED.value())
                                           .data(addCardResponse)
                                           .message("카드가 생성되었습니다.")
                                           .build());
    }

    @GetMapping("/{cardId}")
    @Authenticated
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
    @Authenticated
    public ResponseEntity<Response> updateCard(@CurrentMember Long memberId,
            @PathVariable Long cardId,
            @Validated(ValidationSequence.class) @RequestBody CardRequest cardRequest) {

        cardService.updateCard(memberId, cardId, cardRequest);

        return ResponseEntity.ok()
                             .body(Response.builder()
                                           .code(HttpStatus.OK.value())
                                           .message("카드 변경 완료")
                                           .build());
    }

    @DeleteMapping("/{cardId}")
    @Authenticated
    public ResponseEntity<Response> deleteCard(@PathVariable Long cardId) {

        cardService.deleteCard(cardId);

        return ResponseEntity.ok()
                             .body(Response.builder()
                                           .code(HttpStatus.OK.value())
                                           .message("카드 삭제 완료")
                                           .build());
    }

    @GetMapping("/personal/keyword")
    @Authenticated
    public ResponseEntity<Response> searchCardByKeywordPersonalPage(@RequestParam(value = "keyword") String keyword,
            @CurrentMember Long memberId) {
        List<CardResponse> cardResponses = cardService.SearchCardByKeywordpersonalPage(keyword, memberId);
        return ResponseEntity.ok()
                             .body(Response.of(HttpStatus.OK, cardResponses, "조회 성공"));
    }

    @GetMapping("/tag/{tagId}")
    @Authenticated
    public ResponseEntity<Response> findCardsByTagId(@CurrentMember Long memberId, @PathVariable Long tagId) {

        List<CardResponse> cardResponses = cardService.findCardsByTagId(memberId, tagId);
        return ResponseEntity.ok()
                             .body(Response.builder()
                                           .code(HttpStatus.OK.value())
                                           .message("태그에 해당하는 카드 조회 성공")
                                           .data(cardResponses)
                                           .build());
    }

    @GetMapping("/package/{tagId}")
    @Authenticated
    public ResponseEntity<Response> findShareableCardsByTagId(@PathVariable Long tagId) {

        List<CardResponse> cardResponses = cardService.findShareableCardsByTagId(tagId);
        return ResponseEntity.ok()
                             .body(Response.builder()
                                           .code(HttpStatus.OK.value())
                                           .message("태그에 해당하는 카드 조회 성공")
                                           .data(cardResponses)
                                           .build());
    }

    @GetMapping("/folder/{folderId}")
    @Authenticated
    public ResponseEntity<Response> findCardsByFolderId(@CurrentMember Long memberId,
            @PathVariable Long folderId,
            @RequestParam boolean findDeep) {

        List<CardResponse> cardResponses = cardService.findCardsByFolderId(memberId, folderId, findDeep);
        return ResponseEntity.ok()
                             .body(Response.builder()
                                           .code(HttpStatus.OK.value())
                                           .message("폴더에 해당하는 카드 조회 성공")
                                           .data(cardResponses)
                                           .build());
    }

    @GetMapping("/all")
    @Authenticated
    public ResponseEntity<Response> findCardsByMemberId(@CurrentMember Long memberId) {

        List<CardResponse> cardResponses = cardService.findCardsByMemberId(memberId);
        return ResponseEntity.ok()
                             .body(Response.builder()
                                           .code(HttpStatus.OK.value())
                                           .message("태그에 해당하는 카드 조회 성공")
                                           .data(cardResponses)
                                           .build());
    }
}
