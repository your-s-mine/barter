    package com.barter.domain.auth.dto;

    import lombok.Builder;
    import lombok.Getter;

    @Getter
    @Builder
    public class MemberInfoResDto {
        private Long id;
        private String email;
        private String nickname;
        private String profileImageUrl;
        private String address;
    }
