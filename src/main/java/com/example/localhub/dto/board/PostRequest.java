package com.example.localhub.dto.board;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class PostRequest {

    // 지역 코드 (예: "SEOUL", "BUSAN")
    private String region;

    // 게시글 내용
    private String content;

    // 광고 여부
    private boolean ad;

    // 태그
    private List<String> tags;
}


