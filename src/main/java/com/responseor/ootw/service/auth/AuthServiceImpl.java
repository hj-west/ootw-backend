package com.responseor.ootw.service.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.responseor.ootw.config.exception.CustomException;
import com.responseor.ootw.config.exception.ErrorCode;
import com.responseor.ootw.config.jwt.JwtTokenProvider;
import com.responseor.ootw.dto.auth.KakaoTokenResponseDto;
import com.responseor.ootw.dto.auth.KakaoUserInfoResponseDto;
import com.responseor.ootw.entity.MailCertNumber;
import com.responseor.ootw.entity.Member;
import com.responseor.ootw.entity.enums.Role;
import com.responseor.ootw.repository.MailCertNumberRepository;
import com.responseor.ootw.repository.MemberRepository;
import com.responseor.ootw.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final MailCertNumberRepository mailCertNumberRepository;
    private final MailService mailService;

    @Override
    public String login(String email, String password) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.UNREGISTERED_MEMBER));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new CustomException(ErrorCode.INCORRECT_MEMBER_INFORMATION);
        }

        List<String> roles = member.getRoles();

        return jwtTokenProvider.generateToken(member.getUuid(), roles);

    }

    public String kakaoLogin(String code) {
        String accessToken;
        String reqUrl = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=" + "9f69a060918ad406bdd0bc6cbd272655");
            sb.append("&code=").append(code);

            BufferedWriter bw = null;
            try {
                bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
                bw.write(sb.toString());
            } catch (IOException e) {
                log.error("Kakao Token Api Error : {}", e.getMessage(), e);
                throw new RuntimeException(e);
            } finally {
                if (bw != null) {
                    bw.flush();
                }
            }
            String line;
            StringBuilder result = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                while ((line = br.readLine()) != null) {
                    result.append(line);
                }

                ObjectMapper objectMapper = new ObjectMapper();

                log.info(result.toString());

                KakaoTokenResponseDto responseDto = objectMapper.readValue(result.toString(), KakaoTokenResponseDto.class);
                accessToken = responseDto.getAccess_token();

                KakaoUserInfoResponseDto userInfoResponseDto = getKakaoUserInfo(accessToken);

                return getTokenByKakao(userInfoResponseDto.getKakao_account().getEmail());
            } catch (IOException e) {
                log.error("Kakao Token Api Error : {}", e.getMessage(), e);
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            log.error("Kakao Token Api Error : {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public Long issueCertNumber(String email) {

        int length = 6;
        try {
            Random random;
            random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                builder.append(random.nextInt(10));
            }
            String certNumber = builder.toString();

            MailCertNumber mailCertNumber = new MailCertNumber();

            mailCertNumber.setCertNumber(certNumber);
            mailCertNumber.setCertStatus("N");
            mailCertNumber.setEmail(email);

            mailCertNumberRepository.save(mailCertNumber);

            String subject = "OOTW 인증번호 확인";
            String text = "OOTW 인증번호를 확인해주세요.\n\n" +
                    "인증번호 : " + certNumber;

            mailService.sendEmail(email, subject, text);

            return mailCertNumber.getSeq();
        } catch (NoSuchAlgorithmException e) {
            throw new CustomException(ErrorCode.NOT_ISSUED_CERT_NUMBER);
        }
    }

    @Override
    @Transactional
    public Long checkCertNumber(Long mailCertSeq, String certNumber) {
        MailCertNumber mailCertNumber = mailCertNumberRepository.findBySeqAndCertStatus(mailCertSeq, "N").orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CERT_NUMBER));

        if (!mailCertNumber.getCertNumber().equals(certNumber)) {
            throw new CustomException(ErrorCode.INCORRECT_CERT_NUMBER);
        }
        mailCertNumber.setCertStatus("Y");
        mailCertNumberRepository.save(mailCertNumber);

        Member member = memberRepository.findByEmail(mailCertNumber.getEmail()).orElse(new Member());

        return member.getUuid();
    }

    private KakaoUserInfoResponseDto getKakaoUserInfo(String accessToken) {
        String reqURL = "https://kapi.kakao.com/v2/user/me";

        //access_token을 이용하여 사용자 정보 조회
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + accessToken); //전송할 header 작성, access_token전송

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            log.info("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder result = new StringBuilder();

            while ((line = br.readLine()) != null) {
                result.append(line);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            KakaoUserInfoResponseDto responseDto = objectMapper.readValue(result.toString(), KakaoUserInfoResponseDto.class);

            br.close();
            return responseDto;
        } catch (IOException e) {
            log.error("Kakao User Info Api Error : {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private String getTokenByKakao(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElse(null);

        if (member == null) {
            List<String> newRoles = new ArrayList<>();
            newRoles.add(Role.USER.role());

            member = memberRepository.save(Member.builder()
                    .email(email)
                    .password("KAKAO:"+email)
                    .telNo(null)
                    .roles(newRoles)
                    .build());
        }

        List<String> roles = member.getRoles();

        return jwtTokenProvider.generateToken(member.getUuid(), roles);
    }


}
