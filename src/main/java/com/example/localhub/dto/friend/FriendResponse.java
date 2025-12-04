package com.example.localhub.dto.friend;

import com.example.localhub.domain.friend.Friendship;
import com.example.localhub.domain.member.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FriendResponse {

    private Long friendId;
    private Long id;
    private String name;
    private String email;
    private String profileImage;
    private String role;

    // Member 엔티티를 DTO로 변환하는 정적 메서드
    public static FriendResponse from(Member member) {
        return FriendResponse.builder()
                .friendId(member.getId())
                .name(member.getNickname())
                .email(member.getEmail())
                .role(member.getRole().name())
                .build();
    }

    public static FriendResponse fromRequest(Friendship friendship) {
        Member requester = friendship.getRequester();
        return FriendResponse.builder()
                .friendId(requester.getId())
                .id(friendship.getId())
                .name(requester.getNickname())
                .email(requester.getEmail())
                .role(requester.getRole().name())
                .build();
    }
}