package com.example.localhub.dto.comment;

import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;

@Getter
@Setter
public class CommentResponse {

    private Long id;
    private String author;
    private String authorId;
    private String content;
    private LocalDateTime timestamp;

    private int likesCount;
    private boolean liked;
    private String role;
}


