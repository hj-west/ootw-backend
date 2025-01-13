package com.responseor.ootw.controller;

import com.responseor.ootw.config.jwt.JwtTokenProvider;
import com.responseor.ootw.dto.member.*;
import com.responseor.ootw.entity.ClothesByTemp;
import com.responseor.ootw.entity.Member;
import com.responseor.ootw.service.MemberService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "Authentication")
@AllArgsConstructor
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("")
    public ResponseEntity<?> join(@Valid @RequestBody MemberJoinRequestDto memberJoinRequestDto) {
        return ResponseEntity.ok().body(memberService.join(
                memberJoinRequestDto.getEmail()
                , memberJoinRequestDto.getPassword()
                , memberJoinRequestDto.getTelNo()
                , memberJoinRequestDto.getRole()
        ));
    }

    @GetMapping("/my-info")
    public ResponseEntity<MemberInfoResponseDto> memberInfo(HttpServletRequest request) {
        Long uuid = jwtTokenProvider.getUserUuidLoginUser(request);
        boolean snsJoined = false;
        String snsType = null;

        Member member = memberService.getMemberInfo(uuid);
        if (member.getPassword().contains("KAKAO")) {
            snsJoined = true;
            snsType = "KAKAO";
        }

        return ResponseEntity.ok().body(MemberInfoResponseDto.builder()
                        .uuid(member.getUuid())
                        .email(member.getEmail())
                        .tellNo(member.getTelNo())
                        .snsJoined(snsJoined)
                        .snsType(snsType)
                        .viewDefault(member.getViewDefault())
                        .createDate(member.getCreateDate())
                .build());
    }

    @PostMapping("/my-info")
    public ResponseEntity<Void> memberInfoUpdate(HttpServletRequest request
    , @RequestBody MemberUpdateRequestDto memberUpdateRequestDto) {
        Long uuid = jwtTokenProvider.getUserUuidLoginUser(request);

        memberService.updateMemberInfo(uuid, memberUpdateRequestDto);
        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/my-info/password-update")
    public ResponseEntity<Void> updateMemberPassword(HttpServletRequest request
    , @RequestBody MemberPasswordUpdateRequestDto memberPasswordUpdateRequestDto) {
        Long uuid = jwtTokenProvider.getUserUuidLoginUser(request);

        memberService.updateMemberPassword(uuid, memberPasswordUpdateRequestDto.getNewPassword(), memberPasswordUpdateRequestDto.getBeforePassword());

        return ResponseEntity.ok(null);
    }

    @GetMapping("/my-clothes")
    public ResponseEntity<List<ClothesByTemp>> memberClothes(HttpServletRequest request) {
        Long uuid = jwtTokenProvider.getUserUuidLoginUser(request);

        return ResponseEntity.ok().body(memberService.getMemberClothes(uuid));
    }

    @PostMapping("/my-clothes")
    public ResponseEntity<Void> addMemberClothes(HttpServletRequest request
            , @RequestBody List<MemberClotheRequestDto> memberClotheRequestDtoList) {
        Long uuid = jwtTokenProvider.getUserUuidLoginUser(request);

        memberService.addMemberClothes(uuid, memberClotheRequestDtoList);

        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/my-clothes/{id}")
    public ResponseEntity<Void> deleteMemberClothes(@PathVariable("id") int id
            , HttpServletRequest request) {
        Long uuid = jwtTokenProvider.getUserUuidLoginUser(request);

        memberService.deleteMemberClothes(id, uuid);

        return ResponseEntity.ok(null);
    }
}
