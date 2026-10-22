package com.nexusloan.repository;

import java.util.UUID;

import com.nexusloan.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

/** Consultas filtradas automaticamente pelo tenant da sessão (ver {@code @TenantId}). */
public interface ClienteRepository extends JpaRepository<Cliente, UUID> {

    boolean existsByDocument(String document);
}
