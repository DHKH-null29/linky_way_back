package com.wnis.linkyway.service;

import com.wnis.linkyway.dto.member.*;
import com.wnis.linkyway.entity.Member;
import com.wnis.linkyway.exception.common.*;
import com.wnis.linkyway.repository.FolderRepository;
import com.wnis.linkyway.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final FolderRepository folderRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Member login(LoginRequest loginRequest) {
        Member member = getMemberByEmail(loginRequest.getEmail(),
                new UsernameNotFoundException("이메일 또는 비밀번호를 확인하세요"));
        validPassword(loginRequest.getPassword(), member.getPassword());
        return member;
    }

    @Transactional
    public MemberResponse join(JoinRequest joinRequest) {
        validEmailDuplication(joinRequest.getEmail());
        validNicknameDuplication(joinRequest.getNickname());

        Member joinMember = Member.builder()
                .nickname(joinRequest.getNickname())
                .password(joinRequest.getPassword())
                .email(joinRequest.getEmail())
                .build();

        joinMember.changePassword(passwordEncoder.encode(joinMember.getPassword()));

        memberRepository.save(joinMember);
        folderRepository.addSuperFolder(joinMember.getId());

        return MemberResponse.from(joinMember);
    }

    @Transactional
    public MemberResponse searchEmail(String email) {
        Member member = getMemberByEmail(email);
        return MemberResponse.builder().email(member.getEmail()).build();
    }

    @Transactional
    public MemberResponse searchMyPage(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundEntityException("회원을 찾을 수 없습니다"));

        return MemberResponse.from(member);
    }

    @Transactional
    public MemberResponse updateMyPage(UpdateMemberRequest updateMemberRequest, Long memberId) {
        validNicknameDuplication(updateMemberRequest.getNickname());

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundEntityException("회원을 찾을 수 없습니다"));

        member.updateNickname(updateMemberRequest.getNickname());

        return MemberResponse.builder()
                .memberId(memberId)
                .nickname(member.getNickname())
                .build();
    }

    @Transactional
    public MemberResponse updatePassword(PasswordRequest passwordRequest, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotModifyEmptyEntityException(
                        "회원이 존재하지 않아 비밀번호를 바꿀 수 없습니다"));
        member.changePassword(passwordEncoder.encode(passwordRequest.getPassword()));
        return MemberResponse.builder().build();
    }

    @Transactional
    public void updatePasswordByVerifiedEmail(String password, String email) {
        Member member = getMemberByEmail(email);
        member.changePassword(passwordEncoder.encode(password));
    }

    @Transactional
    public MemberResponse deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() ->
                new NotDeleteEmptyEntityException("삭제 할 수 없습니다"));

        memberRepository.deleteById(memberId);
        return MemberResponse.builder().build();
    }

    public DuplicationResponse isValidNickname(String nickname) {
        if (!StringUtils.hasText(nickname)) {
            throw new InvalidValueException("닉네임을 입력하세요");
        }

        return new DuplicationResponse(!memberRepository.existsByNickname(nickname));
    }

    private Member getMemberByEmail(String email, RuntimeException e) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> e);
    }

    private Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundEntityException("존재하지 않는 회원입니다"));
    }

    private void validNicknameDuplication(String nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            throw new NotAddDuplicateEntityException("이미 중복되는 닉네임이 있습니다");
        }
    }

    private void validEmailDuplication(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new NotAddDuplicateEntityException("이미 중복되는 이메일이 있습니다");
        }
    }

    private void validPassword(String inputPassword, String encodedPassword) {
        if (!passwordEncoder.matches(inputPassword, encodedPassword)) {
            throw new BadCredentialsException("이메일 또는 비밀번호를 확인하세요");
        }
    }

}
