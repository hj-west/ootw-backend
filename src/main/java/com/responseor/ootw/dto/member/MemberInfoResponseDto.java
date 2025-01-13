package com.responseor.ootw.dto.member;

import com.responseor.ootw.entity.enums.ViewDefault;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class MemberInfoResponseDto {
    private Long uuid;
    private String email;
    private String tellNo;
    private boolean snsJoined;
    private String snsType;
    private ViewDefault viewDefault;
    private LocalDateTime createDate;
}
