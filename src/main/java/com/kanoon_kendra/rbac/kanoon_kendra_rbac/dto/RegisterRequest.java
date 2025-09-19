package com.kanoon_kendra.rbac.kanoon_kendra_rbac.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @Size(max = 100)
    private String deviceId; // optional device identifier
}
