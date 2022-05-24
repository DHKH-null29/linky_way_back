package com.wnis.linkyway.controller;

import com.wnis.linkyway.dto.Response;
import com.wnis.linkyway.dto.member.*;
import com.wnis.linkyway.security.annotation.Authenticated;
import com.wnis.linkyway.security.annotation.CurrentMember;
import com.wnis.linkyway.service.MemberService;
import com.wnis.linkyway.validation.ValidationSequence;
import lombok.RequiredArgsConstructor;
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
        Response<MemberResponse> response = memberService.join(joinRequest);
        return ResponseEntity.status(response.getCode()).body(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok().build();
    }
    
    
    @GetMapping("/email")
    public ResponseEntity<Response> searchEmail(@RequestParam String email) {
        Response<MemberResponse> response = memberService.searchEmail(email);
        return ResponseEntity.status(response.getCode()).body(response);
    }
    
    @GetMapping("/mypage")
    @Authenticated
    public ResponseEntity<Response> searchMyPage(@CurrentMember Long memberId) {
        Response<MemberResponse> response = memberService.searchMyPage(memberId);
        return ResponseEntity.status(response.getCode()).body(response);
    }
    
    @PutMapping("/password")
    @Authenticated
    public ResponseEntity<Response> setPassword(
            @Validated(ValidationSequence.class) @RequestBody PasswordRequest passwordRequest,
            @CurrentMember Long memberId) {
        Response<MemberResponse> response = memberService.setPassword(passwordRequest, memberId);
        return ResponseEntity.status(response.getCode()).body(response);
    }
    
    @DeleteMapping
    @Authenticated
    public ResponseEntity<Response> deleteMember(@CurrentMember Long memberId) {
        Response<MemberResponse> response = memberService.deleteMember(memberId);
        return ResponseEntity.status(response.getCode()).body(response);
    }
    
}
