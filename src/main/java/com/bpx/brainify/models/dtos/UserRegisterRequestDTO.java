package com.bpx.brainify.models.dtos;

import com.bpx.brainify.enums.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterRequestDTO {
    @NotBlank(message = "Email required!")
    private String email;
    @NotBlank(message = "Password required!")
    private String password;
    @NotBlank(message = "First name required!")
    private String firstName;
    @NotBlank(message = "Last name required!")
    private String lastName;
    private Role role;
}
