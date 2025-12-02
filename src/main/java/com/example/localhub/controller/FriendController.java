package com.example.localhub.controller;

import com.example.localhub.dto.friend.FriendRequest;
import com.example.localhub.dto.friend.FriendResponse;
import com.example.localhub.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ResponseEntity<List<FriendResponse>> getFriends(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(friendService.getFriendList(userDetails.getUsername()));
    }

    // 2. 나에게 온 친구 요청 목록 조회
    @GetMapping("/requests")
    public ResponseEntity<List<FriendResponse>> getFriendRequests(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(friendService.getFriendRequests(userDetails.getUsername()));
    }

    // 3. 친구 요청 보내기
    @PostMapping("/requests")
    public ResponseEntity<Void> sendFriendRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody FriendRequest dto) { // import한 DTO 이름과 일치하는지 확인하세요
        friendService.sendFriendRequest(userDetails.getUsername(), dto.getEmail());
        return ResponseEntity.ok().build();
    }

    // 4. 친구 요청 수락/거절 (수정됨!)
    @PostMapping("/requests/{requestId}")
    public ResponseEntity<Void> respondFriendRequest(
            @PathVariable Long requestId,
            @RequestBody Map<String, Boolean> body) {

        // [수정 포인트] 안전하게 가져오기 (값이 없으면 false 처리)
        Boolean accept = body.getOrDefault("accept", false);

        friendService.respondFriendRequest(requestId, accept);
        return ResponseEntity.ok().build();
    }

    // 5. 친구 삭제
    @DeleteMapping("/{friendId}")
    public ResponseEntity<Void> deleteFriend(
            @PathVariable Long friendId,
            @AuthenticationPrincipal UserDetails userDetails) {
        friendService.deleteFriend(friendId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}
