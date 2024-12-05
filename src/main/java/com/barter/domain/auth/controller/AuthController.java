package com.barter.domain.auth.controller;

import com.barter.domain.auth.dto.SignUpRequestDto;
import com.barter.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody @Valid SignUpRequestDto signUpRequestDto) {
        authService.register(signUpRequestDto);
        return new ResponseEntity<>("회원가입 성공", HttpStatus.CREATED);
    }
}
