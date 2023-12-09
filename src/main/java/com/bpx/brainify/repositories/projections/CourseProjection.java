package com.bpx.brainify.repositories.projections;

import java.time.LocalDateTime;

public interface CourseProjection {
    Long getId();

    String getCourse_name();

    LocalDateTime getEnrollment();

    int getProgress();
}
