package com.example.localhub.dto.tour;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@NoArgsConstructor
@ToString
public class NaverBlogResponse {

    // 네이버는 "items"라는 이름으로 결과 리스트를 줍니다.
    private List<Item> items;

    @Getter
    @NoArgsConstructor
    @ToString
    public static class Item {
        private String title;       // 블로그 제목 (HTML 태그 포함됨)
        private String link;        // 블로그 링크 URL
        private String description; // 요약 내용
        private String bloggername; // 블로거 이름
        private String postdate;    // 작성일
    }
}
