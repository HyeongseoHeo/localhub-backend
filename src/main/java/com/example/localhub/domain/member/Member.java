package com.example.localhub.domain.member;

import com.example.localhub.domain.board.Comment;
import com.example.localhub.domain.board.Post;
import com.example.localhub.domain.board.PostLike;
import com.example.localhub.domain.board.PostRating;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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
    private String password;  // BCrypt 암호화

    @Column(nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private boolean manager;   // true = 사업자

    @Column(nullable = false)
    @Builder.Default
    private boolean cleanbotOn = true; // 클린봇 기능 사용 여부

    public void toggleCleanbot() {
        this.cleanbotOn = !this.cleanbotOn;
    }

    // 👇 [여기부터 추가] 회원이 삭제될 때 같이 삭제될 데이터들 설정

    // 1. 내가 쓴 게시글 삭제
    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Builder.Default // Builder 패턴 쓸 때 리스트 초기화 방지
    private List<Post> posts = new ArrayList<>();

    // 2. 내가 쓴 댓글 삭제
    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    // 3. 내가 누른 좋아요 삭제
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Builder.Default
    private List<PostLike> likes = new ArrayList<>();

    // 4. 내가 남긴 별점 삭제
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Builder.Default
    private List<PostRating> ratings = new ArrayList<>();
}


