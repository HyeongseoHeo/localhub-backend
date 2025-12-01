package com.example.localhub.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopRegionStatResponse {
    private String regionCode;
    private Long visitCount;
}
