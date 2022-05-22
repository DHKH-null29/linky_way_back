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

    public Long join(JoinRequest joinRequest) {
        validEmailDuplication(joinRequest.getEmail());
        Member joinMember = Member.builder()
                .nickname(joinRequest.getNickname())
                .email(joinRequest.getEmail())
                .password(passwordEncoder.encode(joinRequest.getPassword()))
                .build();
        memberRepository.save(joinMember);
        return joinMember.getId();
    }

    private void validEmailDuplication(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new ResourceConflictException("이미 중복되는 이메일이 있습니다.");
        }
    }

    private void validPassword(String inputPassword, String encodedPassword) {
        if (!passwordEncoder.matches(inputPassword, encodedPassword)) {
            throw new BadCredentialsException("이메일 또는 비밀번호를 확인하세요");
        }
    }

}
