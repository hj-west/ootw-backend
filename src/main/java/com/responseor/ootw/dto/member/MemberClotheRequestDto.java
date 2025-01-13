package com.responseor.ootw.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class MemberClotheRequestDto {
    private String clothes;
    private int startTemp;
    private int endTemp;
}
