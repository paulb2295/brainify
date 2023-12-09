package com.bpx.brainify.services.interfaces;

import com.bpx.brainify.models.dtos.CourseDTO;
import com.bpx.brainify.models.dtos.CourseResponseDTO;
import com.bpx.brainify.models.dtos.ModuleDTO;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface UserCourseService {
    String enrollToCourse(Principal connectedUser, Long courseId);

    List<CourseResponseDTO> getMyCourses(Principal connectedUser);

    List<CourseDTO> viewAllCourses();

    String updateProgress(Principal connectedUser, Long courseId, double progress);

    List<CourseResponseDTO> getCoursesBasedOnFilter(Principal connectedUser, Integer progress, String courseName, String order, String orderTime);
}
