package com.bpx.brainify.services.interfaces;

import com.bpx.brainify.models.dtos.ChapterDTO;
import com.bpx.brainify.models.dtos.CourseDTO;
import com.bpx.brainify.models.dtos.CourseResponseDTO;
import com.bpx.brainify.models.dtos.ModuleDTO;

import java.security.Principal;
import java.util.List;

public interface CourseService {
    CourseDTO createCourse(CourseDTO courseDTO, Principal connectedUser);

    List<CourseResponseDTO> instructorsCourses(Principal connectedUser);


    CourseDTO editCourse(CourseDTO courseDTO, Long courseId);
}
