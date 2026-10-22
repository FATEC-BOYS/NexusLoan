package com.nexusloan.repository;

import java.util.Optional;
import java.util.UUID;

import com.nexusloan.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant, UUID> {

    Optional<Tenant> findBySlug(String slug);

    boolean existsBySlug(String slug);
}
