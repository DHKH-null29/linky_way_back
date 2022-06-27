package com.wnis.linkyway.controller;

import com.wnis.linkyway.aop.WithEmailVerification;
import com.wnis.linkyway.dto.Response;
import com.wnis.linkyway.dto.member.*;
import com.wnis.linkyway.security.annotation.Authenticated;
import com.wnis.linkyway.security.annotation.CurrentMember;
import com.wnis.linkyway.service.MemberService;
import com.wnis.linkyway.validation.ValidationSequence;
import io.swagger.annotations.ApiOperation;
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
    @ApiOperation(value = "회원가입", notes = "회원정보를 입력해 회원가입")
    @WithEmailVerification
    public ResponseEntity<Response> join(
            @Validated(ValidationSequence.class) @RequestBody JoinRequest joinRequest) {
        MemberResponse response = memberService.join(joinRequest);
        return ResponseEntity.ok(Response.of(HttpStatus.OK, response, "회원가입 성공"));
    }
    
    @PostMapping("/login")
    @ApiOperation(value = "로그인", notes = "로그인 이후 JWT 토큰 리턴")
    public ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok().build();
    }
    
    
    @GetMapping("/email")
    @ApiOperation(value = "이메일 조회", notes = "해당 이메일이 실제 등록되어 있는지 조회한다")
    public ResponseEntity<Response> searchEmail(@RequestParam String email) {
        MemberResponse response = memberService.searchEmail(email);
        return ResponseEntity.ok(Response.of(HttpStatus.OK, response, "이메일 조회 성공"));
    }

    @GetMapping("/nickname")
    @ApiOperation(value = "닉네임 조회", notes = "닉네임의 중복 여부를 확인하기 위한 조회한다")
    public ResponseEntity<Response<DuplicationResponse>> searchNicknameDuplicationInfo(@RequestParam String nickname) {
        DuplicationResponse response = memberService.isValidNickname(nickname);
        return  ResponseEntity.ok(Response.of(HttpStatus.OK, response, "닉네임 사용가능 여부 조회 성공"));
    }
    
    @GetMapping("/page/me")
    @ApiOperation(value = "회원 정보 조회", notes = "가지고 있는 토큰 정보를 활용해 회원 정보 조회한다")
    @Authenticated
    public ResponseEntity<Response> searchMyPage(@CurrentMember Long memberId) {
        MemberResponse response = memberService.searchMyPage(memberId);
        return ResponseEntity.ok(Response.of(HttpStatus.OK, response, "마이 페이지 조회 성공"));
    }
    

    @PutMapping("/page/me")
    @ApiOperation(value = "회원 정보 수정", notes = "회원 정보를 수정한다")
    @Authenticated
    public ResponseEntity<Response> updateMyPage(
            @Validated(ValidationSequence.class) @RequestBody UpdateMemberRequest updateMemberRequest,
            @CurrentMember Long memberId) {
        MemberResponse response = memberService.updateMyPage(updateMemberRequest, memberId);
        return ResponseEntity.ok(Response.of(HttpStatus.OK, response, "마이 페이지 수정 성공"));
    }
    
    @PutMapping("/password")
    @ApiOperation(value = "회원 비밀번호 수정", notes = "회원 비밀번호를 수정한다")
    @Authenticated
    public ResponseEntity<Response> updatePassword(
            @Validated(ValidationSequence.class) @RequestBody PasswordRequest passwordRequest,
            @CurrentMember Long memberId) {
        MemberResponse response = memberService.updatePassword(passwordRequest, memberId);
        return ResponseEntity.ok(Response.of(HttpStatus.OK, response, "패스워드 변경 성공"));
    }

    @PutMapping("/password/noauth")
    @ApiOperation(value = "비인증 회원 비밀번호 수정", notes = "비밀번호를 잊은 사용자의 비밀번호를 수정한다")
    @WithEmailVerification
    public ResponseEntity<Response<Object>> updatePasswordByVerifiedEmail(
            @Validated(ValidationSequence.class) @RequestBody PasswordByEmailRequest passwordRequest) {
        memberService.updatePasswordByVerifiedEmail(passwordRequest.getPassword(), passwordRequest.getEmail());
        return ResponseEntity.ok(Response.of(HttpStatus.OK, "패스워드 변경 성공"));
    }
    
    @DeleteMapping
    @ApiOperation(value = "회원 탈퇴", notes = "회원을 삭제한다")
    @Authenticated
    public ResponseEntity<Response> deleteMember(@CurrentMember Long memberId) {
        MemberResponse response = memberService.deleteMember(memberId);
        return ResponseEntity.ok(Response.of(HttpStatus.OK, response, "멤버 삭제 성공"));
    }
    
}
