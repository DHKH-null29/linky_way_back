package com.wnis.linkyway.service;

import com.wnis.linkyway.dto.Response;
import com.wnis.linkyway.dto.member.*;
import com.wnis.linkyway.entity.Member;
import com.wnis.linkyway.exception.common.*;
import com.wnis.linkyway.repository.FolderRepository;
import com.wnis.linkyway.repository.MemberRepository;
import com.wnis.linkyway.util.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        Member member = memberRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("이메일 또는 비밀번호를 확인하세요"));
        
        validPassword(loginRequest.getPassword(), member.getPassword());
        return member;
    }
    
    @Transactional
    public Response<MemberResponse> join(JoinRequest joinRequest) {
        validEmailDuplication(joinRequest.getEmail());
        validNicknameDuplication(joinRequest.getNickname());
        
        Member joinMember = MemberMapper.instance.joinRequestToMember(joinRequest);
        joinMember.changePassword(passwordEncoder.encode(joinMember.getPassword()));
        
        memberRepository.save(joinMember);
        folderRepository.addSuperFolder(joinMember.getId());
        
        return Response.of(HttpStatus.OK,
                MemberMapper.instance.memberToJoinResponse(joinMember), "회원가입 성공");
    }
    @Transactional
    public Response<MemberResponse> searchEmail(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(()->
                 new NotFoundEntityException("조회한 이메일이 존재하지 않습니다")
        );
        
        return Response.of(HttpStatus.OK,
                MemberMapper.instance.memberToEmailResponse(member), "이메일 조회 성공");
    }
    @Transactional
    public Response<MemberResponse> searchMyPage(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() ->
            new NotFoundEntityException("회원을 찾을 수 없습니다")
        );
       
        return Response.of(HttpStatus.OK,
                MemberMapper.instance.memberToMyPageResponse(member), "회원 정보 조회 성공");
    }
    @Transactional
    public Response<MemberResponse> setPassword(PasswordRequest passwordRequest, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(()->
            new NotModifyEmptyEntityException("회원이 존재하지 않아 비밀번호를 바꿀 수 없습니다")
        );
        member.changePassword(passwordEncoder.encode(passwordRequest.getPassword()));
        return Response.of(HttpStatus.OK,null, "비밀번호 변경 성공");
    }
    @Transactional
    public Response<MemberResponse> deleteMember(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new NotDeleteEmptyEntityException("삭제 할 수 없습니다");
        }
        memberRepository.deleteById(memberId);
        return Response.of(HttpStatus.OK, null, "삭제 성공");
    }

    public Response<DuplicationResponse> isValidNickname(String nickname) {
        if (!StringUtils.hasText(nickname)) {
            throw new InvalidValueException("닉네임을 입력하세요");
        }

        return Response.of(HttpStatus.OK,
                new DuplicationResponse(!memberRepository.existsByNickname(nickname)),
                "닉네임 사용가능 여부 조회 성공");
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
