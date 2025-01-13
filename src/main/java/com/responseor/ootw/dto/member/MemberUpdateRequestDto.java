package com.responseor.ootw.dto.member;

import com.responseor.ootw.entity.enums.ViewDefault;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberUpdateRequestDto {
    private String telNo;
    private ViewDefault viewDefault;
}
