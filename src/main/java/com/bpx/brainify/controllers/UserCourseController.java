package com.bpx.brainify.controllers;

import com.bpx.brainify.models.dtos.CourseDTO;
import com.bpx.brainify.models.dtos.CourseResponseDTO;
import com.bpx.brainify.models.dtos.ModuleDTO;
import com.bpx.brainify.services.interfaces.UserCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RequestMapping("/api/users-courses")
@RestController
public class UserCourseController {

    private final UserCourseService userCourseService;

    @PostMapping("/{courseId}")
    public ResponseEntity<String> enrollToCourse(Principal connectedUser, @PathVariable Long courseId) {
        return ResponseEntity.ok(userCourseService.enrollToCourse(connectedUser, courseId));
    }

    @GetMapping
    public ResponseEntity<List<CourseResponseDTO>> findAllCoursesForUser(Principal connectedUser) {
        return ResponseEntity.ok(userCourseService.getMyCourses(connectedUser));
    }

    @GetMapping("/all")
    public ResponseEntity<List<CourseDTO>> viewAllCourses() {
        return ResponseEntity.ok(userCourseService.viewAllCourses());
    }

    @GetMapping("/filter")
    public ResponseEntity<List<CourseResponseDTO>> getFilteredCourses(Principal connectedUser,
                                                                      @RequestParam(required = false) Integer progress,
                                                                      @RequestParam(required = false) String courseName,
                                                                      @RequestParam(required = false) String order,
                                                                      @RequestParam(required = false) String orderType) {
        return ResponseEntity.ok(userCourseService.getCoursesBasedOnFilter(connectedUser, progress, courseName, order, orderType));
    }

    @PatchMapping("/progress/{courseId}")
    public ResponseEntity<String> updateProgress(Principal connectedUser,
                                                 @PathVariable Long courseId,
                                                 @RequestParam double progress) {
        return ResponseEntity.ok(userCourseService.updateProgress(connectedUser, courseId, progress));
    }
}
