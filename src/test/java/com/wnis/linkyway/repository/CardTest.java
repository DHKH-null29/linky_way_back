package com.wnis.linkyway.repository;

import com.wnis.linkyway.entity.Card;
import com.wnis.linkyway.entity.Folder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CardTest {

    @Autowired
    CardRepository cardRepository;

    @Test
    void CardFolderRelationTest_N_1() {

        Folder folder = Folder.builder()
                .name("folder")
                .build();
        Card card = Card.builder()
                .folder(folder)
                .title("제목")
                .build();

        cardRepository.save(card);

        List<Card>  cardList = cardRepository.findAllIncludesFolder();
        assertThat(cardList.size()).isEqualTo(1);
        assertThat(cardList.get(0).getFolder().getName()).isEqualTo(folder.getName());
        assertThat(cardList.get(0).getFolder().getCards().get(0).getTitle()).isEqualTo(card.getTitle());
    }

}
