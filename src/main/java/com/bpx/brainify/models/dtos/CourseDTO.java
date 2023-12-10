package com.bpx.brainify.models.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseDTO {
    private Long id;
    @NotBlank(message = "Course name mandatory!")
    private String courseName;
}
