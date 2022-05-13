package com.wnis.linkyway.repository;


import com.wnis.linkyway.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {
    @Query("select t from Tag t join fetch t.member")
    List<Tag> findAllIncludesTag();

}