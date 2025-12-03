package com.example.localhub.dto.friend;

import com.example.localhub.domain.member.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FriendResponse {
    private Long id;          // Friendship ID (요청 ID) 또는 Member ID
    private String name;      // 친구 이름
    private String email;     // 친구 이메일
    private String profileImage; // 프로필 이미지 URL

    public static FriendResponse from(Member member, Long friendshipId) {
        return FriendResponse.builder()
                .id(friendshipId != null ? friendshipId : member.getId())
                .name(member.getNickname())
                .email(member.getEmail())
                .build();
    }
}
