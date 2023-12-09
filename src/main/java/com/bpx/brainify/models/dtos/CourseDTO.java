package com.bpx.brainify.models.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CourseDTO {
    private Long id;
    @NotBlank(message = "Course name mandatory!")
    private String courseName;
}
