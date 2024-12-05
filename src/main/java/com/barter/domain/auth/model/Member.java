package com.barter.domain.auth.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member") // member 테이블과 매핑
@Getter
@NoArgsConstructor // 기본 생성자 자동 생성 (JPA 요구)
@AllArgsConstructor // 모든 필드를 초기화하는 생성자 자동 생성
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String username;
}
