package com.wnis.linkyway.integration;

import com.wnis.linkyway.dto.Response;
import com.wnis.linkyway.dto.member.LoginRequest;
import com.wnis.linkyway.dto.member.LoginResponse;
import com.wnis.linkyway.entity.Member;
import com.wnis.linkyway.exception.error.ErrorResponse;
import com.wnis.linkyway.repository.MemberRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MemberIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final HttpHeaders httpHeaders = new HttpHeaders();

    private final String rawPassword = "AbcdEf1234!!!";
    private Member member = null;

    @BeforeAll
    void setup() {
        member = Member.builder()
                .nickname("김갑환")
                .email("ehd0309@naver.com")
                .password(passwordEncoder.encode(rawPassword))
                .build();
        memberRepository.save(member);
        httpHeaders.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
    }

    @Nested
    @DisplayName("POST: api/member/login: 로그인 통합 테스트")
    class LoginTest {

        @Test
        @DisplayName("존재하는 멤버에 대한 시도로 정상적으로 응답한다.")
        void shouldLoginSucceedAndReturnAccessToken() {
            LoginRequest loginRequest = new LoginRequest(member.getEmail(), rawPassword);
            assertThat(testRestTemplate.exchange(
                    "/api/members/login",
                    HttpMethod.POST,
                    new HttpEntity<>(loginRequest, httpHeaders),
                    new ParameterizedTypeReference<Response<LoginResponse>>() {
                    }))
                    .isNotNull()
                    .isInstanceOfSatisfying(ResponseEntity.class, responseEntity -> {
                        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
                        assertThat(responseEntity.getBody())
                                .isNotNull()
                                .isInstanceOfSatisfying(Response.class, response -> {
                                    assertThat(response).isNotNull();
                                    assertThat(response.getData())
                                            .hasFieldOrProperty("accessToken");
                                });
                    });
        }

        @Test
        @DisplayName("비밀번호 불일치로 로그인에 실패한다")
        void shouldThrowsAuthorizationExceptionWithIncorrectPassword() {

            LoginRequest loginRequest = new LoginRequest(member.getEmail(), "!");
            assertThat(testRestTemplate.exchange(
                    "/api/members/login",
                    HttpMethod.POST,
                    new HttpEntity<>(loginRequest, httpHeaders),
                    new ParameterizedTypeReference<Response<ErrorResponse>>() {
                    }))
                    .isNotNull()
                    .isInstanceOfSatisfying(ResponseEntity.class, responseEntity -> {
                        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
                    });
        }

    }

}
