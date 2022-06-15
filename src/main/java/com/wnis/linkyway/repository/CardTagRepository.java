package com.wnis.linkyway.repository;

import com.wnis.linkyway.dto.PackageDto;
import com.wnis.linkyway.dto.tag.TagResponse;
import com.wnis.linkyway.entity.CardTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface CardTagRepository extends JpaRepository<CardTag, Long> {
    
    @Query("select t.id from CardTag ct join ct.card c join ct.tag t " +
            "where c.id = :cardId")
    Set<Long> findAllTagIdByCardId(@Param(value = "cardId") Long cardId);
    
    @Query("select new com.wnis.linkyway.dto.tag.TagResponse(t) from CardTag ct " +
            "join ct.card c join ct.tag t " +
            "where c.id = :cardId")
    List<TagResponse> findAllTagResponseByCardId(@Param(value = "cardId") Long cardId);
    
    @Modifying
    @Query("delete from CardTag ct  " +
            "where ct.id in :cardTags")
    void deleteAllCardTagInIds(@Param(value = "cardTags") List<Long> cardTags);
    
    @Query("select ct.id from CardTag ct join ct.tag t " +
            "where t.id in :tagIdSet")
    List<Long> findAllCardTagIdInTagSet(@Param(value = "tagIdSet") Set<Long> tagIdSet);
    
    
    @Query("select new com.wnis.linkyway.dto.PackageDto(m.id, m.nickname, t.id, t.name) from CardTag ct join ct.tag t " +
            "join t.member m " +
            "where t.name like %:tagName%")
    List<PackageDto> findAllPackageDtoByTagName(@Param(value = "tagName") String tagName);
}