package com.wnis.linkyway.service;

import com.wnis.linkyway.dto.card.CardRequest;
import com.wnis.linkyway.entity.Card;
import com.wnis.linkyway.entity.Folder;
import com.wnis.linkyway.repository.CardRepository;
import com.wnis.linkyway.service.card.CardServiceImpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

    @InjectMocks
    private CardServiceImpl cardService;

    @Mock
    private CardRepository cardRepository;

    private final String link = "https://github.com/DHKH-null29/linky_way_back/issues/12";
    private final String title = "카드 조회";
    private final String content = "카드 조회 issue";
    private final boolean shareable = true;
    private final Folder folder = null;

    private Card card() {
        return Card.builder()
                   .id(3L)
                   .link(link)
                   .title(title)
                   .content(content)
                   .shareable(shareable)
                   .folder(folder)
                   .build();
    }

    private CardRequest cardRequest() {
        return CardRequest.builder()
                          .link(link)
                          .title(title)
                          .content(content)
                          .shareable(shareable)
                          .folderId(1L)
                          .build();
    }

    @Test
    @DisplayName("카드(북마크) 추가 성공")
    public void addCardSuccess() {
        // given
        Card card = card();

        CardRequest cardRequest = cardRequest();

        BDDMockito.given(cardRepository.save(any(Card.class))).willReturn(card);

        // when
        Long result = cardService.addCard(cardRequest);

        // then
        assertThat(result).isNotNull();
        verify(cardRepository).save(Mockito.any(Card.class));
    }
}