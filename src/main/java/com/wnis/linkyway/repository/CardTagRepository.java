package com.wnis.linkyway.repository;


import com.wnis.linkyway.entity.CardTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CardTagRepository extends JpaRepository<CardTag, Long> {

    @Query("select ct from CardTag ct join fetch ct.card")
    List<CardTag> findAllIncludesCard();

    @Query("select ct from CardTag ct join fetch ct.tag")
    List<CardTag> findAllIncludesTag();
}