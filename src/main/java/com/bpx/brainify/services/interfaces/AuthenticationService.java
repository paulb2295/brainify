package com.bpx.brainify.services.interfaces;

import com.bpx.brainify.models.dtos.UserAuthenticationRequestDTO;
import com.bpx.brainify.models.dtos.UserAuthenticationResponseDTO;
import com.bpx.brainify.models.dtos.UserRegisterRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface AuthenticationService {
    UserAuthenticationResponseDTO register(UserRegisterRequestDTO request);

    UserAuthenticationResponseDTO authenticate(UserAuthenticationRequestDTO request);

    void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException;
}
