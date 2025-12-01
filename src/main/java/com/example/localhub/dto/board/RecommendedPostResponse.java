package com.example.localhub.dto.board;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendedPostResponse {

    private Long id;
    private String title;
    private String author;
    private String content;
    private String thumbnail;
}


