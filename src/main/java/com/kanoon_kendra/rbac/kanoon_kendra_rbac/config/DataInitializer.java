package com.kanoon_kendra.rbac.kanoon_kendra_rbac.config;

import com.kanoon_kendra.rbac.kanoon_kendra_rbac.entity.Role;
import com.kanoon_kendra.rbac.kanoon_kendra_rbac.entity.User;
import com.kanoon_kendra.rbac.kanoon_kendra_rbac.repository.RoleRepository;
import com.kanoon_kendra.rbac.kanoon_kendra_rbac.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username:}")
    private String adminUsername;

    @Value("${app.admin.password:}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        // Ensure roles exist (Flyway seeds, but keep idempotent)
        Role roleUser = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_USER").build()));
        Role roleAdmin = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_ADMIN").build()));

        // Seed admin user if properties provided and user not present
        if (adminUsername != null && !adminUsername.isBlank() && adminPassword != null && !adminPassword.isBlank()) {
            if (!userRepository.existsByUsername(adminUsername)) {
                User admin = User.builder()
                        .username(adminUsername)
                        .password(passwordEncoder.encode(adminPassword))
                        .enabled(true)
                        .roles(Set.of(roleUser, roleAdmin))
                        .build();
                userRepository.save(admin);
                log.info("Seeded admin user: {}", adminUsername);
            }
        } else {
            log.info("Admin user not seeded. Set app.admin.username and app.admin.password to seed.");
        }
    }
}
