package com.bpx.brainify.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {
    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),
    INSTRUCTOR_READ("admin:read"),
    INSTRUCTOR_UPDATE("admin:update"),
    INSTRUCTOR_CREATE("admin:create"),
    INSTRUCTOR_DELETE("admin:delete");

    @Getter
    private final String permission;
}
