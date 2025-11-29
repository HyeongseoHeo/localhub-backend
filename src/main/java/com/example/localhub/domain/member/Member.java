package com.example.localhub.domain.member;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;  //BCrypy로 암호화

    @Column(nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private boolean manager;   // true = 사업자, false = 일반유저

    @Column(nullable = false)
    @Builder.Default
    private boolean cleanbotOn = true; // 클린봇 기능 사용 여부

    public void toggleCleanbot() {
        this.cleanbotOn = !this.cleanbotOn; // 설정 껐다 켰다 할 편의 메서드
    }

}


