package com.wnis.linkyway.controller;

import com.wnis.linkyway.dto.Response;
import com.wnis.linkyway.dto.member.JoinRequest;
import com.wnis.linkyway.dto.member.JoinResponse;
import com.wnis.linkyway.dto.member.LoginRequest;
import com.wnis.linkyway.service.MemberService;
import com.wnis.linkyway.validation.ValidationSequence;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<Response<Long>> join(
            @Validated(ValidationSequence.class) @RequestBody JoinRequest joinRequest) {
        return ResponseEntity.ok(Response.of(HttpStatus.OK, memberService.join(joinRequest), "회원가입 성공"));
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok().build();
    }

}
