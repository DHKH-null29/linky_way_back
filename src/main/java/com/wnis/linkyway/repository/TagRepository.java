package com.wnis.linkyway.repository;


import com.wnis.linkyway.dto.tag.TagResponse;
import com.wnis.linkyway.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {
    
    @Query("select new com.wnis.linkyway.dto.tag.TagResponse(t) " +
            "from Tag t join t.member where t.member.id = :memberId")
    public List<TagResponse> findAllTagList(@Param("memberId") Long memberId);
    
    
    @Query("select case when count(t) > 0 then true else false end from Tag t join t.member m " +
            "where t.name = :name and m.id = :memberId")
    public boolean existsByTagNameAndMemberId(@Param(value = "name") String name,
                                              @Param(value = "memberId") Long memberId);
    
    // 코드 변경에 따라 사용되지 않는 코드가 되어서 추후에 삭제 예정
    @Modifying
    @Query(value = "insert into tag (name, shareable, views, member_member_id)" +
            "values (:name, :shareable, 0, :member_id)", nativeQuery = true)
    public void addTag(@Param("name") String name,
                       @Param("shareable") boolean shareable,
                       @Param("member_id") Long memberId);
}