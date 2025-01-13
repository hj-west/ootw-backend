package com.responseor.ootw.entity.enums;

public enum Role {
    USER("USER")
    , MANAGER("MANAGER")
    , ADMIN("ADMIN");

    private final String role;

    Role (String role) {
        this.role = role;
    }

    public String role() {
        return  role;
    }
}
