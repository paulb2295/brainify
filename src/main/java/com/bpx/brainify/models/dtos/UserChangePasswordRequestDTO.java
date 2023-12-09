package com.bpx.brainify.models.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserChangePasswordRequestDTO {
    @NotBlank(message = "Current Password required!")
    private String currentPassword;
    @NotBlank(message = "New Password required!")
    private String newPassword;
    @NotBlank(message = "Confirm new Password required!")
    private String confirmationPassword;
}
