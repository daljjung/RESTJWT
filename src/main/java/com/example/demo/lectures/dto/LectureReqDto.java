package com.example.demo.lectures.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LectureReqDto {
    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
    @NotNull
    @JsonFormat(pattern="yyyy-MM-ddHH:mm")
    private LocalDateTime beginEnrollmentDateTime;
    @JsonFormat(pattern="yyyy-MM-ddHH:mm")
    private LocalDateTime closeEnrollmentDateTime;
    @JsonFormat(pattern="yyyy-MM-ddHH:mm")
    private LocalDateTime beginLectureDateTime;
    @JsonFormat(pattern="yyyy-MM-ddHH:mm")
    private LocalDateTime endLectureDateTime;
    private String location;
    @Min(0)
    private int basePrice;
    private int maxPrice;
    private int limitOfEnrollment;
}
