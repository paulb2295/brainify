package com.bpx.brainify.services.implementations;

import com.bpx.brainify.exceptions.InvalidFilterParametersException;
import com.bpx.brainify.exceptions.ResourceNotFoundException;
import com.bpx.brainify.exceptions.UserHasNoCoursesException;
import com.bpx.brainify.models.dtos.CourseDTO;
import com.bpx.brainify.models.dtos.CourseResponseDTO;
import com.bpx.brainify.models.entities.Course;
import com.bpx.brainify.models.entities.User;
import com.bpx.brainify.models.entities.UserCourse;
import com.bpx.brainify.repositories.CourseRepository;
import com.bpx.brainify.repositories.ModuleRepository;
import com.bpx.brainify.repositories.UserCourseRepository;
import com.bpx.brainify.repositories.UserRepository;
import com.bpx.brainify.repositories.projections.CourseProjection;
import com.bpx.brainify.services.interfaces.UserCourseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserCourseServiceImpl implements UserCourseService {

    private final CourseRepository courseRepository;
    private final UserCourseRepository userCourseRepository;
    private final ObjectMapper objectMapper;

    @Override
    public String enrollToCourse(Principal connectedUser, Long courseId) {

        User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isPresent()) {
            Course course = courseOptional.get();
            UserCourse userCourse = UserCourse.builder()
                    .user(user)
                    .course(course)
                    .progress(0)
                    .build();
            UserCourse userCourseResponse = userCourseRepository.save(userCourse);
            return user.getEmail() + " enrolled to " + course.getCourseName() + ".";
        } else {
            throw new ResourceNotFoundException("A course with this ID does not exist!");
        }
    }

    @Override
    public List<CourseResponseDTO> getMyCourses(Principal connectedUser) {
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
    public List<CourseDTO> viewAllCourses() {
        return courseRepository.findAll().stream()
                .map(course -> objectMapper.convertValue(course, CourseDTO.class))
                .toList();
    }

    @Override
    public String updateProgress(Principal connectedUser, Long courseId, double progress) {
        User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        UserCourse userCourse = userCourseRepository.findEnrollmentByUserIdAndCourseId(user.getId(), courseId)
                .orElseThrow(() -> new UserHasNoCoursesException("Current user is not enrolled to selected course!"));
        userCourse.setProgress(progress);
        userCourseRepository.save(userCourse);
        return user.getEmail() + " progress for course updated to " + progress;
    }

    @Override
    public List<CourseResponseDTO> getCoursesBasedOnFilter(Principal connectedUser, Integer progress, String courseName, String order, String orderType) {
        try {
            order = order != null ? order : "ASC";
            orderType = orderType != null ? orderType : "NAME";
            User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
            List<CourseProjection> selectedCourses = new ArrayList<>();
            if (orderType.equalsIgnoreCase("NAME")) {
                selectedCourses = userCourseRepository.findCoursesByFiltersOrderName(user.getEmail(), progress, courseName, order.toUpperCase());
            } else if (orderType.equalsIgnoreCase("ENROLLMENT")) {
                selectedCourses = userCourseRepository.findCoursesByFiltersOrderEnrollment(user.getEmail(), progress, courseName, order.toUpperCase());
            }
            List<CourseResponseDTO> myCoursesDTO = new ArrayList<>();
            for (CourseProjection course : selectedCourses) {
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
                throw new UserHasNoCoursesException("No courses matching search criteria!");
            }
        } catch (Exception exception) {
            throw new InvalidFilterParametersException("Invalid filters!");
        }
    }
}
