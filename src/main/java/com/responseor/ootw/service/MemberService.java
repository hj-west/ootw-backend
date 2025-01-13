package com.responseor.ootw.service;

import com.responseor.ootw.dto.member.MemberClotheRequestDto;
import com.responseor.ootw.dto.member.MemberUpdateRequestDto;
import com.responseor.ootw.entity.ClothesByTemp;
import com.responseor.ootw.entity.Member;
import com.responseor.ootw.entity.enums.Role;

import java.util.List;

public interface MemberService {
    Long join(String email, String password, String telNo, Role role);
    Member getMemberInfo(Long uuid);
    void updateMemberInfo(Long uuid, MemberUpdateRequestDto memberUpdateRequestDto);
    void updateMemberPassword(Long uuid, String newPassword, String beforePassword);
    List<ClothesByTemp> getMemberClothes(Long uuid);
    void addMemberClothes(Long uuid, List<MemberClotheRequestDto> memberClotheRequestDtoList);
    void deleteMemberClothes(int id, Long uuid);
    Member getMemberByEmail(String email);
}
