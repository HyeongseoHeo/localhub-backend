package com.example.localhub.dto.board;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class PostRequest {
    private Long memberId;
    // 지역 코드 (예: "SEOUL", "BUSAN")
    private String region;
    // 게시글 내용
    private String content;
    // 광고 여부
    private Boolean ad;
    // 태그
    private List<String> tags;
    // 광고 이미지
    private List<String> images;
    // 지도 관련
    private PlaceResponse place;
}


