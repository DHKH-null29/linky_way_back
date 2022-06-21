package com.wnis.linkyway.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.wnis.linkyway.entity.Card;
import com.wnis.linkyway.entity.CardTag;
import com.wnis.linkyway.entity.Folder;
import com.wnis.linkyway.entity.Member;
import com.wnis.linkyway.entity.Tag;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CardRepositoryFindTest {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private CardTagRepository cardTagRepository;

    private Member savedMember;
    private List<Folder> savedFolders;
    private List<Tag> savedTags;
    private List<Card> savedCards;

    @BeforeEach
    void setUp() {
        // given
        savedMember = memberRepository.save(Member.builder()
                                                  .email("maee@naver.com")
                                                  .nickname("sssee")
                                                  .password("a!aA212341")
                                                  .build());

        Folder folder1 = folderRepository.save(Folder.builder()
                                                     .member(savedMember)
                                                     .depth(1L)
                                                     .name("f1")
                                                     .build());
        Folder folder2 = folderRepository.save(Folder.builder()
                                                     .member(savedMember)
                                                     .depth(2L)
                                                     .name("f2")
                                                     .parent(folder1)
                                                     .build());
        savedFolders = new ArrayList<>(Arrays.asList(folder1, folder2));

        savedTags = tagRepository.saveAll(Arrays.asList(Tag.builder()
                                                           .member(savedMember)
                                                           .name("t1")
                                                           .isPublic(true)
                                                           .build(),
                                                        Tag.builder()
                                                           .member(savedMember)
                                                           .name("t2")
                                                           .isPublic(false)
                                                           .build()));

        Card card1 = Card.builder()
                         .link("https://github.com/DHKH-null29/linky_way_back/issues/12")
                         .title("카드 조회")
                         .content("카드 조회 issue")
                         .isPublic(true)
                         .folder(savedFolders.get(0))
                         .build();

        Card card2 = Card.builder()
                         .link("https://github.com/DHKH-null29/linky_way_back/issues/12")
                         .title("카드 조회")
                         .content("카드 조회 issue")
                         .isPublic(false)
                         .folder(savedFolders.get(1))
                         .build();

        Card card3 = Card.builder()
                         .link("https://github.com/DHKH-null29/linky_way_back/issues/12")
                         .title("카드 조회")
                         .content("카드 조회 issue")
                         .isPublic(true)
                         .folder(savedFolders.get(0))
                         .build();

        Card card4 = Card.builder()
                         .link("https://github.com/DHKH-null29/linky_way_back/issues/12")
                         .title("카드 조회")
                         .content("카드 조회 issue")
                         .isPublic(true)
                         .folder(savedFolders.get(0))
                         .build();

        savedCards = cardRepository.saveAll(Arrays.asList(card1, card2, card3, card4));

        CardTag cardtag1 = CardTag.builder()
                                  .card(savedCards.get(0))
                                  .tag(savedTags.get(0))
                                  .build();

        CardTag cardtag2 = CardTag.builder()
                                  .card(savedCards.get(0))
                                  .tag(savedTags.get(1))
                                  .build();

        CardTag cardtag3 = CardTag.builder()
                                  .card(savedCards.get(1))
                                  .tag(savedTags.get(0))
                                  .build();

        CardTag cardtag4 = CardTag.builder()
                                  .card(savedCards.get(2))
                                  .tag(savedTags.get(1))
                                  .build();
        cardTagRepository.saveAll(Arrays.asList(cardtag1, cardtag2, cardtag3, cardtag4));
    }

    @Test
    @DisplayName("태그 아이디로 카드 목록 조회 성공")
    public void findCardsByTagIdSuccess() {
        // given
        Tag tag = savedTags.get(0);
        // when
        List<Card> cardList = cardRepository.findCardsByTagId(tag.getId());
        // then
        assertThat(cardList.size()).isEqualTo(2);
    }
}