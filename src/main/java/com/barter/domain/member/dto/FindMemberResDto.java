    package com.barter.domain.member.dto;

    import lombok.Builder;
    import lombok.Getter;

    @Getter
    @Builder
    public class FindMemberResDto {
        private Long id;
        private String email;
        private String nickname;
        private String profileImageUrl;
        private String address;
    }
