package com.example.localhub.dto.tour;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@NoArgsConstructor
@ToString
public class TourApiResponse {
    private Response response;

    @Getter
    @NoArgsConstructor
    public static class Response {
        private Body body;
    }

    @Getter
    @NoArgsConstructor
    public static class Body {
        private Items items;
        private int totalCount;
    }

    @Getter
    @NoArgsConstructor
    public static class Items {
        private List<Item> item;
    }

    @Getter
    @NoArgsConstructor
    @ToString
    public static class Item {
        private String title;        // 장소명
        private String addr1;        // 주소
        private String firstimage;   // 대표 이미지 URL
        private String contentid;
        private String contenttypeid;
        private String overview;
        private String mapx;         // 경도
        private String mapy;         // 위도

    }
}