package com.bpx.brainify.services.implementations;

import com.bpx.brainify.enums.Role;
import com.bpx.brainify.enums.TokenType;
import com.bpx.brainify.exceptions.UserEmailExistsException;
import com.bpx.brainify.exceptions.WrongEmailOrPasswordException;
import com.bpx.brainify.models.dtos.UserAuthenticationRequestDTO;
import com.bpx.brainify.models.dtos.UserAuthenticationResponseDTO;
import com.bpx.brainify.models.dtos.UserRegisterRequestDTO;
import com.bpx.brainify.models.entities.User;
import com.bpx.brainify.models.entities.Token;
import com.bpx.brainify.repositories.TokenRepository;
import com.bpx.brainify.repositories.UserRepository;
import com.bpx.brainify.security.JwtService;
import com.bpx.brainify.services.interfaces.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    @Override
    public UserAuthenticationResponseDTO register(UserRegisterRequestDTO request) {
        try {
            var user = User.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.STUDENT) //.role(request.getRole())
                    .build();
            var savedUser = repository.save(user);
            var jwtToken = jwtService.generateToken(extractUserDetails(savedUser),savedUser); //**
            var refreshToken = jwtService.generateRefreshToken(extractUserDetails(savedUser), savedUser); //**
            saveUserToken(savedUser, jwtToken);
            return UserAuthenticationResponseDTO.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .build();
        }catch (DataIntegrityViolationException exception){
            throw  new UserEmailExistsException("An account with this email address already exists!");
        }
    }

    @Override
    public UserAuthenticationResponseDTO authenticate(UserAuthenticationRequestDTO request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            var user = repository.findByEmail(request.getEmail())
                    .orElseThrow();
            var jwtToken = jwtService.generateToken(extractUserDetails(user),user); //**
            var refreshToken = jwtService.generateRefreshToken(extractUserDetails(user),user); //**
            revokeAllUserTokens(user);
            saveUserToken(user, jwtToken);
            return UserAuthenticationResponseDTO.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .build();
        }catch (AuthenticationException exception){
            throw new WrongEmailOrPasswordException("Incorrect email or password!");
        }
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    @Override
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.repository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user); //** (user)
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = UserAuthenticationResponseDTO.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    private Map<String, Object> extractUserDetails(User user) {
        Map<String, Object> userClaims = new HashMap<>();
        userClaims.put("id", user.getId());
        userClaims.put("email", user.getEmail());
        userClaims.put("firstName", user.getFirstName());
        userClaims.put("lastName", user.getLastName());
        userClaims.put("role", user.getRole());
        return userClaims;
    }

}
