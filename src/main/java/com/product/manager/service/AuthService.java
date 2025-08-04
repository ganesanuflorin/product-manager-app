package com.product.manager.service;

import com.product.manager.dto.GenericResponse;
import com.product.manager.dto.LoginRequest;
import com.product.manager.dto.RegisterRequest;
import com.product.manager.entity.Role;
import com.product.manager.entity.User;
import com.product.manager.repository.RoleRepository;
import com.product.manager.repository.UserRepository;
import com.product.manager.util.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AuthService {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public GenericResponse<String> login(LoginRequest loginRequest) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.username());

        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password())
        );

        String jwtToken = jwtUtil.generateToken(userDetails);
        return new GenericResponse<>(HttpStatus.OK.value(), true, "Login successful", jwtToken);
    }

    public GenericResponse<Void> register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.username())) {
            return new GenericResponse<>(HttpStatus.BAD_REQUEST.value(), false, "Username already exists", null);
        }

        Set<Role> roles = registerRequest.roles().stream()
                .map(roleName -> roleRepository.findByRoleName(roleName.toUpperCase())
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                .collect(Collectors.toSet());


        User user = new User();
        user.setUsername(registerRequest.username());
        user.setPassword(passwordEncoder.encode(registerRequest.password()));
        user.setRoles(roles);
        userRepository.save(user);

        return new GenericResponse<>(HttpStatus.CREATED.value(), true, "User registered successfully", null);
    }
}
