package com.wnis.linkyway.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnis.linkyway.dto.Response;
import com.wnis.linkyway.dto.member.JoinRequest;
import com.wnis.linkyway.dto.member.MemberResponse;
import com.wnis.linkyway.dto.member.PasswordRequest;
import com.wnis.linkyway.exception.common.ResourceConflictException;
import com.wnis.linkyway.exception.common.ResourceNotFoundException;
import com.wnis.linkyway.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@Sql("/sqltest/member-test.sql")
class MemberServiceTest {
    
    private final Logger logger = LoggerFactory.getLogger(MemberServiceTest.class);
    @Autowired
    MemberService memberService;
    
    @Autowired
    MemberRepository memberRepository;
    
    @Autowired
    ObjectMapper objectMapper;
    
    @Nested
    @DisplayName("회원가입")
    class JoinTest {
        
        @Test
        @DisplayName("응답 테스트")
        void responseTest() throws JsonProcessingException {
            JoinRequest joinRequest = JoinRequest.builder()
                    .nickname("hello")
                    .password("asdfasdfa")
                    .email("masss2213@naver.com")
                    .build();
            
            Response<MemberResponse> response = memberService.join(joinRequest);
            logger.info(objectMapper.writeValueAsString(response));
        }
        
        @Test
        @DisplayName("중복 예외 테스트")
        void shouldThrowDuplicateExceptionTest() {
            JoinRequest joinRequest = JoinRequest.builder()
                    .nickname("Ze1")
                    .password("asdfasdfa")
                    .email("marrin1101@naver.com")
                    .build();
            
            assertThatThrownBy(() -> {
                memberService.join(joinRequest);
            }).isInstanceOf(ResourceConflictException.class).hasMessage("이미 중복되는 이메일이 있습니다");
            
        }
        
    }
    
    @Nested
    @DisplayName("이메일 조회")
    class searchEmailTest {
        
        @Test
        @DisplayName("응답 테스트")
        void responseTest() throws JsonProcessingException {
            String email = "marrin1101@naver.com";
            Response<MemberResponse> response = memberService.searchEmail(email);
            logger.info(objectMapper.writeValueAsString(response));
        }
        
        @Test
        @DisplayName("이메일이 없는 경우 예외 테스트")
        void shouldThrowNotFoundEmailException() {
            String email = "aaasdbadafdaf@naver.com";
            assertThatThrownBy(() -> {
                memberService.searchEmail(email);
            }).isInstanceOf(ResourceNotFoundException.class).hasMessage("조회한 이메일이 존재하지 않습니다");
        }
    }
    
    @Nested
    @DisplayName("마이 페이지 조회")
    class searchMyPage {
        
        @Test
        @DisplayName("응답 테스트")
        void responseTest() throws JsonProcessingException {
            Long memberId = 1L;
            Response<MemberResponse> response = memberService.searchMyPage(memberId);
            logger.info(objectMapper.writeValueAsString(response));
        }
        
        @Test
        @DisplayName("회원이 없는 경우 예외 테스트")
        void shouldThrowNotFoundEmailException() {
            Long memberId = 100L;
            assertThatThrownBy(() -> {
                memberService.searchMyPage(memberId);
            }).isInstanceOf(ResourceNotFoundException.class).hasMessage("회원을 찾을 수 없습니다");
            
        }
    }
    
    @Nested
    @DisplayName("패스워드 변경")
    class SetPasswordTest {
    
        @Test
        @DisplayName("응답 테스트")
        void responseTest() throws JsonProcessingException {
            PasswordRequest passwordRequest = PasswordRequest.builder()
                    .password("aasd!@!asdA").build();
            Long memberId = 1L;
            Response<MemberResponse> response =
                    memberService.setPassword(passwordRequest, memberId);
            
            logger.info(objectMapper.writeValueAsString(response));
        }
        
        @Test
        @DisplayName("회원이 없는 경우 예외")
        void shouldThrowNotFoundMemberException() {
            PasswordRequest passwordRequest = PasswordRequest.builder()
                    .password("aasd!@!asdA").build();
            Long memberId = 100L;
            
            assertThatThrownBy(()-> {
                memberService.setPassword(passwordRequest, memberId);
            }).isInstanceOf(ResourceNotFoundException.class).hasMessage("회원을 찾을 수 없습니다");
        }
    }
    
    @Nested
    @DisplayName("회원 탈퇴")
    class DeleteMemberTest {
        
        @Test
        @DisplayName("응답 테스트")
        void responseTest() throws JsonProcessingException {
            Long memberId = 1L;
            Response<MemberResponse> response = memberService.deleteMember(memberId);
            
            logger.info(objectMapper.writeValueAsString(response));
        }
        
        @Test
        @DisplayName("삭제 할 회원이 없는 경우")
        void shouldThrowNotFoundMemberException() {
            Long memberId = 10000L;
            
            assertThatThrownBy(() -> {
                memberService.deleteMember(memberId);
            }).isInstanceOf(ResourceConflictException.class).hasMessage("삭제 할 수 없습니다");
        }
    }
    
}