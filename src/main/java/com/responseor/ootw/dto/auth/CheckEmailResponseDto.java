package com.responseor.ootw.dto.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CheckEmailResponseDto {
    private boolean snsJoined;
    private boolean issuedCertNumber;
    private Long mailCertSeq;
}
