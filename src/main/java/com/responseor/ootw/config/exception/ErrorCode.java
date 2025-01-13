package com.responseor.ootw.config.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    UNREGISTERED_MEMBER(HttpStatus.BAD_REQUEST, "가입 되지 않은 회원 입니다.")
    , INCORRECT_MEMBER_INFORMATION(HttpStatus.BAD_REQUEST, "잘못된 회원 정보 입니다.")
    , EMAIL_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재 하는 이메일 입니다.")
    , WEATHER_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Weather Api Error")
    , NOT_FOUND_CLOTHES(HttpStatus.BAD_REQUEST, "의류 정보를 찾을 수 없습니다.")
    , NOT_ISSUED_CERT_NUMBER(HttpStatus.BAD_REQUEST, "인증번호 발급 중 문제가 발생하였습니다.")
    , NOT_FOUND_CERT_NUMBER(HttpStatus.NOT_FOUND, "인증번호 정보를 찾을 수 없습니다.")
    , INCORRECT_CERT_NUMBER(HttpStatus.NOT_FOUND, "잘못된 인증번호 입니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
