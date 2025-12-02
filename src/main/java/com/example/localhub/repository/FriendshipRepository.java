package com.example.localhub.repository;

import com.example.localhub.domain.friend.Friendship;
import com.example.localhub.domain.friend.FriendshipStatus;
import com.example.localhub.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    // 나에게 온 친구 요청 목록 조회 (상태가 PENDING 인 것)
    List<Friendship> findAllByReceiverAndStatus(Member receiver, FriendshipStatus status);

    // 내 친구 목록 조회 (내가 보냈거나, 내가 받았거나 + 상태가 ACCEPTED 인 것)
    @Query("SELECT f FROM Friendship f WHERE (f.requester = :member OR f.receiver = :member) AND f.status = 'ACCEPTED'")
    List<Friendship> findAllFriends(@Param("member") Member member);

    // 중복 요청 방지용 (A와 B 사이에 관계가 존재하는지 확인)
    boolean existsByRequesterAndReceiver(Member requester, Member receiver);
    boolean existsByReceiverAndRequester(Member requester, Member receiver);
}
