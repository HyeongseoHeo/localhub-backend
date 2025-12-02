package com.example.localhub.controller;

import com.example.localhub.dto.friend.FriendRequest;
import com.example.localhub.dto.friend.FriendResponse;
import com.example.localhub.security.details.MemberDetailsService;
import com.example.localhub.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    // 1. 친구 목록 조회
    @GetMapping
    public ResponseEntity<List<FriendResponse>> getFriends(
            @AuthenticationPrincipal MemberDetailsService.MemberDetails memberDetails
    ) {
        String userEmail = memberDetails.getMember().getEmail();
        return ResponseEntity.ok(friendService.getFriendList(userEmail));
    }

    // 2. 나에게 온 친구 요청 목록 조회
    @GetMapping("/requests")
    public ResponseEntity<List<FriendResponse>> getFriendRequests(
            @AuthenticationPrincipal MemberDetailsService.MemberDetails memberDetails
    ) {
        String userEmail = memberDetails.getMember().getEmail();
        return ResponseEntity.ok(friendService.getFriendRequests(userEmail));
    }

    // 3. 친구 요청 보내기
    @PostMapping("/requests")
    public ResponseEntity<Void> sendFriendRequest(
            @AuthenticationPrincipal MemberDetailsService.MemberDetails memberDetails,
            @RequestBody FriendRequest dto
    ) {
        String userEmail = memberDetails.getMember().getEmail();
        friendService.sendFriendRequest(userEmail, dto.getEmail());
        return ResponseEntity.ok().build();
    }

    // 4. 친구 요청 수락/거절
    @PostMapping("/requests/{requestId}")
    public ResponseEntity<Void> respondFriendRequest(
            @PathVariable Long requestId,
            @RequestBody Map<String, Boolean> body
    ) {
        Boolean accept = body.getOrDefault("accept", false);
        friendService.respondFriendRequest(requestId, accept);
        return ResponseEntity.ok().build();
    }

    // 5. 친구 삭제
    @DeleteMapping("/{friendId}")
    public ResponseEntity<Void> deleteFriend(
            @PathVariable Long friendId,
            @AuthenticationPrincipal MemberDetailsService.MemberDetails memberDetails
    ) {
        String userEmail = memberDetails.getMember().getEmail();
        friendService.deleteFriend(friendId, userEmail);
        return ResponseEntity.ok().build();
    }
}

