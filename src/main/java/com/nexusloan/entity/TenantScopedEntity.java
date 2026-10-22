package com.nexusloan.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.TenantId;

/**
 * Base de toda entidade que pertence a um tenant. O {@code tenantId} nunca é atribuído pelo
 * código da aplicação: o Hibernate preenche com o tenant da sessão no insert e filtra por
 * ele em todas as leituras.
 */
@Getter
@MappedSuperclass
public abstract class TenantScopedEntity extends BaseEntity {

    @TenantId
    @Column(name = "tenant_id", nullable = false, updatable = false)
    private UUID tenantId;
}
