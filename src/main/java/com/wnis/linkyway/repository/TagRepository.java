package com.wnis.linkyway.repository;

import com.wnis.linkyway.dto.tag.TagResponse;
import com.wnis.linkyway.entity.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    @Query("select new com.wnis.linkyway.dto.tag.TagResponse(t) "
            + "from Tag t join t.member where t.member.id = :memberId")
    public List<TagResponse> findAllTagList(@Param("memberId") Long memberId);

    @Query("select case when count(t) > 0 then true else false end from Tag t join t.member m "
            + "where m.id = :memberId and t.name = :name")
    public boolean existsByMemberIdAndTagName(@Param(value = "name") String name,
            @Param(value = "memberId") Long memberId);
    
    @Query("select t from Tag t join t.member m " + "where t.id = :tagId and m.id = :memberId")
    public Optional<Tag> findByIdAndMemberId(@Param(value = "memberId") Long memberId,
            @Param(value = "tagId") Long tagId);
    
    @Query("select distinct t "
            + "from Tag t join fetch t.member m " +
            "where t.isPublic = true and t.name like %:tagName%")
    public List<Tag> findAllDistinctTagListLikeTagName(@Param(value = "tagName") String tagName, Pageable pageable);
    
    @Query("select distinct t "
            + "from Tag t join fetch t.member m " +
            "where t.isPublic = true and t.name = :tagName")
    public List<Tag> findAllDistinctTagListByTagName(@Param(value = "tagName") String tagName, Pageable pageable);
    
    @Query("select count(*) from Tag t join t.member m where m.id = :memberId")
    public long countTagByMemberId(@Param(value = "memberId") Long memberId);
}