package com.barter.controller;

import com.barter.annotation.CustomUser;
import com.barter.domain.member.entity.Member;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api") // API 경로
public class UserController {

    @GetMapping("/user")
    public String getUserInfo(@CustomUser Member member) {
        // CustomUser로 주입된 Member 객체 사용
        return "Hello, " + member.getNickname();
    }
}
