package com.example.localhub.dto.member;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberSignupRequest {
    private String email;
    private String password;
    private String nickname;
}

