package com.wnis.linkyway.repository;

import com.wnis.linkyway.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {

    @Query("select c from Card c join fetch c.folder")
    List<Card> findAllIncludesFolder();

    @Query("select distinct c from Card c join fetch c.cardTags join c.folder "
            + "where c.folder.member.id = :memberId and (c.title like %:keyword% or c.content like %:keyword% "
            + "or c.link like %:keyword%)")
    List<Card> findAllCardByKeyword(@Param(value = "keyword") String keyword, @Param(value = "memberId") Long memberId);

    @Query("select ct.card from CardTag ct join ct.card join ct.tag where ct.tag.id = :tagId")
    public List<Card> findCardsByTagId(@Param(value = "tagId") Long tagId);

    @Query("select c from Card c join c.folder f where f.member.id = :memberId and f.id = :folderId")
    public List<Card> findCardsByMemberIdAndFolderId(@Param(value = "memberId") Long memberId,
            @Param(value = "folderId") Long folderId);

    @Query("select c from Card c join c.folder f "
            + "where (f.parent.id = :folderId or f.id = :folderId) and f.member.id = :memberId")
    public List<Card> findLowLevelFoldersCardsByMemberIdAndFolderId(@Param(value = "memberId") Long memberId,
            @Param(value = "folderId") Long folderId);

    @Query("select c from Card c join c.folder f where f.member.id = :memberId")
    public List<Card> findCardsByMemberId(@Param(value = "memberId") Long memberId);
}