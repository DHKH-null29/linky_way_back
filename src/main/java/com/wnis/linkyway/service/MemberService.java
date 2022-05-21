package com.wnis.linkyway.service;

import com.wnis.linkyway.dto.Response;
import com.wnis.linkyway.dto.member.JoinRequest;
import com.wnis.linkyway.dto.member.JoinResponse;
import com.wnis.linkyway.dto.member.LoginRequest;
import com.wnis.linkyway.entity.Member;
import com.wnis.linkyway.exception.common.ResourceConflictException;
import com.wnis.linkyway.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional(readOnly = true)
    public Member login(LoginRequest loginRequest) {
        Member member = memberRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("이메일 또는 비밀번호를 확인하세요"));
        validPassword(loginRequest.getPassword(), member.getPassword());
        return member;
    }
    
    public Response<JoinResponse> join(JoinRequest joinRequest) {
        Member joinMember = Member.builder().nickname(joinRequest.getNickname())
                .email(joinRequest.getEmail())
                .password(passwordEncoder.encode(joinRequest.getPassword()))
                .build();
        
        Member existMember = memberRepository.findByEmail(joinRequest.getEmail()).orElse(null);
        if (existMember != null) {
            throw new ResourceConflictException("중복된 이메일/아이디가 존재합니다.");
        }
        
        Member member = memberRepository.save(joinMember);
        JoinResponse joinResponse = JoinResponse.builder().memberId(member.getId()).build();
        return Response.<JoinResponse>builder()
                .code(200)
                .message("회원가입 성공")
                .data(joinResponse)
                .build();
    }
    
    private void validPassword(String inputPassword, String encodedPassword) {
        if (!passwordEncoder.matches(inputPassword, encodedPassword)) {
            throw new BadCredentialsException("이메일 또는 비밀번호를 확인하세요");
        }
    }
    
}
