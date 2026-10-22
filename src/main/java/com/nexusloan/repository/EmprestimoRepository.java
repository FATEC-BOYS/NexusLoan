package com.nexusloan.repository;

import java.util.UUID;

import com.nexusloan.entity.Emprestimo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/** Consultas filtradas automaticamente pelo tenant da sessão (ver {@code @TenantId}). */
public interface EmprestimoRepository extends JpaRepository<Emprestimo, UUID> {

    Page<Emprestimo> findByClienteId(UUID clienteId, Pageable pageable);
}
