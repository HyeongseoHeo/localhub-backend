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
        Member user = memberRepository.findByEmail(userEmail).orElseThrow();

        return friendshipRepository.findAllByReceiverAndStatus(user, FriendshipStatus.PENDING).stream()
                .map(f -> FriendResponse.from(f.getRequester(), f.getId()))
                .collect(Collectors.toList());
    }

    // 3. 친구 목록 조회
    @Transactional(readOnly = true)
    public List<FriendResponse> getFriendList(String userEmail) {
        Member user = memberRepository.findByEmail(userEmail).orElseThrow();

        return friendshipRepository.findAllFriends(user).stream()
                .map(f -> {
                    // 친구 관계에서 '상대방'이 누구인지 판별
                    Member friend = f.getRequester().equals(user) ? f.getReceiver() : f.getRequester();
                    return FriendResponse.from(friend, f.getId());
                })
                .collect(Collectors.toList());
    }

    // 4. 친구 요청 수락/거절
    public void respondFriendRequest(Long friendshipId, boolean accept) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요청입니다."));

        if (accept) {
            friendship.acceptFriendship(); // 상태를 ACCEPTED로 변경
        } else {
            friendshipRepository.delete(friendship); // 거절 시 데이터 삭제
        }
    }

    // 5. 친구 삭제
    public void deleteFriend(Long friendId, String userEmail) {
        // 친구 삭제 로직은 friendshipId를 받거나, 상대방 memberId를 받아서 해당 관계를 찾아 삭제해야 함
        // 여기서는 프론트에서 넘어오는 friendId가 'Friendship 테이블의 PK'라고 가정하고 짭니다.
        // 만약 MemberId라면 로직을 조금 수정해야 합니다.
        friendshipRepository.deleteById(friendId);
    }
}
