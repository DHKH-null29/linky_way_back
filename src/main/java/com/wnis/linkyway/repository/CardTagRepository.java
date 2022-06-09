package com.wnis.linkyway.repository;

import com.wnis.linkyway.entity.Card;
import com.wnis.linkyway.entity.CardTag;
import com.wnis.linkyway.entity.Tag;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardTagRepository extends JpaRepository<CardTag, Long> {

    Optional<CardTag> findByCardAndTag(Card card, Tag tag);

    void deleteByCardAndTag(Card card, Tag tag);
}