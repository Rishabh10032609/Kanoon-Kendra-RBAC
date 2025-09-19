package com.kanoon_kendra.rbac.kanoon_kendra_rbac.repository;

import com.kanoon_kendra.rbac.kanoon_kendra_rbac.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
