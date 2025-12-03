package com.example.localhub.service;

import com.example.localhub.domain.friend.Friendship;
import com.example.localhub.domain.friend.FriendshipStatus;
import com.example.localhub.domain.member.Member;
import com.example.localhub.dto.friend.FriendResponse;
import com.example.localhub.repository.FriendshipRepository;
import com.example.localhub.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendService {

    private final FriendshipRepository friendshipRepository;
    private final MemberRepository memberRepository;

    // 1. 친구 요청 보내기
    public void sendFriendRequest(String userEmail, String targetEmail) {
        Member requester = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Member receiver = memberRepository.findByEmail(targetEmail)
                .orElseThrow(() -> new IllegalArgumentException("대상 사용자를 찾을 수 없습니다."));

        if (requester.equals(receiver)) {
            throw new IllegalArgumentException("자기 자신에게는 친구 요청을 보낼 수 없습니다.");
        }

        // 이미 친구거나 요청 중인지 확인
        if (friendshipRepository.existsByRequesterAndReceiver(requester, receiver) ||
                friendshipRepository.existsByReceiverAndRequester(requester, receiver)) {
            throw new IllegalArgumentException("이미 친구 요청을 보냈거나 친구 상태입니다.");
        }

        Friendship friendship = Friendship.builder()
                .requester(requester)
                .receiver(receiver)
                .status(FriendshipStatus.PENDING)
                .build();

        friendshipRepository.save(friendship);
    }

    // 2. 받은 친구 요청 목록 조회
    @Transactional(readOnly = true)
    public List<FriendResponse> getFriendRequests(String userEmail) {

        // 에러 메시지 추가
        Member user = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("로그인 된 사용자를 찾을 수 없습니다."));

        return friendshipRepository.findAllByReceiverAndStatus(user, FriendshipStatus.PENDING).stream()
                .map(f -> FriendResponse.from(f.getRequester()))
                .collect(Collectors.toList());
    }

    // 3. 친구 목록 조회
    @Transactional(readOnly = true)
    public List<FriendResponse> getFriendList(String userEmail) {

        Member user = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("로그인 된 사용자를 찾을 수 없습니다."));

        return friendshipRepository.findAllFriends(user).stream()
                .map(f -> {
                    Member friend = f.getRequester().equals(user) ? f.getReceiver() : f.getRequester();
                    return FriendResponse.from(friend);
                })
                .collect(Collectors.toList());
    }

    // 4. 친구 요청 수락/거절
    public void respondFriendRequest(Long friendshipId, boolean accept) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요청입니다."));

        if (accept) {
            friendship.acceptFriendship();
        } else {
            friendshipRepository.delete(friendship);
        }
    }

    // 5. 친구 삭제
    public void deleteFriend(Long friendId, String userEmail) {
        friendshipRepository.deleteById(friendId);
    }
}