package com.bpx.brainify.services.implementations;

import com.bpx.brainify.exceptions.CourseInstructorOwnershipException;
import com.bpx.brainify.exceptions.NullRequiredValueException;
import com.bpx.brainify.exceptions.ResourceNotFoundException;
import com.bpx.brainify.exceptions.UserHasNoCoursesException;
import com.bpx.brainify.models.dtos.*;
import com.bpx.brainify.models.entities.Course;
import com.bpx.brainify.models.entities.User;
import com.bpx.brainify.models.entities.UserCourse;
import com.bpx.brainify.repositories.CourseRepository;
import com.bpx.brainify.repositories.UserCourseRepository;
import com.bpx.brainify.repositories.projections.CourseProjection;
import com.bpx.brainify.services.interfaces.CourseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final UserCourseRepository userCourseRepository;
    private final ObjectMapper objectMapper;


    @Override
    public CourseDTO createCourse(CourseDTO courseDTO, Principal connectedUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        Course course = objectMapper.convertValue(courseDTO, Course.class);
        Course courseResponse = courseRepository.save(course);
        UserCourse userCourse = UserCourse.builder()
                .user(user)
                .course(courseResponse)
                .progress(0)
                .build();
        userCourseRepository.save(userCourse);
        return objectMapper.convertValue(courseResponse, CourseDTO.class);
    }

    @Override
    public List<CourseResponseDTO> instructorsCourses(Principal connectedUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        List<CourseProjection> myCourses = userCourseRepository.findAllCoursesForUser(user.getId());
        List<CourseResponseDTO> myCoursesDTO = new ArrayList<>();
        for (CourseProjection course : myCourses) {
            myCoursesDTO.add(CourseResponseDTO.builder()
                    .id(course.getId())
                    .courseName(course.getCourse_name())
                    .enrollment(course.getEnrollment())
                    .progress(course.getProgress())
                    .build());
        }
        if (!myCoursesDTO.isEmpty()) {
            return myCoursesDTO;
        } else {
            throw new UserHasNoCoursesException("No courses found for connected user!");
        }
    }

    @Override
    public CourseDTO editCourse(CourseDTO courseDTO, Long courseId, Principal connectedUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        userCourseRepository.findEnrollmentByUserIdAndCourseId(user.getId(),courseId).orElseThrow(
                () -> new CourseInstructorOwnershipException("The user is not the courses instructor!")
        );
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isPresent()) {
            Course course = courseOptional.get();
            if (courseDTO.getCourseName() != null && courseDTO.getCourseName().isEmpty()) {
                course.setCourseName(courseDTO.getCourseName());
                return objectMapper.convertValue(courseRepository.save(course), CourseDTO.class);
            }
            throw new NullRequiredValueException("Invalid value inserted!");
        }
        throw new ResourceNotFoundException("Course does not exist!");
    }
}
