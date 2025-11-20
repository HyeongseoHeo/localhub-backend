package com.example.localhub.dto.board;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecommendedPostResponse {

    private Long id;
    private String author;    // 작성자
    private String content;   // 앞부분만 잘라서
}


