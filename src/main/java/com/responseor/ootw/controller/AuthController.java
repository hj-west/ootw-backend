package com.responseor.ootw.controller;

import com.responseor.ootw.dto.auth.CheckEmailResponseDto;
import com.responseor.ootw.dto.auth.KakaoLoginRequestDto;
import com.responseor.ootw.dto.auth.LoginRequestDto;
import com.responseor.ootw.dto.auth.PasswordChangeRequestDto;
import com.responseor.ootw.entity.Member;
import com.responseor.ootw.service.MemberService;
import com.responseor.ootw.service.auth.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto loginDto) {
        String token = authService.login(loginDto.getEmail(), loginDto.getPassword());

        return ResponseEntity.ok().body(token);
    }

    @PostMapping("/oauth/kakao")
    public ResponseEntity<String> kakaoLogin(@RequestBody KakaoLoginRequestDto loginRequestDto) {
        String token = authService.kakaoLogin(loginRequestDto.getCode());

        return ResponseEntity.ok().body(token);
    }

    @GetMapping("/check-email")
    public ResponseEntity<CheckEmailResponseDto> checkEmail(@RequestParam("email") String email) {
        Member member = memberService.getMemberByEmail(email);
        boolean snsJoined = member.getPassword().contains("KAKAO");
        boolean issuedCertNumber = false;
        Long mailSeq = 0L;

        if (!snsJoined) {
            mailSeq = authService.issueCertNumber(member.getEmail());
            issuedCertNumber = true;
        }
        return ResponseEntity.ok().body(CheckEmailResponseDto.builder()
                .issuedCertNumber(issuedCertNumber)
                .snsJoined(snsJoined)
                .mailCertSeq(mailSeq)
                .build());
    }

    @GetMapping("/certificate")
    public ResponseEntity<Long> certificateMember(@RequestParam("mailCertSeq") String mailCertSeq, @RequestParam("certNumber") String certNumber) {
        return ResponseEntity.ok().body(authService.checkCertNumber(Long.valueOf(mailCertSeq), certNumber));
    }

    @PostMapping("/password-update")
    public ResponseEntity<Void> changePassword(@RequestBody PasswordChangeRequestDto passwordChangeRequestDto) {
        memberService.updateMemberPassword(passwordChangeRequestDto.getUuid(), passwordChangeRequestDto.getNewPassword(), null);

        return ResponseEntity.ok(null);
    }
}
