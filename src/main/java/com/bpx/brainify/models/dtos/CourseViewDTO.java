package com.bpx.brainify.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseViewDTO {
    private Long id;
    private String courseName;
    private Set<ModuleDTO> modules;
}
