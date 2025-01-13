package com.responseor.ootw.repository;

import com.responseor.ootw.entity.MailCertNumber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MailCertNumberRepository extends JpaRepository<MailCertNumber, Integer> {
    Optional<MailCertNumber> findBySeqAndCertStatus(Long seq, String certStatus);
}
