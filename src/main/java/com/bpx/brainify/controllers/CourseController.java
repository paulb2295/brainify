package com.bpx.brainify.controllers;

import com.bpx.brainify.models.dtos.ChapterDTO;
import com.bpx.brainify.models.dtos.CourseDTO;
import com.bpx.brainify.models.dtos.CourseResponseDTO;
import com.bpx.brainify.models.dtos.ModuleDTO;
import com.bpx.brainify.services.interfaces.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<CourseDTO> createCourse(@RequestBody CourseDTO courseDTO, Principal connectedUser) {
        return ResponseEntity.ok(courseService.createCourse(courseDTO, connectedUser));
    }

    @GetMapping
    public ResponseEntity<List<CourseResponseDTO>> findAllCoursesForInstructor(Principal connectedUser) {
        return ResponseEntity.ok(courseService.instructorsCourses(connectedUser));
    }

    @PatchMapping("/{courseId}")
    public ResponseEntity<CourseDTO> editCourse(@RequestBody CourseDTO courseDTO,
                                                @PathVariable Long courseId,
                                                Principal connectedUser) {
        return ResponseEntity.ok(courseService.editCourse(courseDTO, courseId, connectedUser));
    }


}
