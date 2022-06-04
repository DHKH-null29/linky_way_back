package com.wnis.linkyway.controller.folder;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnis.linkyway.controller.tag.TagControllerIntegrationTest;
import com.wnis.linkyway.dto.folder.AddFolderRequest;
import com.wnis.linkyway.dto.folder.SetFolderNameRequest;
import com.wnis.linkyway.dto.folder.SetFolderPathRequest;
import com.wnis.linkyway.entity.Folder;
import com.wnis.linkyway.entity.Member;
import com.wnis.linkyway.repository.FolderRepository;
import com.wnis.linkyway.repository.MemberRepository;
import com.wnis.linkyway.security.testutils.WithMockMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.transaction.Transactional;
import java.util.Arrays;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql("/sqltest/initialize-test.sql")
public class FolderControllerIntegrationTest {
    private final Logger logger = LoggerFactory.getLogger(TagControllerIntegrationTest.class);
    @Autowired
    WebApplicationContext ctx;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    FolderRepository folderRepository;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;
    
    @BeforeEach
    void setup() {
        //MockMvc 설정
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .apply(springSecurity())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
                .alwaysDo(print())
                .build();
        
        Member member1 = Member.builder()
                .email("marrin1101@naver.com")
                .nickname("Hyeon")
                .password("A1!a1234")
                .build();
        
        Folder folder1 = Folder.builder()
                .member(member1)
                .depth(0L)
                .name("f1")
                .build();
        
        Folder folder2 = Folder.builder()
                .member(member1)
                .name("f2")
                .depth(1L)
                .parent(null)
                .build();
        
        Folder folder3 = Folder.builder()
                .member(member1)
                .name("f3")
                .depth(2L)
                .parent(folder2)
                .build();
        
        Folder folder4 = Folder.builder()
                .member(member1)
                .name("f4")
                .depth(3L)
                .parent(folder2)
                .build();
        
        Folder folder5 = Folder.builder()
                .member(member1)
                .name("f5")
                .depth(2L)
                .parent(folder1)
                .build();
        
        memberRepository.save(member1);
        folderRepository.saveAll(Arrays.asList(folder1, folder2, folder3, folder4, folder5));
        
    }
    
    @Nested
    @DisplayName("최상위 폴더 조회")
    class SearchSuperFolder {
        
        @Test
        @DisplayName("최상위 응답 테스트")
        @WithMockMember(id = 1L, email = "marrin1101@naver.com")
        void responseTest() throws Exception {
            MvcResult mvcResult = mockMvc.perform(get("/api/folders/super"))
                    .andExpect(status().is(200))
                    .andReturn();
    
            logger.info(mvcResult.getResponse().getContentAsString());
    
        }
        
    }
    
    @Nested
    @DisplayName("폴더 조회")
    class SearchFolder {
        
        @Test
        @DisplayName("응답 테스트")
        @WithMockMember(id = 1L, email = "marrin1101@naver.com")
        void responseTest() throws Exception {
            MvcResult mvcResult = mockMvc.perform(get("/api/folders/1"))
                    .andExpect(status().is(200))
                    .andReturn();
            
            logger.info(mvcResult.getResponse().getContentAsString());
            
        }
    }
    
    @Nested
    @DisplayName("폴더 추가")
    class AddFolder {
        
        @Test
        @DisplayName("응답 테스트")
        @WithMockMember(id = 1L, email = "marrin1101@naver.com")
        void responseTest() throws Exception {
            AddFolderRequest addFolderRequest = AddFolderRequest.builder()
                    .parentFolderId(2L)
                    .name("f10")
                    .build();
            
            MvcResult mvcResult = mockMvc.perform(post("/api/folders")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsBytes(addFolderRequest)))
                    .andExpect(status().is(200))
                    .andReturn();
            
            logger.info(mvcResult.getResponse().getContentAsString());
            
        }
    
