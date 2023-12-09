package com.bpx.brainify.services.implementations;

import com.bpx.brainify.enums.Role;
import com.bpx.brainify.exceptions.InvalidRoleException;
import com.bpx.brainify.exceptions.PasswordChangeException;
import com.bpx.brainify.models.dtos.UserChangePasswordRequestDTO;
import com.bpx.brainify.models.entities.User;
import com.bpx.brainify.repositories.UserRepository;
import com.bpx.brainify.services.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public String changePassword(UserChangePasswordRequestDTO request, Principal connectedUser) {

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new PasswordChangeException("Wrong password");
        }
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new PasswordChangeException("Password are not the same");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return "Password successfully changed";
    }

    @Override
    public String changeRole(Long userId, String newRole, Principal connectedUser) {
        var admin = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("This user does not exist!"));
        User responseUser = new User();
        if (admin.getRole().equals(Role.ADMIN)) {
            switch (newRole.toUpperCase()) {
                case "STUDENT" -> user.setRole(Role.STUDENT);
                case "INSTRUCTOR" -> user.setRole(Role.INSTRUCTOR);
                case "ADMIN" -> user.setRole(Role.ADMIN);
                default -> throw new InvalidRoleException("Invalid Role!");
            }
            responseUser = userRepository.save(user);
        }
        return "Role for " + responseUser.getEmail() + " changed to " + responseUser.getRole();
    }
}
