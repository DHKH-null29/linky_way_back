package com.wnis.linkyway.repository;

import com.wnis.linkyway.entity.Endorse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EndorseRepository extends JpaRepository<Endorse, Long> {
    @Query("select dd from Endorse dd join fetch dd.member")
    List<Endorse> findAllIncludesMember();

    @Query("select dd from Endorse dd join fetch dd.tag")
    List<Endorse> findAllIncludesTag();
}