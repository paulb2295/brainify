package com.bpx.brainify.repositories;

import com.bpx.brainify.models.entities.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {

    @Query(value = "SELECT * FROM modules WHERE course_id = :courseId", nativeQuery = true)
    List<Module> findModulesByCourseId(@Param("courseId") Long courseId);

    @Query(value = "SELECT id FROM modules WHERE course_id = :courseId", nativeQuery = true)
    List<Long> findModulesIdsForCourseId(@Param("courseId") Long courseId);
}
