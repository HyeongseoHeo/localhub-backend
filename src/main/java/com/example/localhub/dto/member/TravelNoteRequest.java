package com.example.localhub.dto.member;

import lombok.Data;
import java.time.LocalDate;

@Data
public class TravelNoteRequest {
    private String title;
    private String place;
    private LocalDate startDate;
    private LocalDate endDate;
    private String content;
}
