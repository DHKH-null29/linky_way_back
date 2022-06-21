package com.wnis.linkyway.controller;

import java.net.URI;
import java.util.List;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.wnis.linkyway.dto.Response;
import com.wnis.linkyway.dto.card.AddCardResponse;
import com.wnis.linkyway.dto.card.CardRequest;
import com.wnis.linkyway.dto.card.CardResponse;
import com.wnis.linkyway.dto.card.CopyPackageCardsRequest;
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
    @ApiOperation(value = "카드 생성", notes = "카드 요청 정보를 입력 받아 카드 하나를 생성한다")
    @Authenticated
    public ResponseEntity<Response> addCard(@CurrentMember Long memberId,
            @Validated(ValidationSequence.class) @RequestBody CardRequest cardRequest) {

        AddCardResponse addCardResponse = cardService.addCard(memberId, cardRequest);

        return ResponseEntity.created(URI.create("/card/" + addCardResponse))
                             .body(Response.builder()
                                           .code(HttpStatus.CREATED.value())
                                           .data(addCardResponse)
                                           .message("카드가 생성 완료")
                                           .build());
    }

    @GetMapping("/{cardId}")
    @ApiOperation(value = "ID를 통해 카드 조회", notes = "Card ID를 활용해 하나의 카드를 조회한다")
    @Authenticated
    public ResponseEntity<Response> findCardByCardId(@PathVariable Long cardId) {

        CardResponse cardResponse = cardService.findCardByCardId(cardId);

        return ResponseEntity.ok()
                             .body(Response.builder()
                                           .code(HttpStatus.OK.value())
                                           .data(cardResponse)
                                           .message("카드 조회 성공")
                                           .build());
    }

    @PutMapping("/{cardId}")
    @ApiOperation(value = "카드 수정", notes = "카드 수정 정보를 이용해 카드를 수정한다")
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
    @ApiOperation(value = "카드 완전 삭제", notes = "DB에서 카드를 완전히 삭제한다")
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
    @ApiOperation(value = "키워드를 통한 카드 조회", notes = "키워드를 활용해 여러 카드를 조회한다")
    @Authenticated
    public ResponseEntity<Response> searchCardByKeywordPersonalPage(@RequestParam(value = "keyword") String keyword,
            @CurrentMember Long memberId) {
        List<CardResponse> cardResponses = cardService.SearchCardByKeywordPersonalPage(keyword, memberId);
        return ResponseEntity.ok()
                             .body(Response.of(HttpStatus.OK, cardResponses, "조회 성공"));
    }

    @GetMapping("/tag/{tagId}")
    @ApiOperation(value = "태그아이디를 통해 카드 조회", notes = "TAG ID를 가지고 있는 여러 카드를 조회")
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
    @ApiOperation(value = "태그아이디를 통해 패키지 조회", notes = "태그 아이디를 활용해 여러 태그 아이디 조회")
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
    @ApiOperation(value = "폴더 아이디를 활용한 카드 조회", notes = "FOLDER ID에 속해있는 카드들 모두 조회")
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
    @ApiOperation(value = "회원 카드 조회", notes = "회원이 가지고 있는 모든 카드 조회")
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

    @PostMapping("/package/copy")
    @ApiOperation(value = "패키지 카드 복사", notes = "패키지에 있는 카드 모두 복사")
    @Authenticated
    public ResponseEntity<Response> copyCardsInPackage(
            @Validated(ValidationSequence.class) @RequestBody CopyPackageCardsRequest copyPackageCardsRequest) {

        int numOfSavedCards = cardService.copyCardsInPackage(copyPackageCardsRequest);
        return ResponseEntity.ok()
                             .body(Response.builder()
                                           .code(HttpStatus.OK.value())
                                           .message(numOfSavedCards + "개 카드 복사 성공")
                                           .data(numOfSavedCards)
                                           .build());
    }
}
