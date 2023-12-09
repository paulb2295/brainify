package com.bpx.brainify.services.interfaces;

import com.bpx.brainify.enums.Role;
import com.bpx.brainify.models.dtos.UserChangePasswordRequestDTO;

import java.security.Principal;

public interface UserService {
    String changePassword(UserChangePasswordRequestDTO request, Principal connectedUser);

    String changeRole(Long userId, String newRole, Principal connectedUser);
}
