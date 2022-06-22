package com.wnis.linkyway.controller.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnis.linkyway.controller.MemberController;
import com.wnis.linkyway.dto.member.UpdateMemberRequest;
import com.wnis.linkyway.exception.common.NotAddDuplicateEntityException;
import com.wnis.linkyway.exception.common.NotFoundEntityException;
import com.wnis.linkyway.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MemberController.class,
        excludeFilters = { @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = WebSecurityConfigurerAdapter.class) })
public class MemberControllerTest {
    
    @Autowired
    MockMvc mockMvc;
    
    @MockBean
    MemberService memberService;
    
    @Autowired
    WebApplicationContext ctx;
    
    @Autowired
    ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                                 .alwaysDo(print())
                                 .build();
    }
    
    
    @Nested
    @DisplayName("회원 수정")
    class updateMemberTest {
        
        @Test
        @DisplayName("입력 Validation 테스트")
        void shouldReturnStatus400_WhenInvalidInputTest() throws Exception {
            UpdateMemberRequest updateMemberRequest = UpdateMemberRequest.builder()
                    .nickname("adksjflkajsdljaksdflksjflasdkfjsdjfjsalkjfasdfas").build();
            
            mockMvc.perform(put("/api/members/page/me")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(updateMemberRequest)))
                   .andExpect(status().is(400));
        }
    
        @Test
        @DisplayName("닉네임 중복시 테스트")
        void shouldReturnStatus409_WhenThrowDuplicateNicknameTest() throws Exception {
            UpdateMemberRequest updateMemberRequest = UpdateMemberRequest.builder()
                                                                         .nickname("hello").build();
    
            doThrow(new NotAddDuplicateEntityException("h")).when(memberService).updateMyPage(any(), any());
            
            mockMvc.perform(put("/api/members/page/me")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(updateMemberRequest)))
                   .andExpect(status().isConflict());
        }
    
        @Test
        @DisplayName("회원 정보가 없을 시 테스트")
        void shouldReturnStatus404_WhenThrowNotFoundIDTest() throws Exception {
            UpdateMemberRequest updateMemberRequest = UpdateMemberRequest.builder()
                                                                         .nickname("hello").build();
        
            doThrow(new NotFoundEntityException(" ")).when(memberService).updateMyPage(any(), any());
        
            mockMvc.perform(put("/api/members/page/me")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(updateMemberRequest)))
                   .andExpect(status().is(404));
        }
    }
}
