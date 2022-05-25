package com.wnis.linkyway.controller.folder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnis.linkyway.dto.folder.AddFolderRequest;
import com.wnis.linkyway.dto.folder.SetFolderNameRequest;
import com.wnis.linkyway.dto.folder.SetFolderPathRequest;
import com.wnis.linkyway.security.testutils.WithMockMember;
import com.wnis.linkyway.service.folder.FolderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class FolderControllerTest {
    private final static Logger logger = LoggerFactory.getLogger(FolderControllerTest.class);
    
    @MockBean
    private FolderService folderService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private WebApplicationContext context;
    
    @Autowired
    private MockMvc mockMvc;
    
    @BeforeEach
    private void setupMock() {
        mockMvc =
                MockMvcBuilders
                        .webAppContextSetup(context)
                        .apply(springSecurity())
                        .alwaysDo(print())
                        .build();
    }
    
    @Nested
    @DisplayName("HttpRequest 테스트")
    class HttpRequestTest {
        
        @Test
        @DisplayName("성공 테스트")
        @WithMockMember(id = 1L)
        void successTest() throws Exception {
            AddFolderRequest addFolderRequest = AddFolderRequest.builder()
                    .parentFolderId(1L)
                    .name("hello").build();
            
            mockMvc.perform(post("/api/folders")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(addFolderRequest)))
                    .andExpect(status().isOk());
        }
        
        @Test
        @DisplayName("post 요청 시 body 가 없는 경우 테스트")
        @WithMockMember
        void noBodyTest() throws Exception {
            mockMvc.perform(post("/api/folders")
                            .contentType("application/json")
                    )
                    .andExpect(status().is(400));
        }
    }
    
    @Nested
    @DisplayName("Validation 예외 핸들러")
    class ValidationExceptionHandlerTest {
        
        @Test
        @DisplayName("addFolder 테스트")
        @WithMockMember
        void addFolderTest() throws Exception {
            AddFolderRequest addFolderRequest = AddFolderRequest.builder()
                    .build();
            
            mockMvc.perform(post("/api/folders")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(addFolderRequest)))
                    .andExpect(status().is(400));
        }
        
        @Test
        @DisplayName("setFolderName 테스트")
        @WithMockMember
        void setFolderNameTest() throws Exception {
            SetFolderNameRequest setFolderNameRequest = SetFolderNameRequest.builder()
                    .build();
            
            mockMvc.perform(post("/api/folders")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(setFolderNameRequest)))
                    .andExpect(status().is(400));
        }
        
        @Test
        @DisplayName("setFolderName 테스트")
        @WithMockMember
        void setFolderPathTest() throws Exception {
            SetFolderPathRequest setFolderPathRequest = SetFolderPathRequest.builder()
                    .build();
            
            mockMvc.perform(post("/api/folders")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(setFolderPathRequest)))
                    .andExpect(status().is(400));
        }
    }
}