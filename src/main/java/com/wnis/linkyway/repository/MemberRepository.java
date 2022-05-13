package com.wnis.linkyway.repository;


import com.wnis.linkyway.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}