package com.responseor.ootw.service.auth;

public interface AuthService {
    String login(String email, String password);
    String kakaoLogin(String code);
    Long issueCertNumber(String email);
    Long checkCertNumber(Long mailCertSeq, String certNumber);
}
