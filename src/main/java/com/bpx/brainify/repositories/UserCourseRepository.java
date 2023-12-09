package com.bpx.brainify.repositories;

import com.bpx.brainify.models.entities.UserCourse;
import com.bpx.brainify.repositories.projections.CourseProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserCourseRepository extends JpaRepository<UserCourse, Long> {

    @Query(value = "SELECT courses.id, courses.course_name, users_courses.enrollment, users_courses.progress\n" +
            "FROM (courses\n" +
            "INNER JOIN users_courses ON courses.id = users_courses.course_id)\n" + //**
            "WHERE users_courses.user_id = :userId", nativeQuery = true)
    List<CourseProjection> findAllCoursesForUser(@Param("userId") Long userId);

    @Query(value = "SELECT * from users_courses WHERE user_id = :userId AND course_id = :courseId", nativeQuery = true)
    Optional<UserCourse> findEnrollmentByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);


    @Query(value = "SELECT courses.id, courses.course_name, users_courses.enrollment, users_courses.progress " +
            "FROM ((users_courses " +
            "INNER JOIN users ON users_courses.user_id = users.id) " +
            "INNER JOIN courses ON users_courses.course_id = courses.id) " +
            "WHERE users.email = :email " +
            "AND (users_courses.progress >= COALESCE(:progress, users_courses.progress) OR :progress IS NULL) " +
            "AND (courses.course_name = COALESCE(:courseName, courses.course_name) OR :courseName IS NULL) " +
            "ORDER BY CASE WHEN :order = 'ASC' THEN courses.course_name END ASC," +
            "         CASE WHEN :order = 'DESC' THEN courses.course_name END DESC",
            nativeQuery = true)
    List<CourseProjection> findCoursesByFiltersOrderName(@Param("email") String email,
                                                         @Param("progress") Integer progress,
                                                         @Param("courseName") String courseName,
                                                         @Param("order") String order);


    @Query(value = "SELECT courses.id, courses.course_name, users_courses.enrollment, users_courses.progress " +
            "FROM ((users_courses " +
            "INNER JOIN users ON users_courses.user_id = users.id) " +
            "INNER JOIN courses ON users_courses.course_id = courses.id) " +
            "WHERE users.email = :email " +
            "AND (users_courses.progress >= COALESCE(:progress, users_courses.progress) OR :progress IS NULL) " +
            "AND (courses.course_name = COALESCE(:courseName, courses.course_name) OR :courseName IS NULL) " +
            "ORDER BY CASE WHEN :order = 'ASC' THEN users_courses.enrollment END ASC," +
            "         CASE WHEN :order = 'DESC' THEN users_courses.enrollment END DESC",
            nativeQuery = true)
    List<CourseProjection> findCoursesByFiltersOrderEnrollment(@Param("email") String email,
                                                               @Param("progress") Integer progress,
                                                               @Param("courseName") String courseName,
                                                               @Param("order") String order);

}
