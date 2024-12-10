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

    // 토큰 생성
    public String createToken(String email, String nickname) {
        return Jwts.builder()
                .setSubject(email)
                .claim("nickname", nickname)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token); // 토큰 검증
            return true;
        } catch (Exception e) {
            return false; // 유효하지 않은 경우
        }
    }

    // 클레임 추출
    public String extractEmail(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject(); // email 반환
    }

    public String extractNickname(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .get("nickname", String.class); // nickname 반환
    }
}
