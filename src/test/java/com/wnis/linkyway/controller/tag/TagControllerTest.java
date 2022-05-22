package com.wnis.linkyway.controller.tag;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnis.linkyway.dto.tag.TagRequest;
import com.wnis.linkyway.exception.error.ErrorResponse;
import com.wnis.linkyway.repository.MemberRepository;
import com.wnis.linkyway.security.testutils.WithMockMember;
import com.wnis.linkyway.service.tag.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;

import static com.wnis.linkyway.utils.ResponseBodyMatchers.responseBody;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class TagControllerTest {
    
    private final static Logger logger = LoggerFactory.getLogger(TagControllerTest.class);
    
    @MockBean
    private TagService tagService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private WebApplicationContext context;
    
    @Autowired
    private MockMvc mockMvc;
    
    @BeforeEach
    private void before() {
        mockMvc =
                MockMvcBuilders
                        .webAppContextSetup(context)
                        .apply(springSecurity())
                        .alwaysDo(print())
                        .build();
    }
    
    
    @Nested
    @DisplayName("1. HttpRequest 테스트")
    class HttpRequestTest {
        
        @Test
        @DisplayName("HttpRequest 성공 테스트")
        @WithMockMember(id = 1L)
        void httpRequestSuccessTest() throws Exception {
            TagRequest tagRequest = TagRequest.builder().tagName("스프링").shareable("true").build();
            
            mockMvc.perform(post("/api/tags").contentType("application/json").content(objectMapper.writeValueAsString(tagRequest))).andExpect(status().isOk());
        }
        
        @Test
        @DisplayName("post 요청 시 HttpRequest 바디가 없는 경우 테스트")
        void httpRequestFail_NoBodyCaseTest() throws Exception {
            TagRequest tagRequest = TagRequest.builder().tagName("스프링").shareable("true").build();
            
            mockMvc.perform(post("/api/tags").contentType("application/json")).andExpect(status().is(400));
            
        }
        
        @Test
        @DisplayName("HttpRequest Http Method를 잘못 요청한 경우 테스트")
        void httpRequestFail_IllegalHttpMethodCaseTest() throws Exception {
            TagRequest tagRequest = TagRequest.builder().tagName("스프링").shareable("true").build();
            
            mockMvc.perform(put("/api/tags").contentType("application/json")).andExpect(status().is(405));
        }
        
        @Test
        @DisplayName("HttpRequest Mapping하지 않는 URI가 들어온 경우 테스트")
        void httpRequestFail_NotFoundCaseTest() throws Exception {
            MvcResult ret = mockMvc.perform(post("/apia/tags").contentType("application/json"))
                    .andExpect(status().is(404))
                    .andReturn();
            
        }
    }
    
    @Nested
    @DisplayName("2. Validation 테스트")
    class ValidationTest {
        @ParameterizedTest
        @CsvSource(value = {"'', ''", "tagName, ''", "' ', shreable"})
        @DisplayName("빈 값 테스트")
        void nullTest(String tagName, String shareable) throws Exception {
            TagRequest tagRequest = TagRequest.builder().tagName(tagName).shareable(shareable).build();
            
            MvcResult mvcResult = mockMvc.perform(post("/api/tags").contentType("application/json").content(objectMapper.writeValueAsString(tagRequest))).andExpect(status().is(400)).andExpect(responseBody().containsPropertiesAsJson(ErrorResponse.class)) // body의
                    .andReturn();
            
        }
        
        @ParameterizedTest
        @CsvSource(value = {"'aa', ' '", "aa, 'true1'", "'aa', false2"})
        @DisplayName("패턴 테스트")
        void patternTest(String tagName, String shareable) throws Exception {
            TagRequest tagRequest = TagRequest.builder().tagName(tagName).shareable(shareable).build();
            
            MvcResult mvcResult = mockMvc.perform(post("/api/tags").contentType("application/json").content(objectMapper.writeValueAsString(tagRequest))).andExpect(status().is(400)).andExpect(responseBody().containsPropertiesAsJson(ErrorResponse.class)) // body의
                    .andReturn();
            
        }
    }
    
    
}