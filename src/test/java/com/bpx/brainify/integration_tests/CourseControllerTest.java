package com.bpx.brainify.integration_tests;

import com.bpx.brainify.models.dtos.CourseDTO;
import com.bpx.brainify.models.dtos.UserAuthenticationResponseDTO;
import com.bpx.brainify.models.dtos.UserRegisterRequestDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
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
public class CourseControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;



    @Transactional
    @DirtiesContext
    @Test
    void testCreateCourseShouldPass() throws Exception {
        UserRegisterRequestDTO userRegisterRequestDTO = UserRegisterRequestDTO.builder()
                .email("user@email.com")
                .password("1234")
                .firstName("User")
                .lastName("Doe")
                .build();
        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegisterRequestDTO)))
                .andExpect(status().isOk())
                .andReturn();
        String resultAsStringRegister = registerResult.getResponse().getContentAsString();

        UserAuthenticationResponseDTO userAuthenticationResponseDTO = objectMapper.readValue(resultAsStringRegister, new TypeReference<UserAuthenticationResponseDTO>() {
        });

        CourseDTO courseDTO= CourseDTO.builder()
                .courseName("Java")
                .build();

        MvcResult result = mockMvc.perform(post("/api/courses")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseDTO))
                        .header("Authorization", "Bearer " + userAuthenticationResponseDTO.getAccessToken()))
                .andExpect(status().isOk())
                .andReturn();

        String resultAsString = result.getResponse().getContentAsString();

        CourseDTO courseDTOResponse = objectMapper.readValue(resultAsString, new TypeReference<CourseDTO>() {
        });

        Assertions.assertEquals(courseDTOResponse.getCourseName(), courseDTO.getCourseName());
    }

    @Transactional
    @DirtiesContext
    @Test
    void testCreateCourseWithEmptyNameShouldFail() throws Exception {
        UserRegisterRequestDTO userRegisterRequestDTO = UserRegisterRequestDTO.builder()
                .email("user@email.com")
                .password("1234")
                .firstName("User")
                .lastName("Doe")
                .build();
        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegisterRequestDTO)))
                .andExpect(status().isOk())
                .andReturn();
        String resultAsStringRegister = registerResult.getResponse().getContentAsString();

        UserAuthenticationResponseDTO userAuthenticationResponseDTO = objectMapper.readValue(resultAsStringRegister, new TypeReference<UserAuthenticationResponseDTO>() {
        });

        CourseDTO courseDTO= CourseDTO.builder()
                .courseName("")
                .build();

        MvcResult result = mockMvc.perform(post("/api/courses")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseDTO))
                        .header("Authorization", "Bearer " + userAuthenticationResponseDTO.getAccessToken()))
                .andExpect(status().isBadRequest())
                .andReturn();

    }
}
