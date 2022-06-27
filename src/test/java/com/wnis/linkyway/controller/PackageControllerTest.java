package com.wnis.linkyway.controller;

import com.wnis.linkyway.dto.PackageResponse;
import com.wnis.linkyway.service.PackageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PackageController.class,
        excludeFilters = { @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = WebSecurityConfigurerAdapter.class) })
class PackageControllerTest {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @MockBean PackageService packageService;
    
    @Autowired MockMvc mockMvc;
    
    @Autowired WebApplicationContext ctx;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                                 .alwaysDo(print())
                                 .build();
    }
    
    
    @Test
    @DisplayName("응답 형식 테스트")
    void ControllerTest() throws Exception {
        PackageResponse packageResponse = PackageResponse.builder()
                .memberId(1L)
                .tagId(1L)
                .nickname("hello")
                .tagName("t1")
                .numberOfCard(10)
                .build();
        List<PackageResponse> responseList = new ArrayList<>();
        responseList.add(packageResponse);
        doReturn(responseList).when(packageService).findAllPackageByTagName(any(), anyBoolean(), any());
        
        mockMvc.perform(get("/api/search/social?isLike=false").param("tagName", "hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..memberId").isNotEmpty())
                .andExpect(jsonPath("$..nickname").isNotEmpty())
                .andExpect(jsonPath("$..numberOfCard").isNotEmpty())
                .andExpect(jsonPath("$..tagName").isNotEmpty())
                .andExpect(jsonPath("$..tagId").isNotEmpty());
    }
    
}