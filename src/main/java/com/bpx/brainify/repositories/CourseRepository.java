package com.bpx.brainify.repositories;

import com.bpx.brainify.models.entities.Course;
import com.bpx.brainify.repositories.projections.CourseProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query(value = "SELECT courses.id, courses.course_name, users_courses.enrollment, users_courses.progress\n" +
            "FROM (courses\n" +
            "INNER JOIN users_courses ON courses.id = users_courses.id)\n" +
            "WHERE users_courses.user_id = :userId", nativeQuery = true)
    List<CourseProjection> findAllCoursesForUser(@Param("userId") Long userId);

    @Query(value = "SELECT user_id FROM users_courses WHERE course_id = :courseId", nativeQuery = true)
    List<Long> userIdsForCourse(@Param("courseId") Long courseId);
}
