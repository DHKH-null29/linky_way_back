package com.wnis.linkyway.controller;

import com.wnis.linkyway.dto.Page;
import com.wnis.linkyway.dto.Response;
import com.wnis.linkyway.dto.card.io.AddCardResponse;
import com.wnis.linkyway.dto.card.io.CardRequest;
import com.wnis.linkyway.dto.card.io.CardResponse;
import com.wnis.linkyway.dto.card.io.CopyPackageCardsRequest;
import com.wnis.linkyway.security.annotation.Authenticated;
import com.wnis.linkyway.security.annotation.CurrentMember;
import com.wnis.linkyway.service.card.CardService;
import com.wnis.linkyway.validation.ValidationSequence;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping
    @ApiOperation(value = "카드 생성", notes = "카드 요청 정보를 입력 받아 카드 하나를 생성한다")
    @Authenticated
    public ResponseEntity<Response<AddCardResponse>> addCard(@CurrentMember Long memberId,
        @Validated(ValidationSequence.class) @RequestBody CardRequest cardRequest) {

        AddCardResponse addCardResponse = cardService.addCard(memberId, cardRequest);

        return ResponseEntity.ok()
            .body(Response.of(HttpStatus.OK, addCardResponse, "카드가 생성 완료"));
    }

    @GetMapping("/{cardId}")
    @ApiOperation(value = "ID를 통해 카드 조회", notes = "Card ID를 활용해 하나의 카드를 조회한다")
    @Authenticated
    public ResponseEntity<Response<CardResponse>> findCardByCardId(@PathVariable Long cardId,
        @CurrentMember Long memberId) {

        CardResponse cardResponse = cardService.findCardByCardId(cardId, memberId);

        return ResponseEntity.ok()
            .body(Response.of(HttpStatus.OK, cardResponse, "카드 조회 성공"));
    }

    @PutMapping("/{cardId}")
    @ApiOperation(value = "카드 수정", notes = "카드 수정 정보를 이용해 카드를 수정한다")
    @Authenticated
    public ResponseEntity<Response<Long>> updateCard(@CurrentMember Long memberId,
        @PathVariable Long cardId,
        @Validated(ValidationSequence.class) @RequestBody CardRequest cardRequest) {

        Long updatedCardId = cardService.updateCard(memberId, cardId, cardRequest);

        return ResponseEntity.ok()
            .body(Response.of(HttpStatus.OK, updatedCardId, "카드 변경 완료"));
    }

    @DeleteMapping("/{cardId}")
    @ApiOperation(value = "카드 완전 삭제", notes = "카드 하나를 휴지통으로 보낸다")
    @Authenticated
    public ResponseEntity<Response<Long>> deleteCard(@PathVariable Long cardId,
        @CurrentMember Long memberId) {

        Long response = cardService.deleteCard(cardId, memberId);

        return ResponseEntity.ok()
            .body(Response.of(HttpStatus.OK, response, "카드 삭제 완료"));
    }

    @GetMapping("/personal/keyword")
    @ApiOperation(value = "키워드를 통한 카드 조회", notes = "키워드를 활용해 여러 카드를 조회한다")
    @Authenticated
    public ResponseEntity<Response<Page<CardResponse>>> searchCardByKeywordPersonalPage(
        @RequestParam(value = "lastIdx", required = false) Long lastIdx,
        @RequestParam(value = "keyword") String keyword,
        @CurrentMember Long memberId, Pageable pageable) {
        Page<CardResponse> cardResponses = cardService.SearchCardByKeywordPersonalPage(lastIdx,
            keyword, memberId, pageable);
        return ResponseEntity.ok()
            .body(Response.of(HttpStatus.OK, cardResponses, "키워드 조회 성공"));
    }

    @GetMapping("/tag/{tagId}")
    @ApiOperation(value = "태그아이디를 통해 카드 조회", notes = "TAG ID를 가지고 있는 여러 카드를 조회")
    @Authenticated
    public ResponseEntity<Response<Page<CardResponse>>> findCardsByTagId(
        @RequestParam(value = "lastIdx", required = false) Long lastIdx,
        @CurrentMember Long memberId, @PathVariable Long tagId) {

        Page<CardResponse> cardResponses = cardService.findCardsByTagId(lastIdx, memberId, tagId,
            PageRequest.of(0, 200));
        return ResponseEntity.ok()
            .body(Response.of(HttpStatus.OK, cardResponses, "태그 조회 성공"));
    }


    @GetMapping("/folder/{folderId}")
    @ApiOperation(value = "폴더 아이디를 활용한 카드 조회", notes = "폴더 ID에 속해있는 카드들 모두 조회")
    @Authenticated
    public ResponseEntity<Response<Page<CardResponse>>> findCardsByFolderId(
        @RequestParam(value = "lastIdx", required = false) Long lastIdx,
        @CurrentMember Long memberId,
        @PathVariable Long folderId,
        @RequestParam boolean findDeep, Pageable pageable) {

        Page<CardResponse> cardResponses = cardService.findCardsByFolderId(lastIdx, memberId,
            folderId,
            findDeep, pageable);
        return ResponseEntity.ok()
            .body(Response.of(HttpStatus.OK, cardResponses, "폴더에 해당하는 카드 조회 성공"));
    }

    @GetMapping("/all")
    @ApiOperation(value = "회원 카드 조회", notes = "회원이 가지고 있는 모든 카드 조회")
    @Authenticated
    public ResponseEntity<Response<Page<CardResponse>>> findCardsByMemberId(
        @RequestParam(value = "lastIdx", required = false) Long lastIdx,
        @CurrentMember Long memberId,
        Pageable pageable) {

        Page<CardResponse> cardResponses = cardService.findCardsByMemberId(lastIdx, memberId,
            pageable);
        return ResponseEntity.ok()
            .body(Response.of(HttpStatus.OK, cardResponses, "회원이 가지고 있는 카드 조회 성공"));
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
