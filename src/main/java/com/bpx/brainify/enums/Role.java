package com.bpx.brainify.enums;

import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.bpx.brainify.enums.Permission.*;

@RequiredArgsConstructor
public enum Role {
    ADMIN(
            Set.of(
                    ADMIN_READ,
                    ADMIN_UPDATE,
                    ADMIN_DELETE,
                    ADMIN_CREATE,
                    INSTRUCTOR_READ,
                    INSTRUCTOR_UPDATE,
                    INSTRUCTOR_DELETE,
                    INSTRUCTOR_CREATE
            )
    ),
    INSTRUCTOR(
            Set.of(
                    INSTRUCTOR_READ,
                    INSTRUCTOR_UPDATE,
                    INSTRUCTOR_DELETE,
                    INSTRUCTOR_CREATE
            )
    ),
    STUDENT(Collections.emptySet());

    @Getter
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
