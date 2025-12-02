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

    // Member 엔티티를 DTO로 변환하는 정적 메서드
    public static FriendResponse from(Member member, Long friendshipId) {
        return FriendResponse.builder()
                .id(friendshipId != null ? friendshipId : member.getId())
                .name(member.getNickname()) // 실제 멤버 필드명에 맞게 수정하세요 (nickname 등)
                .email(member.getEmail())
                .build();
    }
}
