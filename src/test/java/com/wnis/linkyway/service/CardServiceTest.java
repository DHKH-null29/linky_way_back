package com.wnis.linkyway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.wnis.linkyway.dto.card.CardRequest;
import com.wnis.linkyway.entity.Card;
import com.wnis.linkyway.entity.Folder;
import com.wnis.linkyway.repository.CardRepository;
import com.wnis.linkyway.service.CardServiceImpl;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

    @InjectMocks
    private CardServiceImpl cardService;

    @Mock
    private CardRepository cardRepository;

    private final String link = "https://github.com/DHKH-null29/linky_way_back/issues/12";
    private final String link2 = "https://github.com/DHKH-null29/linky_way_back/issues/11";
    private final String title = "카드 조회";
    private final String content = "카드 조회 issue";
    private final boolean shareable = true;
    private final Folder folder = null;

    private final CardRequest card = CardRequest.builder()
                                                .link(link)
                                                .title(title)
                                                .content(content)
                                                .shareable(shareable)
                                                .folderId(1L)
                                                .build();

    @Test
    @DisplayName("카드(북마크) 추가 성공")
    public void addCardSuccess() {
        // given
        doReturn(card().toEntity()).when(cardRepository)
                                   .save(Mockito.any(Card.class));

        // when
        final Long result = cardService.addCard(card());
        System.out.println(result);

        // then
        assertThat(result).isNotNull();
    }

    private CardRequest card() {
        return CardRequest.builder()
                          .link(link2)
                          .title(title)
                          .content(content)
                          .shareable(shareable)
                          .folderId(1L)
                          .build();
    }

}
