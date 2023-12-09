package com.bpx.brainify.controllers;

import com.bpx.brainify.models.dtos.UserChangePasswordRequestDTO;
import com.bpx.brainify.services.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PatchMapping("/password")
    public ResponseEntity<String> changePassword(
            @RequestBody UserChangePasswordRequestDTO request,
            Principal connectedUser
    ) {
        return ResponseEntity.ok(userService.changePassword(request, connectedUser));
    }

    @PatchMapping("/role/{userId}")
    public ResponseEntity<String> changeRole(@PathVariable Long userId,
                                             @RequestParam String newRole,
                                             Principal connectedUser) {
        return ResponseEntity.ok(userService.changeRole(userId, newRole, connectedUser));
    }
}
