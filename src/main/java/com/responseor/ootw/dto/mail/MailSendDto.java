package com.responseor.ootw.dto.mail;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailSendDto {
    private String targetEmail;
    private String subject;
    private String text;
}