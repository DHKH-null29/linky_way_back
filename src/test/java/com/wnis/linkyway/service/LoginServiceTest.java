package com.wnis.linkyway.service;

import com.wnis.linkyway.dto.member.LoginRequest;
import com.wnis.linkyway.entity.Member;
import com.wnis.linkyway.repository.MemberRepository;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoginServiceTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private MemberService memberService;

    @BeforeAll
    void initMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("로그인 동작 테스트")
    class LoginTest {

        final String email = "ehd0309@naver.com";
        final String password = "AbcDeFG123!!";
        final Member member = Member.builder()
                                    .email(email)
                                    .password(password)
                                    .build();
        final LoginRequest loginRequest = new LoginRequest(email, password);

        @Test
        @DisplayName("로그인에 성공하여 Member를 반환한다.")
        void shouldReturnMember() {
            given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));
            given(passwordEncoder.matches(password, member.getPassword())).willReturn(true);

            Member resultMember = memberService.login(loginRequest);
            assertThat(resultMember).isEqualTo(member);
        }

        @Test
        @DisplayName("존재하지 않는 이메일이라 예외를 반환한다.")
        void shouldThrowsInvalidInputExceptionDueToNotFoundEmail() {
            given(memberRepository.findByEmail(email)).willReturn(Optional.empty());

            assertThatThrownBy(() -> memberService.login(loginRequest)).isInstanceOf(UsernameNotFoundException.class);
            verify(passwordEncoder, times(0)).encode(any());
        }

        @Test
        @DisplayName("비밀번호가 일치하지 않아 예외를 반환한다.")
        void shouldThrowsConflictExceptionDueToNotCorrectPassword() {
            given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));
            given(passwordEncoder.matches(password, member.getPassword())).willReturn(false);

            assertThatThrownBy(() -> memberService.login(loginRequest)).isInstanceOf(BadCredentialsException.class);
        }

    }

}
