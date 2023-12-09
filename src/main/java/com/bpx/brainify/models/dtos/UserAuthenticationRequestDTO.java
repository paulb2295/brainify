package com.bpx.brainify.models.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthenticationRequestDTO {
    @NotBlank(message = "Email required!")
    private String email;
    @NotBlank(message = "Password required!")
    private String password;
}
