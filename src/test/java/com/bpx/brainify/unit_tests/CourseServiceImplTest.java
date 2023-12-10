package com.bpx.brainify.unit_tests;

import com.bpx.brainify.enums.Role;
import com.bpx.brainify.models.dtos.CourseDTO;
import com.bpx.brainify.models.entities.Course;
import com.bpx.brainify.models.entities.Token;
import com.bpx.brainify.models.entities.User;
import com.bpx.brainify.repositories.CourseRepository;
import com.bpx.brainify.repositories.UserCourseRepository;
import com.bpx.brainify.services.implementations.CourseServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
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
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class CourseServiceImplTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserCourseRepository userCourseRepository;

    @InjectMocks
    private CourseServiceImpl courseService;

    @Test
    void createCourseShouldPass(){
        //given
        User user = User.builder()
                .id(1L)
                .email("user@mail.com")
                .password("password")
                .firstName("John")
                .lastName("Doe")
                .role(Role.ADMIN)
                .tokens(new ArrayList<>())
                .userCourse(new HashSet<>())
                .build();

        CourseDTO courseDTO = CourseDTO.builder()
                .id(1L)
                .courseName("Java")
                .build();

        Course course = Course.builder()
                .id(1L)
                .courseName("Java")
                .build();

        Course savedCourseEntity = new Course();

        Mockito.when(objectMapper.convertValue(courseDTO, Course.class)).thenReturn(course);
        Mockito.when(objectMapper.convertValue(savedCourseEntity, CourseDTO.class)).thenReturn(courseDTO);
        Mockito.when(courseRepository.save(course)).thenReturn(savedCourseEntity);

        Principal mockPrincipal = new UsernamePasswordAuthenticationToken(user, null);
        SecurityContextHolder.getContext().setAuthentication((Authentication) mockPrincipal);

        //when
        CourseDTO savedCourseDTO = courseService.createCourse(courseDTO, mockPrincipal);
        //then
        Assertions.assertEquals(courseDTO,savedCourseDTO);
    }


}