        @Test
        @DisplayName("부모 폴더에 null 을 기입한 경우")
        @WithMockMember(id = 1L, email = "marrin1101@naver.com")
        void RequestParentIdNull() throws Exception {
            AddFolderRequest addFolderRequest = AddFolderRequest.builder()
                    .parentFolderId(null)
                    .name("f10")
                    .build();
        
            MvcResult mvcResult = mockMvc.perform(post("/api/folders")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsBytes(addFolderRequest)))
                    .andExpect(status().is(200))
                    .andReturn();
            
        
        }
        
        @Test
        @DisplayName("멤버 없는 경우 응답 테스트")
        @WithMockMember(id = 10L, email = "marrin1101@naver.com")
        void NotExistMemberResponseTest() throws Exception {
            AddFolderRequest addFolderRequest = AddFolderRequest.builder()
                    .parentFolderId(1L)
                    .name("f10")
                    .build();
            
            MvcResult mvcResult = mockMvc.perform(post("/api/folders")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsBytes(addFolderRequest)))
                    .andExpect(status().is(404))
                    .andReturn();
        }
        
        @Test
        @DisplayName("부모 폴더가 없는 경우 응답 테스트")
        @WithMockMember(id = 1L, email = "marrin1101@naver.com")
        void NotExistFolderResponseTest() throws Exception {
            AddFolderRequest addFolderRequest = AddFolderRequest.builder()
                    .parentFolderId(100L)
                    .name("f10")
                    .build();
            
            MvcResult mvcResult = mockMvc.perform(post("/api/folders")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsBytes(addFolderRequest)))
                    .andExpect(status().is(404))
                    .andReturn();
            
            logger.info(mvcResult.getResponse().getContentAsString());
            
        }
    }
    
    @Nested
    @DisplayName("폴더 이름 수정")
    class SetFolderNameTest {
        
        @Test
        @DisplayName("응답 테스트")
        @WithMockMember(id = 1L, email = "marrin1101@naver.com")
        void responseTest() throws Exception {
            SetFolderNameRequest setFolderNameRequest = SetFolderNameRequest.builder()
                    .name("f_10L").build();
            
            MvcResult mvcResult = mockMvc.perform(put("/api/folders/1/name")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(setFolderNameRequest)))
                    .andExpect(status().is(200))
                    .andReturn();
            
            logger.info(mvcResult.getResponse().getContentAsString());
        }
        
        @Test
        @DisplayName("폴더가 존재하지 않는 경우 응답 테스트")
        @WithMockMember(id = 1L, email = "marrin1101@naver.com")
        void NotExistFolderResponseTest() throws Exception {
            SetFolderNameRequest setFolderNameRequest = SetFolderNameRequest.builder()
                    .name("f_10L").build();
            
            MvcResult mvcResult = mockMvc.perform(put("/api/folders/100/name")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(setFolderNameRequest)))
                    .andExpect(status().is(409))
                    .andReturn();
            
            logger.info(mvcResult.getResponse().getContentAsString());
        }
        
    }
    
    @Nested
    @DisplayName("폴더 경로 수정")
    class SetFolderPathTest {
    
    
        @Test
        @DisplayName("응답 테스트")
        @WithMockMember(id = 1L, email = "marrin1101@naver.com")
        void responseTest() throws Exception {
            SetFolderPathRequest setFolderPathRequest = SetFolderPathRequest.builder()
                    .targetFolderId(2L)
                    .build();
        
            MvcResult mvcResult = mockMvc.perform(put("/api/folders/3/path")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(setFolderPathRequest)))
                    .andExpect(status().is(200))
                    .andReturn();
        }
        
        @Test
        @DisplayName("수정하려는 폴더의 깊이가 깊은 경우 테스트")
        @WithMockMember(id = 1L, email = "marrin1101@naver.com")
        void LimitFolderDepthTest() throws Exception {
            SetFolderPathRequest setFolderPathRequest = SetFolderPathRequest.builder()
                    .targetFolderId(5L)
                    .build();
            
            MvcResult mvcResult = mockMvc.perform(put("/api/folders/3/path")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(setFolderPathRequest)))
                    .andExpect(status().is(409))
                    .andReturn();
        }
        
        @Test
        @DisplayName("수정하려는 폴더가 없는 경우 테스트")
        @WithMockMember(id = 1L, email = "marrin1101@naver.com")
        void NotExistOriginFolderTest() throws Exception {
            SetFolderPathRequest setFolderPathRequest = SetFolderPathRequest.builder()
                    .targetFolderId(5L)
                    .build();
            
            MvcResult mvcResult = mockMvc.perform(put("/api/folders/101/path")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(setFolderPathRequest)))
                    .andExpect(status().is(409))
                    .andReturn();
            
            logger.info(mvcResult.getResponse().getContentAsString());
        }
        
        @Test
        @DisplayName("목표 부모 폴더가 없는 경우 테스트")
        @WithMockMember(id = 1L, email = "marrin1101@naver.com")
        void NotExistTargetFolderTest() throws Exception {
            SetFolderPathRequest setFolderPathRequest = SetFolderPathRequest.builder()
                    .targetFolderId(100L)
                    .build();
            
            MvcResult mvcResult = mockMvc.perform(put("/api/folders/1/path")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(setFolderPathRequest)))
                    .andExpect(status().is(409))
                    .andReturn();
            
            logger.info(mvcResult.getResponse().getContentAsString());
        }
        
        @Test
        @DisplayName("목표부모가 직계후손인 경우 테스트")
        @WithMockMember(id = 1L, email = "marrin1101@naver.com")
        void TargetFolderIsDirectDescendantFolderTest() throws Exception {
            SetFolderPathRequest setFolderPathRequest = SetFolderPathRequest.builder()
                    .targetFolderId(3L)
                    .build();
            
            MvcResult mvcResult = mockMvc.perform(put("/api/folders/1/path")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(setFolderPathRequest)))
                    .andExpect(status().is(409))
                    .andReturn();
            
            logger.info(mvcResult.getResponse().getContentAsString());
        }
    }
    
    @Nested
    @DisplayName("삭제 테스트")
    class DeleteFolderTest {
        
        @Test
        @DisplayName("응답 테스트")
        @WithMockMember(id = 1L, email = "marrin1101@naver.com")
        void responseTest() throws Exception {
            MvcResult mvcResult = mockMvc.perform(delete("/api/folders/1")
                    )
                    .andExpect(status().is(200))
                    .andReturn();
            
            logger.info(mvcResult.getResponse().getContentAsString());
        }
        
        @Test
        @DisplayName("삭제할 폴더가 없는 경우 응답 테스트")
        @WithMockMember(id = 1L, email = "marrin1101@naver.com")
        void NotExistFolderResponseTest() throws Exception {
            MvcResult mvcResult = mockMvc.perform(delete("/api/folders/100")
                    )
                    .andExpect(status().is(409))
                    .andReturn();
            
            logger.info(mvcResult.getResponse().getContentAsString());
        }
    }
}
