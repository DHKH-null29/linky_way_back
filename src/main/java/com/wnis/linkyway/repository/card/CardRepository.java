package com.wnis.linkyway.repository.card;

import com.wnis.linkyway.entity.Card;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    @Deprecated
    @Query("select c from Card c join c.folder f join f.member m " +
            "where m.id = :memberId and c.isDeleted = false and (c.title like %:keyword% or c.content like %:keyword%) " +
            "order by c.id desc")
    List<Card> findAllCardByKeyword(@Param(value = "keyword") String keyword, @Param(value = "memberId") Long memberId, Pageable pageable);

    @Deprecated
    @Query("select ct.card from CardTag ct join ct.card join ct.tag where ct.tag.id = :tagId and ct.card.isDeleted = false " +
            "order by ct.card.id desc")
    List<Card> findCardsByTagId(@Param(value = "tagId") Long tagId, Pageable pageable);

    @Deprecated
    @Query("select distinct c from CardTag ct join ct.card c join ct.tag t "
            + "where t.id = :tagId and c.isPublic = true and c.isDeleted = false " +
            "order by ct.card desc")
    List<Card> findIsPublicCardsByTagId(@Param(value = "tagId") Long tagId, Pageable pageable);

    @Deprecated
    @Query("select c from Card c join c.folder f where f.id = :folderId and c.isDeleted = false " +
            "order by c.id desc")
    List<Card> findCardsByFolderId(@Param(value = "folderId") Long folderId, Pageable pageable);

    // 삭제 예정 쿼리
    @Deprecated
    @Query("select c from Card c join c.folder f where f.parent.id = :folderId or f.id = :folderId")
    List<Card> findDeepFoldersCardsByFolderId(@Param(value = "folderId") Long folderId);

    @Deprecated
    @Query("select c from Card c join c.folder f where f.member.id = :memberId and c.isDeleted = false order by c.id desc")
    List<Card> findCardsByMemberId(@Param(value = "memberId") Long memberId, Pageable pageable);
    
    @Query("select c from Card c join c.folder f join f.member m " +
            "where c.id = :cardId and m.id = :memberId")
    Optional<Card> findByCardIdAndMemberId(Long cardId, Long memberId);
    
    @Query("select c from Card c " +
            "join c.folder f join f.member m " +
            "where c.id in :ids and m.id = :memberId")
    List<Card> findAllInIdsAndMemberId(@Param(value = "ids") List<Long> ids,
            @Param(value = "memberId") Long memberId);
    
    // cursor Paging
    @Query("select c.id from Card c " +
            "where c.isDeleted = true and c.modifiedBy <= :deletedDateAt " +
            "order by c.id desc")
    Slice<Long> findAllIdToDeletedCardUsingPage(@Param(value = "deletedDateAt") LocalDateTime deletedDateAt, Pageable pageable);

    @Query("select c.id from Card c " +
            "where c.id < :lastId and c.isDeleted = true and c.modifiedBy <= :deletedDateAt " +
            "order by c.id desc")
    Slice<Long> findAllIdToDeletedCardUsingCursorPage(@Param(value = "deletedDateAt") LocalDateTime deletedDateAt,Long lastId, Pageable pageable);
    
    // findAll Cursor Paging
    @Query("select c from Card c order by c.id desc")
    Slice<Card> findAllUsingPage(Pageable pageable);
    
    @Query("select c from Card c " +
            "where c.id < :lastId " +
            "order by c.id desc ")
    Slice<Card> findAllUsingCursorPage(Long lastId, Pageable pageable);
    
    //cursor Paging
    @Query("select c from Card c " +
            "join fetch c.folder f join f.member m " +
            "where c.isDeleted = :isDeleted and m.id = :memberId " +
            "order by c.id desc")
    Slice<Card> findAllByIsDeletedAndMemberIdUsingPage(boolean isDeleted, Long memberId, Pageable pageable);
    
    @Query("select c from Card c " +
            "join fetch c.folder f join f.member m " +
            "where c.id < :lastId and c.isDeleted = :isDeleted and m.id = :memberId " +
            "order by c.id desc")
    Slice<Card> findAllByIsDeletedAndMemberIdUsingCursorPage(boolean isDeleted, Long lastId, Long memberId, Pageable pageable);
    
    @Query("select c from Card c " +
            "join c.folder f " +
            "where f.id in :folderList and c.isDeleted = false")
    List<Card> findAllInFolderIds(List<Long> folderList, Pageable pageable);
}