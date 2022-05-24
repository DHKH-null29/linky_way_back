package com.wnis.linkyway.util.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnis.linkyway.dto.member.JoinRequest;
import com.wnis.linkyway.dto.member.MemberResponse;
import com.wnis.linkyway.entity.Member;
import com.wnis.linkyway.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;


import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("/sqltest/initialize-test.sql")
@Import({ObjectMapper.class})
class MemberMapperTest {
    private final Logger logger = LoggerFactory.getLogger(MemberMapperTest.class);
    @Autowired
    MemberRepository memberRepository;
    
    @Autowired
    ObjectMapper objectMapper;
    
    
    @Test
    @DisplayName("JoinReqest -> Member 매핑 테스트")
    void shouldReturnMemberFromJoinRequest() throws JsonProcessingException {
        JoinRequest joinRequest = JoinRequest.builder()
                .email("hellowolrd@naver.com")
                .password("aasaq!12@qA")
                .nickname("hello").build();
        
        Member member = Member.builder()
                .email("hellowolrd@naver.com")
                .password("aasaq!12@qA")
                .nickname("hello")
                .build();
        
        Member member1 = MemberMapper.instance.joinRequestToMember(joinRequest);
        
        logger.info("리턴 값 : {}", objectMapper.writeValueAsString(member));
        
        assertThat(member1.getEmail()).isEqualTo(member.getEmail());
        assertThat(member1.getPassword()).isEqualTo(member1.getPassword());
        assertThat(member1.getNickname()).isEqualTo(member1.getNickname());
    }
    
    @Test
    @DisplayName("Member -> Response 매핑 테스트")
    void shouldReturnMemberResponseFormMember() throws JsonProcessingException {
        Member member = Member.builder()
                .email("hellowolrd@naver.com")
                .password("aasaq!12@qA")
                .nickname("hello")
                .build();
        
        memberRepository.save(member);
        
        MemberResponse memberResponse = MemberMapper.instance.memberToJoinResponse(member);
        logger.info("리턴 값 : {}", objectMapper.writeValueAsString(memberResponse));
        
    }
}