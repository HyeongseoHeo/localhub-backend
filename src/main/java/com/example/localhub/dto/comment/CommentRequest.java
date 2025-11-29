package com.example.localhub.dto.comment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequest {
    private String content;
    private Long memberId;
    private Boolean anonymous;
}



