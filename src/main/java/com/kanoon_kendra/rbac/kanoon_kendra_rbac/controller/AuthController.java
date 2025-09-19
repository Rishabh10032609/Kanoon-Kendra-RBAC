package com.kanoon_kendra.rbac.kanoon_kendra_rbac.controller;

import com.kanoon_kendra.rbac.kanoon_kendra_rbac.dto.AuthRequest;
import com.kanoon_kendra.rbac.kanoon_kendra_rbac.dto.AuthResponse;
import com.kanoon_kendra.rbac.kanoon_kendra_rbac.dto.RegisterRequest;
import com.kanoon_kendra.rbac.kanoon_kendra_rbac.dto.RefreshTokenRequest;
import com.kanoon_kendra.rbac.kanoon_kendra_rbac.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserDetails user,
                                       @RequestParam(value = "deviceId", required = false) String deviceId) {
        if (user != null) {
            if (deviceId == null || deviceId.isBlank()) {
                authService.logout(user.getUsername());
            } else {
                authService.logout(user.getUsername(), deviceId);
            }
        }
        return ResponseEntity.noContent().build();
    }
}
