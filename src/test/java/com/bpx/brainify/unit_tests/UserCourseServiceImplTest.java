package com.bpx.brainify.unit_tests;

import com.bpx.brainify.enums.Role;
import com.bpx.brainify.models.dtos.CourseDTO;
import com.bpx.brainify.models.entities.Course;
import com.bpx.brainify.models.entities.User;
import com.bpx.brainify.repositories.CourseRepository;
import com.bpx.brainify.repositories.UserCourseRepository;
import com.bpx.brainify.services.implementations.CourseServiceImpl;
import com.bpx.brainify.services.implementations.UserCourseServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UserCourseServiceImplTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserCourseRepository userCourseRepository;

    @InjectMocks
    private UserCourseServiceImpl userCourseService;
    @InjectMocks
    private CourseServiceImpl courseService;


    @Test
    public void testViewAllCoursesShouldPass() {
        // Arrange
        Course course1 = new Course(1L, "Java", null,null);
        Course course2 = new Course(2L, "C#", null, null);

        List<Course> courseList = Arrays.asList(course1, course2);

        Mockito.when(courseRepository.findAll()).thenReturn(courseList);

        CourseDTO courseDTO1 = new CourseDTO(1L, "Java");
        CourseDTO courseDTO2 = new CourseDTO(2L, "C#");

        Mockito.when(objectMapper.convertValue(course1, CourseDTO.class)).thenReturn(courseDTO1);
        Mockito.when(objectMapper.convertValue(course2, CourseDTO.class)).thenReturn(courseDTO2);

        // Act
        List<CourseDTO> result = userCourseService.viewAllCourses();

        // Assert
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(result.get(0).getCourseName(),courseList.get(0).getCourseName());
    }

}
