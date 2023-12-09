package com.bpx.brainify.controllers;

import com.bpx.brainify.models.dtos.UserAuthenticationRequestDTO;
import com.bpx.brainify.models.dtos.UserAuthenticationResponseDTO;
import com.bpx.brainify.models.dtos.UserRegisterRequestDTO;
import com.bpx.brainify.services.implementations.AuthenticationServiceImpl;
import com.bpx.brainify.services.interfaces.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<UserAuthenticationResponseDTO> register(
            @RequestBody UserRegisterRequestDTO request
    ) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<UserAuthenticationResponseDTO> authenticate(
            @RequestBody UserAuthenticationRequestDTO request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        authenticationService.refreshToken(request, response);
    }
}
