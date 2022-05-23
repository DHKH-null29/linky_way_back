package com.wnis.linkyway.repository;


import com.wnis.linkyway.dto.tag.TagResponse;
import com.wnis.linkyway.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {
    @Query("select t from Tag t join fetch t.member")
    public List<Tag> findAllIncludesTag();
    
    @Query("select new com.wnis.linkyway.dto.tag.TagResponse(t) " +
            "from Tag t join t.member where t.member.id = :memberId")
    public List<TagResponse> findAllTagList(@Param("memberId") Long memberId);
    
    @Query("select t from Tag t join fetch t.member " +
            "where t.name = :name and t.member.id = :id")
    public Tag findByTagNameAndMemberId(@Param(value = "name") String name,
                                        @Param(value = "id") Long id);
    
    @Modifying
    @Query(value = "insert into tag (name, shareable, views, member_member_id)" +
            "values (:name, :shareable, 0, :member_id)", nativeQuery = true)
    public void addTag(@Param("name") String name,
                       @Param("shareable") boolean shareable,
                       @Param("member_id") Long memberId);
}