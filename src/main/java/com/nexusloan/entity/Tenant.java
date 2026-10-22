package com.nexusloan.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

/**
 * Tenant (organização cliente do SaaS). Esta entidade não é filtrada por tenant.
 *
 * <p>O id é gerado pela aplicação (e não pelo Hibernate) porque no onboarding ele precisa
 * estar no {@code TenantContext} antes de a transação abrir. É isso que permite gravar o
 * tenant e o primeiro usuário ADMIN na mesma transação. Implementa {@link Persistable} para
 * que o Spring Data faça {@code persist} (e não {@code merge}) mesmo com o id já preenchido.
 */
@Getter
@Entity
@Table(name = "tenant")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tenant implements Persistable<UUID> {

    @Id
    private UUID id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 60, unique = true)
    private String slug;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Transient
    @Getter(AccessLevel.NONE)
    private boolean isNew = true;

    public Tenant(UUID id, String name, String slug) {
        this.id = id;
        this.name = name;
        this.slug = slug;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PrePersist
    void onPersist() {
        this.createdAt = Instant.now();
    }

    @PostLoad
    @PostPersist
    void markNotNew() {
        this.isNew = false;
    }
}
