package com.barter.controller;

import com.barter.annotation.CustomUser;
import com.barter.domain.member.entity.Member;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/user") // /user 경로로 변경
    public String getUserInfo(@CustomUser Member member) {
        // CustomUser로 주입된 Member 객체 사용
        return "Hello, " + member.getNickname();
    }
}
