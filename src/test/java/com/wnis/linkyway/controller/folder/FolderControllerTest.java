package com.wnis.linkyway.controller.folder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnis.linkyway.controller.FolderController;
import com.wnis.linkyway.dto.folder.AddFolderRequest;
import com.wnis.linkyway.dto.folder.FolderResponse;
import com.wnis.linkyway.dto.folder.UpdateFolderNameRequest;
import com.wnis.linkyway.dto.folder.UpdateFolderPathRequest;
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
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FolderController.class,
        excludeFilters = { @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = WebSecurityConfigurerAdapter.class) })
@AutoConfigureMockMvc
class FolderControllerTest {
    private final static Logger logger = LoggerFactory.getLogger(FolderControllerTest.class);

    @MockBean
    private FolderService folderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext ctx;

    @Autowired
    private MockMvc mockMvc;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                                 .alwaysDo(print())
                                 .build();
    }

    @Nested
    @DisplayName("HttpRequest ?????????")
    class HttpRequestTest {

        @Test
        @DisplayName("?????? ?????????")
        @WithMockMember(id = 1L)
        void successTest() throws Exception {
            AddFolderRequest addFolderRequest = AddFolderRequest.builder()
                                                                .parentFolderId(1L)
                                                                .name("hello")
                                                                .build();

            doReturn(FolderResponse.builder().build()).when(folderService)
                                                                            .addFolder(any(), any());

            mockMvc.perform(post("/api/folders").contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(objectMapper.writeValueAsString(addFolderRequest)))
                   .andExpect(status().isOk());
        }

        @Test
        @DisplayName("post ?????? ??? body ??? ?????? ?????? ?????????")
        @WithMockMember
        void noBodyTest() throws Exception {
            mockMvc.perform(post("/api/folders").contentType("application/json"))
                   .andExpect(status().is(400));
        }
    }

    @Nested
    @DisplayName("Validation ?????? ?????????")
    class ValidationExceptionHandlerTest {

        @Test
        @DisplayName("addFolder ?????????")
        @WithMockMember
        void addFolderTest() throws Exception {
            AddFolderRequest addFolderRequest = AddFolderRequest.builder()
                                                                .build();

            mockMvc.perform(post("/api/folders").contentType("application/json")
                                                .content(objectMapper.writeValueAsString(addFolderRequest)))
                   .andExpect(status().is(400));
        }

        @Test
        @DisplayName("updateFolderName ?????????")
        @WithMockMember
        void updateFolderNameTest() throws Exception {
            UpdateFolderNameRequest updateFolderNameRequest = UpdateFolderNameRequest.builder()
                                                                               .build();

            mockMvc.perform(post("/api/folders").contentType("application/json")
                                                .content(objectMapper.writeValueAsString(updateFolderNameRequest)))
                   .andExpect(status().is(400));
        }

        @Test
        @DisplayName("updateFolderPath ?????????")
        @WithMockMember
        void updateFolderPathTest() throws Exception {
            UpdateFolderPathRequest updateFolderPathRequest = UpdateFolderPathRequest.builder()
                                                                                     .build();

            mockMvc.perform(post("/api/folders").contentType("application/json")
                                                .content(objectMapper.writeValueAsString(updateFolderPathRequest)))
                   .andExpect(status().is(400));
        }
    }
}