package com.wnis.linkyway.controller.folder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnis.linkyway.controller.tag.TagControllerIntegrationTest;
import com.wnis.linkyway.dto.folder.AddFolderRequest;
import com.wnis.linkyway.dto.folder.UpdateFolderNameRequest;
import com.wnis.linkyway.dto.folder.UpdateFolderPathRequest;
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
        // MockMvc ??????
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                                 .apply(springSecurity())
                                 .addFilters(new CharacterEncodingFilter("UTF-8", true)) // ?????? ??????
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
    @DisplayName("????????? ?????? ??????")
    class SearchSuperFolder {

        @Test
        @DisplayName("????????? ?????? ?????????")
        @WithMockMember(id = 1L, email = "marrin1101@naver.com")
        void responseTest() throws Exception {
            MvcResult mvcResult = mockMvc.perform(get("/api/folders/super"))
                                         .andExpect(status().is(200))
                                         .andReturn();
            
        }

    }

    @Nested
    @DisplayName("?????? ??????")
    class SearchFolder {

        @Test
        @DisplayName("?????? ?????????")
        @WithMockMember(id = 1L, email = "marrin1101@naver.com")
        void responseTest() throws Exception {
            MvcResult mvcResult = mockMvc.perform(get("/api/folders/1"))
                                         .andExpect(status().is(200))
                                         .andReturn();

            logger.info(mvcResult.getResponse()
                                 .getContentAsString());

        }
    }

    @Nested
    @DisplayName("?????? ??????")
    class AddFolder {

        @Test
        @DisplayName("?????? ?????????")
        @WithMockMember(id = 1L, email = "marrin1101@naver.com")
        void responseTest() throws Exception {
            AddFolderRequest addFolderRequest = AddFolderRequest.builder()
                                                                .parentFolderId(2L)
                                                                .name("f10")
                                                                .build();

            MvcResult mvcResult = mockMvc.perform(post("/api/folders").contentType("application/json")
                                                                      .content(objectMapper.writeValueAsBytes(addFolderRequest)))
                                         .andExpect(status().is(200))
                                         .andReturn();

            logger.info(mvcResult.getResponse()
                                 .getContentAsString());

        }
        

        @Test
        @DisplayName("?????? ?????? ?????? ?????? ?????????")
        @WithMockMember(id = 10L, email = "marrin1101@naver.com")
        void NotExistMemberResponseTest() throws Exception {
            AddFolderRequest addFolderRequest = AddFolderRequest.builder()
                                                                .parentFolderId(1L)
                                                                .name("f10")
                                                                .build();

            MvcResult mvcResult = mockMvc.perform(post("/api/folders").contentType("application/json")
                                                                      .content(objectMapper.writeValueAsBytes(addFolderRequest)))
                                         .andExpect(status().is(404))
                                         .andReturn();
        }

        @Test
        @DisplayName("?????? ????????? ?????? ?????? ?????? ?????????")
        @WithMockMember(id = 1L, email = "marrin1101@naver.com")
        void NotExistFolderResponseTest() throws Exception {
            AddFolderRequest addFolderRequest = AddFolderRequest.builder()
                                                                .parentFolderId(100L)
                                                                .name("f10")
                                                                .build();

            MvcResult mvcResult = mockMvc.perform(post("/api/folders").contentType("application/json")
                                                                      .content(objectMapper.writeValueAsBytes(addFolderRequest)))
                                         .andExpect(status().is(404))
                                         .andReturn();

            logger.info(mvcResult.getResponse()
                                 .getContentAsString());

        }
    }

    @Nested
    @DisplayName("?????? ?????? ??????")
    class SetFolderNameTest {

        @Test
        @DisplayName("?????? ?????????")
        @WithMockMember(id = 1L, email = "marrin1101@naver.com")
        void responseTest() throws Exception {
            UpdateFolderNameRequest setFolderNameRequest = UpdateFolderNameRequest.builder()
                                                                                  .name("f_10L")
                                                                                  .build();

            MvcResult mvcResult = mockMvc.perform(put("/api/folders/1/name").contentType("application/json")
                                                                            .content(objectMapper.writeValueAsString(setFolderNameRequest)))
                                         .andExpect(status().is(200))
                                         .andReturn();

            logger.info(mvcResult.getResponse()
                                 .getContentAsString());
        }

        @Test
        @DisplayName("????????? ???????????? ?????? ?????? ?????? ?????????")
        @WithMockMember(id = 1L, email = "marrin1101@naver.com")
        void NotExistFolderResponseTest() throws Exception {
            UpdateFolderNameRequest setFolderNameRequest = UpdateFolderNameRequest.builder()
                                                                                  .name("f_10L")
                                                                                  .build();

            MvcResult mvcResult = mockMvc.perform(put("/api/folders/100/name").contentType("application/json")
                                                                              .content(objectMapper.writeValueAsString(setFolderNameRequest)))
                                         .andExpect(status().is(409))
                                         .andReturn();

            logger.info(mvcResult.getResponse()
                                 .getContentAsString());
        }

    }

    @Nested
    @DisplayName("?????? ?????? ??????")
    class SetFolderPathTest {

        @Test
        @DisplayName("?????? ?????????")
        @WithMockMember(id = 1L, email = "marrin1101@naver.com")
        void responseTest() throws Exception {
            UpdateFolderPathRequest updateFolderPathRequest = UpdateFolderPathRequest.builder()
                                                                               .targetFolderId(2L)
                                                                               .build();

            MvcResult mvcResult = mockMvc.perform(put("/api/folders/3/path").contentType("application/json")
                                                                            .content(objectMapper.writeValueAsString(updateFolderPathRequest)))
                                         .andExpect(status().is(200))
                                         .andReturn();
        }

        @Test
        @DisplayName("??????????????? ????????? ????????? ?????? ?????? ?????????")
        @WithMockMember(id = 1L, email = "marrin1101@naver.com")
        void LimitFolderDepthTest() throws Exception {
            UpdateFolderPathRequest updateFolderPathRequest = UpdateFolderPathRequest.builder()
                                                                            .targetFolderId(3L)
                                                                            .build();

            mockMvc.perform(put("/api/folders/1/path").contentType("application/json")
                                                                            .content(objectMapper.writeValueAsString(updateFolderPathRequest)))
                                         .andExpect(status().is(409))
                                         .andReturn();
        }

        @Test
        @DisplayName("??????????????? ????????? ?????? ?????? ?????????")
        @WithMockMember(id = 1L, email = "marrin1101@naver.com")
        void NotExistOriginFolderTest() throws Exception {
            UpdateFolderPathRequest updateFolderPathRequest = UpdateFolderPathRequest.builder()
                                                                            .targetFolderId(5L)
                                                                            .build();

            MvcResult mvcResult = mockMvc.perform(put("/api/folders/101/path").contentType("application/json")
                                                                              .content(objectMapper.writeValueAsString(updateFolderPathRequest)))
                                         .andExpect(status().is(409))
                                         .andReturn();

            logger.info(mvcResult.getResponse()
                                 .getContentAsString());
        }

        @Test
        @DisplayName("??????????????? ??????????????? ?????? ?????????")
        @WithMockMember(id = 1L, email = "marrin1101@naver.com")
        void TargetFolderIsDirectDescendantFolderTest() throws Exception {
            UpdateFolderPathRequest setFolderPathRequest = UpdateFolderPathRequest.builder()
                                                                            .targetFolderId(5L)
                                                                            .build();

            MvcResult mvcResult = mockMvc.perform(put("/api/folders/1/path").contentType("application/json")
                                                                            .content(objectMapper.writeValueAsString(setFolderPathRequest)))
                                         .andExpect(status().is(409))
                                         .andReturn();

            logger.info(mvcResult.getResponse()
                                 .getContentAsString());
        }
    }

    @Nested
    @DisplayName("?????? ?????????")
    class DeleteFolderTest {

        @Test
        @DisplayName("?????? ?????????")
        @WithMockMember(id = 1L, email = "marrin1101@naver.com")
        void responseTest() throws Exception {
            MvcResult mvcResult = mockMvc.perform(delete("/api/folders/1"))
                                         .andExpect(status().is(200))
                                         .andReturn();

            logger.info(mvcResult.getResponse()
                                 .getContentAsString());
        }

        @Test
        @DisplayName("????????? ????????? ?????? ?????? ?????? ?????????")
        @WithMockMember(id = 1L, email = "marrin1101@naver.com")
        void NotExistFolderResponseTest() throws Exception {
            MvcResult mvcResult = mockMvc.perform(delete("/api/folders/100"))
                                         .andExpect(status().is(409))
                                         .andReturn();

            logger.info(mvcResult.getResponse()
                                 .getContentAsString());
        }
    }
}
