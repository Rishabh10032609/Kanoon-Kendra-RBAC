package com.kanoon_kendra.rbac.kanoon_kendra_rbac.service;

import com.kanoon_kendra.rbac.kanoon_kendra_rbac.dto.AuthRequest;
import com.kanoon_kendra.rbac.kanoon_kendra_rbac.dto.AuthResponse;
import com.kanoon_kendra.rbac.kanoon_kendra_rbac.dto.RegisterRequest;
import com.kanoon_kendra.rbac.kanoon_kendra_rbac.entity.Role;
import com.kanoon_kendra.rbac.kanoon_kendra_rbac.entity.User;
import com.kanoon_kendra.rbac.kanoon_kendra_rbac.repository.RoleRepository;
import com.kanoon_kendra.rbac.kanoon_kendra_rbac.repository.UserRepository;
import com.kanoon_kendra.rbac.kanoon_kendra_rbac.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_USER").build()));

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .roles(Set.of(userRole))
                .build();
        userRepository.save(user);

        String accessToken = jwtService.generateToken(user);
        String refreshToken = refreshTokenService.issue(user, request.getDeviceId()).getToken();
        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
        String accessToken = jwtService.generateToken(user);
        String refreshToken = refreshTokenService.issue(user, request.getDeviceId()).getToken();
        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refresh(String refreshToken) {
        if (!refreshTokenService.validate(refreshToken)) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }
        User user = refreshTokenService.getUser(refreshToken);
        String newAccess = jwtService.generateToken(user);
        return new AuthResponse(newAccess, refreshToken);
    }

    public void logout(String username) {
        logout(username, null);
    }

    public void logout(String username, String deviceId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (deviceId == null || deviceId.isBlank()) {
            refreshTokenService.revokeAllFor(user);
        } else {
            refreshTokenService.revokeFor(user, deviceId);
        }
    }
}
