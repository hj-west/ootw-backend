package com.responseor.ootw.service;

import com.responseor.ootw.config.exception.CustomException;
import com.responseor.ootw.config.exception.ErrorCode;
import com.responseor.ootw.dto.member.MemberClotheRequestDto;
import com.responseor.ootw.dto.member.MemberUpdateRequestDto;
import com.responseor.ootw.entity.ClothesByTemp;
import com.responseor.ootw.entity.Member;
import com.responseor.ootw.entity.enums.Role;
import com.responseor.ootw.repository.ClothesByTempRepository;
import com.responseor.ootw.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {
    private final BCryptPasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final ClothesByTempRepository clothesByTempRepository;

    @Transactional
    @Override
    public Long join(String email, String password, String telNo, Role role) {
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new CustomException(ErrorCode.EMAIL_EXISTS);
        }
        List<String> roles = new ArrayList<>();
        roles.add(Role.USER.role());

        Member member = memberRepository.save(Member.builder()
                        .email(email)
                        .password(passwordEncoder.encode(password))
                        .telNo(telNo)
                .roles(roles)
                .build());

        return member.getUuid();
    }

    @Override
    public Member getMemberInfo(Long uuid) {
        return memberRepository.findByUuid(uuid).orElseThrow(() -> new CustomException(ErrorCode.INCORRECT_MEMBER_INFORMATION));
    }

    @Override
    @Transactional
    public void updateMemberInfo(Long uuid, MemberUpdateRequestDto memberUpdateRequestDto) {
        Member member = memberRepository.findByUuid(uuid).orElseThrow(() -> new CustomException(ErrorCode.INCORRECT_MEMBER_INFORMATION));

        member.setTelNo(memberUpdateRequestDto.getTelNo());
        member.setViewDefault(memberUpdateRequestDto.getViewDefault());
        memberRepository.save(member);
    }

    @Override
    @Transactional
    public void updateMemberPassword(Long uuid, String newPassword, String beforePassword) {
        Member member = memberRepository.findByUuid(uuid).orElseThrow(() -> new CustomException(ErrorCode.INCORRECT_MEMBER_INFORMATION));

        if (beforePassword != null && !passwordEncoder.matches(beforePassword, member.getPassword())) {
            throw new CustomException(ErrorCode.INCORRECT_MEMBER_INFORMATION);
        }
        member.setPassword(passwordEncoder.encode(newPassword));

        memberRepository.save(member);
    }

    @Override
    public List<ClothesByTemp> getMemberClothes(Long uuid) {
        return clothesByTempRepository.findAllByUuid(uuid);
    }

    @Override
    public void addMemberClothes(Long uuid, List<MemberClotheRequestDto> memberClotheRequestDtoList) {
        for (MemberClotheRequestDto memberClotheRequestDto : memberClotheRequestDtoList) {
            ClothesByTemp clothesByTemp = ClothesByTemp.builder()
                    .clothes(memberClotheRequestDto.getClothes())
                    .startTemp(memberClotheRequestDto.getStartTemp())
                    .endTemp(memberClotheRequestDto.getEndTemp())
                    .uuid(uuid)
                    .build();

            clothesByTempRepository.save(clothesByTemp);
        }
    }

    @Override
    @Transactional
    public void deleteMemberClothes(int id, Long uuid) {
        ClothesByTemp clothesByTemp = clothesByTempRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CLOTHES));

        if (!clothesByTemp.getUuid().equals(uuid)) {
            throw new CustomException(ErrorCode.NOT_FOUND_CLOTHES);
        }

        clothesByTempRepository.delete(clothesByTemp);
    }

    @Override
    public Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.UNREGISTERED_MEMBER));
    }
}
