package com.wnis.linkyway.repository;

import com.wnis.linkyway.entity.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CardRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(CardRepositoryTest.class);
    @Autowired
    CardTagRepository cardTagRepository;
    @Autowired
    EntityManager em;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    FolderRepository folderRepository;
    @Autowired
    private CardRepository cardRepository;

    @Test
    @DisplayName("카드(북마크) 추가 성공")
    public void addCardSuccess() {
        // given
        final Card card = Card.builder()
                              .link("https://github.com/DHKH-null29/linky_way_back/issues/12")
                              .title("카드 조회")
                              .content("카드 조회 issue")
                              .shareable(true)
                              .folder(null)
                              .build();

        // when
        final Card result = cardRepository.save(card);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getLink()).isNotNull();
        assertThat(result.getTitle()).isEqualTo("카드 조회");
        assertThat(result.getContent()).isEqualTo("카드 조회 issue");
        assertThat(result.getShareable()).isEqualTo(true);
        assertThat(result.getFolder()).isNull();
    }

    @Test
    @DisplayName("지정된 카드 조회 성공")
    public void findCardByIdSuccess() {
        // given
        final Card card = Card.builder()
                              .link("https://github.com/DHKH-null29/linky_way_back/issues/12")
                              .title("카드 조회")
                              .content("카드 조회 issue")
                              .shareable(true)
                              .folder(null)
                              .build();
        final Card savedCard = cardRepository.save(card);

        // when
        Card findCard = cardRepository.findById(savedCard.getId())
                                      .orElseThrow(() -> new IllegalArgumentException("Wrong CardId"));

        // then
        cardRepository.findById(findCard.getId())
                      .ifPresent(selectCard -> { // 카드
                          // 존재
                          // 시
                          // 출력
                          System.out.println("card:" + selectCard.getId() + selectCard.getTitle());
                      });

        assertThat(findCard.getLink()).isEqualTo("https://github.com/DHKH-null29/linky_way_back/issues/12");
        assertThat(findCard.getTitle()).isEqualTo("카드 조회");
        assertThat(findCard.getContent()).isEqualTo("카드 조회 issue");
        assertThat(findCard.getShareable()).isEqualTo(true);
        assertThat(findCard.getFolder()).isNull();
    }

    @Test
    @DisplayName("카드(북마크) 수정 성공")
    public void updateCardSuccess() {
        // given
        final Card card = Card.builder()
                              .link("https://github.com/DHKH-null29/linky_way_back/issues/12")
                              .title("카드 조회")
                              .content("카드 조회 issue")
                              .shareable(true)
                              .folder(null)
                              .build();
        final Card savedCard = cardRepository.save(card);

        // when
        Optional<Card> resultCard = cardRepository.findById(savedCard.getId());
        resultCard.ifPresent(selectCard -> {
            selectCard.updateLink("https://github.com/DHKH-null29/linky_way_back/issues/12");
            selectCard.updateTitle("카드 조회2");
            selectCard.updateContent("카드 조회 issue2");
            selectCard.updateShareable(false);
            cardRepository.flush();
        });

        // then
        Optional<Card> result = cardRepository.findById(savedCard.getId());
        assertThat(result.get()
                         .getId()).isEqualTo(savedCard.getId());
        assertThat(result.get()
                         .getLink()).isEqualTo("https://github.com/DHKH-null29/linky_way_back/issues/12");
        assertThat(result.get()
                         .getTitle()).isEqualTo("카드 조회2");
        assertThat(result.get()
                         .getContent()).isEqualTo("카드 조회 issue2");
        assertThat(result.get()
                         .getShareable()).isEqualTo(false);
        assertThat(result.get()
                         .getFolder()).isNull();
    }

    @Test
    void countTheNumberOfSqlCalls_UsingFindAllCardByKeyword() {
        Member member = Member.builder()
                              .nickname("h")
                              .password("1")
                              .email("as@naver.com")
                              .build();
        
        Tag tag1 = Tag.builder()
                      .name("hello")
                      .isPublic(true)
                      .member(member)
                      .build();
        Tag tag2 = Tag.builder()
                      .name("hello1")
                      .isPublic(false)
                      .member(member)
                      .build();
        Tag tag3 = Tag.builder()
                      .name("hello2")
                      .isPublic(false)
                      .member(member)
                      .build();

        Folder folder = Folder.builder()
                              .name("1")
                              .depth(1L)
                              .member(member)
                              .build();

        Card card = Card.builder()
                        .content("asdf")
                        .title("12312")
                        .folder(folder)
                        .link("heel//akdjfwww.")
                        .build();
        Card card2 = Card.builder()
                         .content("asf")
                         .title("1234")
                         .folder(folder)
                         .link("www.google.com")
                         .build();

        CardTag cardTag = CardTag.builder()
                                 .card(card)
                                 .tag(tag1)
                                 .build();
        CardTag cardTag2 = CardTag.builder()
                                  .card(card)
                                  .tag(tag2)
                                  .build();
        CardTag cardTag3 = CardTag.builder()
                                  .card(card2)
                                  .tag(tag3)
                                  .build();

        memberRepository.save(member);
        folderRepository.save(folder);
        cardRepository.save(card);
        cardRepository.save(card2);
        tagRepository.saveAll(Arrays.asList(tag1, tag2, tag3));

        cardTagRepository.save(cardTag2);
        cardTagRepository.save(cardTag3);
        cardTagRepository.saveAndFlush(cardTag);
        em.clear(); // db 호출을 위해 의도적으로 영속성 초기화
        logger.info("{}", em.contains(tag1));
        logger.info("{}", em.contains(card));
        logger.info("{}", em.contains(cardTag));

        List<Card> list = cardRepository.findAllCardByKeyword("as", 1L);
        list.forEach((c) -> {
            logger.info("id: {}", c.getId());
            logger.info("title: {}", c.getTitle());
            logger.info("size: {}", c.getCardTags()
                                     .size());
            logger.info("{}", c.getCardTags()
                               .get(0)
                               .getTag()
                               .getName());
        });
    }
}
