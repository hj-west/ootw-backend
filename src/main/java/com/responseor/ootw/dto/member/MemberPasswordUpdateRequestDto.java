package com.responseor.ootw.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberPasswordUpdateRequestDto {
    private String newPassword;
    private String beforePassword;
}
