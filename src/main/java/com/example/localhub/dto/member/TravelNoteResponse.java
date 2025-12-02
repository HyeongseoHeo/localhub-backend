package com.example.localhub.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TravelNoteResponse {
    private Long id;
    private String title;
    private String place;
    private LocalDate startDate;
    private LocalDate endDate;
    private String content;
    private LocalDateTime createdAt;
}