package com.barter.domain.auth.model;

import jakarta.persistence.*;

@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private final String email;

    @Column(nullable = false)
    private final String password;

    @Column(nullable = false)
    private final String username;

    protected Member() {
        // JPA 기본 생성자 (필수)
        this.email = null;
        this.password = null;
        this.username = null;
    }

    public Member(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
}
