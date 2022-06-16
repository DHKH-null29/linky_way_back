package com.wnis.linkyway.controller;

import com.wnis.linkyway.dto.Response;
import com.wnis.linkyway.dto.member.*;
import com.wnis.linkyway.security.annotation.Authenticated;
import com.wnis.linkyway.security.annotation.CurrentMember;
import com.wnis.linkyway.service.MemberService;
import com.wnis.linkyway.validation.ValidationSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {
    
    private final MemberService memberService;
    
    @PostMapping
    public ResponseEntity<Response> join(
            @Validated(ValidationSequence.class) @RequestBody JoinRequest joinRequest) {
        MemberResponse response = memberService.join(joinRequest);
        return ResponseEntity.ok(Response.of(HttpStatus.OK, response, "회원가입 성공"));
    }
    
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok().build();
    }
    
    
    @GetMapping("/email")
    public ResponseEntity<Response> searchEmail(@RequestParam String email) {
        MemberResponse response = memberService.searchEmail(email);
        return ResponseEntity.ok(Response.of(HttpStatus.OK, response, "이메일 조회 성공"));
    }

    @GetMapping("/nickname")
    public ResponseEntity<Response<DuplicationResponse>> searchNicknameDuplicationInfo(@RequestParam String nickname) {
        DuplicationResponse response = memberService.isValidNickname(nickname);
        return  ResponseEntity.ok(Response.of(HttpStatus.OK, response, "닉네임 사용가능 여부 조회 성공"));
    }
    
    @GetMapping("/page/me")
    @Authenticated
    public ResponseEntity<Response> searchMyPage(@CurrentMember Long memberId) {
        MemberResponse response = memberService.searchMyPage(memberId);
        return ResponseEntity.ok(Response.of(HttpStatus.OK, response, "마이 페이지 조회"));
    }
    
    @PutMapping("/page/me")
    @Authenticated
    public ResponseEntity<Response> updateMyPage(@CurrentMember Long memberId) {
        MemberResponse response = memberService.searchMyPage(memberId);
        return ResponseEntity.ok(Response.of(HttpStatus.OK, response, "마이 페이지 조회"));
    }
    
    @PutMapping("/password")
    @Authenticated
    public ResponseEntity<Response> updatePassword(
            @Validated(ValidationSequence.class) @RequestBody PasswordRequest passwordRequest,
            @CurrentMember Long memberId) {
        MemberResponse response = memberService.updatePassword(passwordRequest, memberId);
        return ResponseEntity.ok(Response.of(HttpStatus.OK, response, "패스워드 변경 성공"));
    }
    
    @DeleteMapping
    @Authenticated
    public ResponseEntity<Response> deleteMember(@CurrentMember Long memberId) {
        MemberResponse response = memberService.deleteMember(memberId);
        return ResponseEntity.ok(Response.of(HttpStatus.OK, response, "멤버 삭제 성공"));
    }
    
}
