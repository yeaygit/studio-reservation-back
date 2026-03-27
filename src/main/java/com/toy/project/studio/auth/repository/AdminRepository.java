package com.toy.project.studio.auth.repository;

import com.toy.project.studio.auth.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findAdminByUsername(String username);
}
