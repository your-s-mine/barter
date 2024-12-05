package com.barter.domain.auth.dto;

public class SignUpResponseDto {

    private final Long id;
    private final String email;
    private final String username;

    // 생성자
    public SignUpResponseDto(Long id, String email, String username) {
        this.id = id;
        this.email = email;
        this.username = username;
    }

    // Getter 메서드
    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }
}
