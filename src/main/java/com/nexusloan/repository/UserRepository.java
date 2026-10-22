package com.nexusloan.repository;

import java.util.Optional;
import java.util.UUID;

import com.nexusloan.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/** Consultas filtradas automaticamente pelo tenant da sessão (ver {@code @TenantId}). */
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
