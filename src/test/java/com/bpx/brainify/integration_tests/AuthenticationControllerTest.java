package com.bpx.brainify.integration_tests;

import com.bpx.brainify.models.dtos.UserAuthenticationRequestDTO;
import com.bpx.brainify.models.dtos.UserAuthenticationResponseDTO;
import com.bpx.brainify.models.dtos.UserRegisterRequestDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@Transactional
@AutoConfigureTestDatabase
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Transactional
    @DirtiesContext
    @Test
    void testCreateUserShouldPass() throws Exception {
        UserRegisterRequestDTO userRegisterRequestDTO = UserRegisterRequestDTO.builder()
                .email("user@email.com")
                .password("1234")
                .firstName("User")
                .lastName("Doe")
                .build();
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegisterRequestDTO)))
                .andExpect(status().isOk())
                .andReturn();
        String resultAsString = result.getResponse().getContentAsString();

        UserAuthenticationResponseDTO userAuthenticationResponseDTO = objectMapper.readValue(resultAsString, new TypeReference<UserAuthenticationResponseDTO>() {
        });

        Assertions.assertNotNull(userAuthenticationResponseDTO.getAccessToken());

    }

    @Transactional
    @DirtiesContext
    @Test
    void testCreateUserWithEmptyEmailShouldFail() throws Exception {
        UserRegisterRequestDTO userRegisterRequestDTO = UserRegisterRequestDTO.builder()
                .email("")
                .password("1234")
                .firstName("User")
                .lastName("Doe")
                .build();
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegisterRequestDTO)))
                .andExpect(status().isBadRequest())
                .andReturn();
        String resultAsString = result.getResponse().getContentAsString();

        UserAuthenticationResponseDTO userAuthenticationResponseDTO = objectMapper.readValue(resultAsString, new TypeReference<UserAuthenticationResponseDTO>() {
        });

        Assertions.assertNull(userAuthenticationResponseDTO.getAccessToken());

    }

    @Transactional
    @DirtiesContext
    @Test
    void testAuthenticateUserShouldPass() throws Exception {
        UserRegisterRequestDTO userRegisterRequestDTO = UserRegisterRequestDTO.builder()
                .email("userdoe@email.com")
                .password("12345")
                .firstName("User")
                .lastName("Doe")
                .build();
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegisterRequestDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String registerResultAsString = result.getResponse().getContentAsString();

        UserAuthenticationResponseDTO userAuthenticationResponseDTORegister = objectMapper.readValue(registerResultAsString, new TypeReference<UserAuthenticationResponseDTO>() {
        });

        Thread.sleep(1000); //necessarily to not create the same token while authenticating!
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer " + userAuthenticationResponseDTORegister.getAccessToken()))
                .andExpect(status().isOk());


        UserAuthenticationRequestDTO userAuthenticationRequestDTO = UserAuthenticationRequestDTO.builder()
                .email("userdoe@email.com")
                .password("12345")
                .build();

        MvcResult resultAuth = mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userAuthenticationRequestDTO)))
                .andExpect(status().isOk())
                .andReturn();
        String resultAsString = resultAuth.getResponse().getContentAsString();

        UserAuthenticationResponseDTO userAuthenticationResponseDTO = objectMapper.readValue(resultAsString, new TypeReference<UserAuthenticationResponseDTO>() {
        });

        Assertions.assertNotNull(userAuthenticationResponseDTO.getAccessToken());

    }

    @Transactional
    @DirtiesContext
    @Test
    void testAuthenticateUserWithWrongPasswordShouldFail() throws Exception {
        UserRegisterRequestDTO userRegisterRequestDTO = UserRegisterRequestDTO.builder()
                .email("userdoe@email.com")
                .password("12345")
                .firstName("User")
                .lastName("Doe")
                .build();
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegisterRequestDTO)))
                .andExpect(status().isOk())
                .andReturn();

        String registerResultAsString = result.getResponse().getContentAsString();

        UserAuthenticationResponseDTO userAuthenticationResponseDTORegister = objectMapper.readValue(registerResultAsString, new TypeReference<UserAuthenticationResponseDTO>() {
        });
        Thread.sleep(1000); //necessarily to not create the same token while authenticating!

        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer " + userAuthenticationResponseDTORegister.getAccessToken()))
                .andExpect(status().isOk());

        UserAuthenticationRequestDTO userAuthenticationRequestDTO = UserAuthenticationRequestDTO.builder()
                .email("userdoe@email.com")
                .password("11111")
                .build();

        MvcResult resultAuth = mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userAuthenticationRequestDTO)))
                .andExpect(status().isBadRequest())
                .andReturn();
        String resultAsString = resultAuth.getResponse().getContentAsString();

        UserAuthenticationResponseDTO userAuthenticationResponseDTO = objectMapper.readValue(resultAsString, new TypeReference<UserAuthenticationResponseDTO>() {
        });

        Assertions.assertNull(userAuthenticationResponseDTO.getAccessToken());

    }
}
