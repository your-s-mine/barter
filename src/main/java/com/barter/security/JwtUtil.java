package com.barter.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt_secret}") // application.properties에서 jwt_secret 값 읽어오기
    private String secretKey;

    private final long EXPIRATION_TIME = 1000 * 60 * 60; // 1시간

    public String createToken(String email, String nickname) {
        return Jwts.builder()
                .setSubject(email) // 토큰의 subject로 email 설정
                .claim("nickname", nickname)
                .setIssuedAt(new Date()) // 토큰 발급 시간
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 만료 시간 설정
                .signWith(SignatureAlgorithm.HS256, secretKey) // HS256 알고리즘을 사용해 서명
                .compact();
    }
}
