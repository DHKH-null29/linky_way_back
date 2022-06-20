package com.wnis.linkyway.repository;

import com.wnis.linkyway.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    
    @Query("select c from CardTag ct join ct.card c join c.folder f " +
            "where f.member.id = :memberId and (c.title like %:keyword% or c.content like %:keyword%)")
    List<Card> findAllCardByKeyword(@Param(value = "keyword") String keyword, @Param(value = "memberId") Long memberId);

    @Query("select ct.card from CardTag ct join ct.card join ct.tag where ct.tag.id = :tagId")
    public List<Card> findCardsByTagId(@Param(value = "tagId") Long tagId);

    @Query("select distinct ct.card from CardTag ct join ct.card c join ct.tag t "
            + "where t.id = :tagId and c.isPublic = true")
    public List<Card> findShareableCardsByTagId(@Param(value = "tagId") Long tagId);

    @Query("select c from Card c join c.folder f where f.id = :folderId")
    public List<Card> findCardsByFolderId(@Param(value = "folderId") Long folderId);

    @Query("select c from Card c join c.folder f where f.parent.id = :folderId or f.id = :folderId")
    public List<Card> findDeepFoldersCardsByFolderId(@Param(value = "folderId") Long folderId);

    @Query("select c from Card c join c.folder f where f.member.id = :memberId")
    public List<Card> findCardsByMemberId(@Param(value = "memberId") Long memberId);
    
    @Query("select c.id from Card c " +
            "where c.isDeleted = true and c.modifiedBy <= :deletedDateAt")
    public List<Long> findAllIdToDeletedCard(@Param(value = "deletedDateAt")LocalDateTime deletedDateAt);
}