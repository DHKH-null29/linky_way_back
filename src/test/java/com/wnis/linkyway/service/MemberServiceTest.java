package com.wnis.linkyway.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnis.linkyway.dto.member.JoinRequest;
import com.wnis.linkyway.dto.member.MemberResponse;
import com.wnis.linkyway.dto.member.PasswordRequest;
import com.wnis.linkyway.dto.member.UpdateMemberRequest;
import com.wnis.linkyway.entity.Member;
import com.wnis.linkyway.exception.common.*;
import com.wnis.linkyway.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@Sql("/sqltest/member-test.sql")
class MemberServiceTest {

    private final Logger logger = LoggerFactory.getLogger(MemberServiceTest.class);
    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

            MemberResponse response = memberService.join(joinRequest);

            assertThat(response.getEmail()).isNotNull();
            assertThat(response.getMemberId()).isNotNull();
            assertThat(response.getNickname()).isNotNull();
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
            }).isInstanceOf(ResourceConflictException.class)
              .hasMessage("이미 중복되는 이메일이 있습니다");

        }

    }

    @Nested
    @DisplayName("이메일 조회")
    class searchEmailTest {

        @Test
        @DisplayName("응답 테스트")
        void responseTest() throws JsonProcessingException {
            String email = "marrin1101@naver.com";
            MemberResponse response = memberService.searchEmail(email);
            assertThat(response).extracting("memberId").isNull();
            assertThat(response).extracting("email").isNotNull();
            assertThat(response).extracting("nickname").isNull();
        }

        @Test
        @DisplayName("이메일이 없는 경우 예외 테스트")
        void shouldThrowNotFoundEmailException() {
            String email = "aaasdbadafdaf@naver.com";
            assertThatThrownBy(() -> {
                memberService.searchEmail(email);
            }).isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("마이 페이지 조회")
    class searchMyPage {

        @Test
        @DisplayName("응답 테스트")
        void responseTest() throws JsonProcessingException {
            Long memberId = 1L;
            MemberResponse response = memberService.searchMyPage(memberId);
            logger.info(objectMapper.writeValueAsString(response));

            assertThat(response).extracting("memberId").isNotNull();
            assertThat(response).extracting("email").isNotNull();
            assertThat(response).extracting("nickname").isNotNull();
        }

        @Test
        @DisplayName("회원이 없는 경우 예외 테스트")
        void shouldThrowNotFoundEmailException() {
            Long memberId = 100L;
            assertThatThrownBy(() -> {
                memberService.searchMyPage(memberId);
            }).isInstanceOf(ResourceNotFoundException.class)
              .hasMessage("회원을 찾을 수 없습니다");

        }
    }

    @Nested
    @DisplayName("마이페이지 수정")
    class UpdateMemberTest {

        @Test
        @DisplayName("응답 테스트")
        void shouldReturnIdAndUpdatedNicknameWhenResponseTest() {
            // given
            UpdateMemberRequest updateMemberRequest = UpdateMemberRequest.builder()
                    .nickname("쿠로사키 2치고").build();
            // when
            MemberResponse memberResponse = memberService.updateMyPage(updateMemberRequest, 1L);

            // then
            assertThat(memberResponse.getMemberId()).isNotNull();
            assertThat(memberResponse.getNickname()).isNotNull();
        }

        @Test
        @DisplayName("닉네임 중복 여부")
        void shouldThrowDuplicateException_WhenInputDuplicateNicknameTest() {
            // given
            UpdateMemberRequest updateMemberRequest = UpdateMemberRequest.builder()
                    .nickname("Zeratu6")
                    .build();


            assertThatThrownBy(()-> {
                // when
                memberService.updateMyPage(updateMemberRequest, 1L);
            }).isInstanceOf(NotAddDuplicateEntityException.class); // then
        }

        @Test
        @DisplayName("회원입력이 옳바르지 않은 경우")
        void shouldThrowNotFoundException_WhenInputInvalidMemberIdTest() {
            // given
            UpdateMemberRequest updateMemberRequest = UpdateMemberRequest.builder()
                         .nickname("손흥민").build();

            assertThatThrownBy(()-> {
                // when
                memberService.updateMyPage(updateMemberRequest, 100L);
            }).isInstanceOf(NotFoundEntityException.class); // then
        }
    }

    @Nested
    @DisplayName("패스워드 변경")
    class SetPasswordTest {

        @Test
        @DisplayName("응답 테스트")
        void responseTest() throws JsonProcessingException {
            PasswordRequest passwordRequest = PasswordRequest.builder()
                                                             .password("aasd!@!asdA")
                                                             .build();
            Long memberId = 1L;
            MemberResponse response = memberService.updatePassword(passwordRequest, memberId);

            logger.info(objectMapper.writeValueAsString(response));

            assertThat(response).extracting("memberId").isNull();
            assertThat(response).extracting("email").isNull();
            assertThat(response).extracting("nickname").isNull();
        }

        @Test
        @DisplayName("회원이 없는 경우 예외")
        void shouldThrowNotFoundMemberException() {
            PasswordRequest passwordRequest = PasswordRequest.builder()
                                                             .password("aasd!@!asdA")
                                                             .build();
            Long memberId = 100L;

            assertThatThrownBy(() -> {
                memberService.updatePassword(passwordRequest, memberId);
            }).isInstanceOf(ResourceConflictException.class)
              .hasMessage("회원이 존재하지 않아 비밀번호를 바꿀 수 없습니다");
        }
    }

    @Nested
    @DisplayName("비인증 상황에서 이메일 인증 후 패스워드 변경 동작 테스트")
    class SetPasswordTestByVerfiedEmail {

        private final String EXIST_EMAIL = "marrin1101@hanmail.com";
        private final String REQUEST_PASSWORD = "aasd!@!asdA";

        @Test
        @DisplayName("존재하는 회원(이메일)로의 요청으로 정상적으로 비밀번호 변경이 수행된다.")
        void shouldDoChangePasswordByEmail() {
            memberService.updatePasswordByVerifiedEmail("aasd!@!asdA",EXIST_EMAIL);
            Member member = memberRepository.findByEmail(EXIST_EMAIL).orElse(null);
            assertThat(passwordEncoder.matches(REQUEST_PASSWORD,member.getPassword())).isTrue();
        }
    }

    @Nested
    @DisplayName("회원 탈퇴")
    class DeleteMemberTest {

        @Test
        @DisplayName("응답 테스트")
        void responseTest() throws JsonProcessingException {
            Long memberId = 1L;
            MemberResponse response = memberService.deleteMember(memberId);

            logger.info(objectMapper.writeValueAsString(response));
        }

        @Test
        @DisplayName("삭제 할 회원이 없는 경우")
        void shouldThrowNotFoundMemberException() {
            Long memberId = 10000L;

            assertThatThrownBy(() -> {
                memberService.deleteMember(memberId);
            }).isInstanceOf(ResourceConflictException.class)
              .hasMessage("삭제 할 수 없습니다");
        }
    }

    @Nested
    @DisplayName("중복 닉네임 여부 조회")
    class IsValidNicknameTest {

        @Test
        @DisplayName("사용 가능한 닉네임 요청에 대한 응답: True")
        void shouldReturnTrue() {
            assertThat(memberService.isValidNickname("최번개")
                                    ).isNotNull()
                                               .hasFieldOrPropertyWithValue("usable", true);
        }

        @Test
        @DisplayName("중복되는 닉네임 요청에 대한 응답: False")
        void shouldReturnFalse() {
            assertThat(memberService.isValidNickname("Zeratu1")
                                    ).isNotNull()
                                               .hasFieldOrPropertyWithValue("usable", false);
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(strings = { "", "  " })
        @DisplayName("공백 닉네임 요청에 대해 예외 발생")
        void shouldThrowInvalidValueException(String nickname) {
            assertThatThrownBy(() -> memberService.isValidNickname(nickname)).isInstanceOf(InvalidValueException.class);
        }

    }

}