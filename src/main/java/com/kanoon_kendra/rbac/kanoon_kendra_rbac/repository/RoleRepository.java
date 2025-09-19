package com.kanoon_kendra.rbac.kanoon_kendra_rbac.repository;

import com.kanoon_kendra.rbac.kanoon_kendra_rbac.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
