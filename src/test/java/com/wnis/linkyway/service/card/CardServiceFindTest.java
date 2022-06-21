package com.wnis.linkyway.service.card;

import com.wnis.linkyway.dto.card.CardResponse;
import com.wnis.linkyway.dto.card.SocialCardResponse;
import com.wnis.linkyway.entity.Card;
import com.wnis.linkyway.entity.Folder;
import com.wnis.linkyway.entity.Member;
import com.wnis.linkyway.entity.Tag;
import com.wnis.linkyway.exception.common.NotAccessableException;
import com.wnis.linkyway.exception.common.ResourceConflictException;
import com.wnis.linkyway.repository.CardRepository;
import com.wnis.linkyway.repository.CardTagRepository;
import com.wnis.linkyway.repository.FolderRepository;
import com.wnis.linkyway.repository.MemberRepository;
import com.wnis.linkyway.repository.TagRepository;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardServiceFindTest {

    @InjectMocks
    private CardServiceImpl cardService;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private FolderRepository folderRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CardTagRepository cardTagRepository;

    private Member member;
    private Folder folder1;
    private List<Card> cardList;
    private Tag tag1, tag2;

    @BeforeEach
    void setUp() {
        // given
        member = Member.builder()
                       .email("maee@naver.com")
                       .nickname("sssee")
                       .password("a!aA212341")
                       .build();
        member.setId(1L);

        folder1 = Folder.builder()
                        .member(member)
                        .depth(1L)
                        .name("f")
                        .build();
        folder1.setId(10L);

        tag1 = Tag.builder()
                  .name("t1")
                  .isPublic(true)
                  .build();
        tag1.setId(100L);

        tag2 = Tag.builder()
                  .name("t2")
                  .isPublic(false)
                  .build();
        tag2.setId(101L);

        Card card1 = Card.builder()
                         .link("https://www.naver.com/")
                         .title("title1")
                         .content("content1")
                         .isPublic(true)
                         .folder(folder1)
                         .build();
        card1.setId(1000L);

        Card card2 = Card.builder()
                         .link("https://github.com/DHKH-null29/linky_way_back/issues/12")
                         .title("카드 조회")
                         .content("카드 조회 issue")
                         .isPublic(true)
                         .folder(folder1)
                         .build();
        card2.setId(1001L);
        cardList = new ArrayList<Card>(Arrays.asList(card1, card2));
    }

}