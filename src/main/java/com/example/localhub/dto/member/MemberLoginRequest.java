package com.example.localhub.dto.member;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberLoginRequest {
    private String email;
    private String password;
}

