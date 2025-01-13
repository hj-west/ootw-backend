package com.responseor.ootw.repository;

import com.responseor.ootw.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUuid(Long uuid);
    Optional<Member> findByEmail(String email);
}
