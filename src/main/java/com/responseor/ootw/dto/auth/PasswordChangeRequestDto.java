package com.responseor.ootw.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeRequestDto {
    private Long uuid;
    private String newPassword;
}
