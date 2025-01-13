package com.responseor.ootw.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "MAIL_CERT_NUMBER")
public class MailCertNumber extends BaseEntity {
    /**
     * SEQ : 시퀀스
     */
    @Id
    @Column(name = "SEQ")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    /**
     * EMAIL : 이메일
     */
    @Column(name = "EMAIL", length = 45)
    private String email;

    /**
     * CERT_NUMBER : 인증 번호
     */
    @Column(name = "CERT_NUMBER", length = 100)
    private String certNumber;

    /**
     * CERT_STATUS : 인증여부
     */
    @Column(name = "CERT_STATUS", length = 1)
    private String certStatus;
}
