package com.wnis.linkyway.controller;

import com.wnis.linkyway.dto.Response;
import com.wnis.linkyway.dto.card.CardResponse;
import com.wnis.linkyway.security.annotation.Authenticated;
import com.wnis.linkyway.security.annotation.CurrentMember;
import com.wnis.linkyway.service.TrashService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trash")
@RequiredArgsConstructor
public class TrashController {
    
    private final TrashService trashService;
    
    @PutMapping
    @ApiOperation(value = "삭제 & 복원", notes = "isDeleted가 true이면 삭제, isDeleted가 false이면 복원이 된다 List 형태로 card id를 받는다")
    @Authenticated
    ResponseEntity<Response<List<Long>>> updateCardIsDeletedTrueOrFalse(@RequestBody List<Long> ids, @CurrentMember Long memberId,
            @RequestParam Boolean isDeleted) {
        List<Long> response = null;
        
        if (isDeleted == true) {
            response = trashService.deleteCompletely(ids, memberId);
        } else { //isDeleted == false
            response = trashService.updateDeleteCardFalse(ids, memberId);
        }
        
        String message = isDeleted ? "삭제 성공" : "복원 성공";
        return ResponseEntity.ok(Response.of(HttpStatus.OK, response, message));
    }
    
    @GetMapping
    @ApiOperation(value = "삭제된 카드 조회", notes = "isDeleted가 true인 카드들을 조회한다")
    @Authenticated
    ResponseEntity<Response<List<CardResponse>>> findAllDeletedCard(@CurrentMember Long memberId,
            @RequestParam(value = "lastCardId", required = false) Long lastCardId, Pageable pageable) {
        List<CardResponse> response = trashService.findAllDeletedCard(memberId,lastCardId, pageable);
        return ResponseEntity.ok(Response.of(HttpStatus.OK, response, "삭제된 카드 조회 성공"));
    }
}
