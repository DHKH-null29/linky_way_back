package com.wnis.linkyway.controller.card;

import com.wnis.linkyway.controller.CardController;
import com.wnis.linkyway.dto.card.CardResponse;
import com.wnis.linkyway.entity.Tag;
import com.wnis.linkyway.service.card.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CardController.class,
            excludeFilters = { @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                                                     classes = WebSecurityConfigurerAdapter.class) })
public class PersonalSearchResponseTest {

    @MockBean
    CardService cardService;

    @Autowired
    WebApplicationContext ctx;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // MockMvc 설정
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                                 .addFilters(new CharacterEncodingFilter("UTF-8", true)) // 필터 추가
                                 .alwaysDo(print())
                                 .build();
    }

//    @Nested
//    @DisplayName("개인 검색")
//    class personalSearchTest {
//
//        @Test
//        void shouldReturnCode200Test() throws Exception {
//            Tag tag = Tag.builder()
//                         .isPublic(false)
//                         .name("site")
//                         .build();
//
//            CardResponse cardResponse = CardResponse.builder()
//                                                    .cardId(10L)
//                                                    .title("hello")
//                                                    .link("www.google.com")
//                                                    .content("hello")
//                                                    .tags(Collections.singletonList(tag))
//                                                    .build();
//
//            List<CardResponse> list = new ArrayList<>(Collections.singletonList(cardResponse));
//
//            doReturn(list).when(cardService)
//                          .personalSearchCardByKeyword(any(), any());
//            mockMvc.perform(get("/api/cards/personal/keyword").param("keyword", "hello"))
//                   .andExpect(status().is(200))
//                   .andExpect(jsonPath("$..title").exists())
//                   .andExpect(jsonPath("$..link").exists())
//                   .andExpect(jsonPath("$..content").exists())
//                   .andExpect(jsonPath("$..tags").exists());
//        }
//
//    }

}
