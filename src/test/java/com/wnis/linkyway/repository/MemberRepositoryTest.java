package com.wnis.linkyway.repository;

import com.wnis.linkyway.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/sqltest/initialize-test.sql")
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    
    @BeforeEach
    void beforeTest() {
        Member member1 = Member.builder()
                .nickname("aa")
                .email("helloworld@naver.com")
                .password("ads!!@adg21234A")
                .build();
        
        Member member2 = Member.builder()
                .nickname("zeratu1")
                .email("hellonear@naver.com")
                .password("aaA!1231Aas")
                .build();
        
        memberRepository.saveAll(Arrays.asList(member1, member2));
        
    }
    
    
    @Test
    @DisplayName("Exist 메서드 SQL 쿼리 테스트")
    void existByEmailTest() {
        boolean flag = memberRepository.existsByEmail("helloworld@naver.com");
    }
    
}