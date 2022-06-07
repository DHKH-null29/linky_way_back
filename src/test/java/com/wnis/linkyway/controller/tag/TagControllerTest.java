package com.wnis.linkyway.controller.tag;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnis.linkyway.controller.TagController;
import com.wnis.linkyway.dto.tag.TagRequest;
import com.wnis.linkyway.service.tag.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TagController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurerAdapter.class)
        })
class TagControllerTest {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @MockBean
    TagService tagService;
    
    @Autowired
    MockMvc mockMvc;
    
    @Autowired
    ObjectMapper objectMapper;
    
    @Autowired
    WebApplicationContext ctx;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .alwaysDo(print())
                .build();
    }
    
    @ParameterizedTest
    @CsvSource(value = {
            "a,",
            ",",
            ",true"
    })
    @DisplayName("태그이름 또는 소셜 공유 여부를 입력하지 않는 경우")
    void shouldReturn400_WhenBlankProperty(String tagName, String shareable) throws Exception {
        TagRequest tagRequest = TagRequest.builder().tagName(tagName)
                .shareable(shareable).build();
        
        mockMvc.perform(post("/api/tags/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagRequest)))
                .andExpect(status().is(400));
    }
    
    @ParameterizedTest
    @CsvSource(value = {
            "aaaaaaaaaaaaaaaaaaaaaaaa,true",
            "aa,faaaa",
            "aaaaaaaaaaaaaaaaaaaaaaaa,faaa"
    })
    @DisplayName("패턴 또는 태그 이름 길이를 초과한 경우")
    void shouldReturn400_WhenInvalidPattern(String tagName, String shareable) throws Exception {
        TagRequest tagRequest = TagRequest.builder().tagName(tagName)
                .shareable(shareable).build();
        
        mockMvc.perform(post("/api/tags/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tagRequest)))
                .andExpect(status().is(400));
    }
    
    
}