package com.nexusloan.tenant;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Guarda o tenant da requisição corrente (ThreadLocal).
 *
 * <p>É preenchido pelo filtro JWT a partir da claim {@code tenant} e lido pelo
 * {@link TenantIdentifierResolver} sempre que o Hibernate abre uma sessão.
 */
public final class TenantContext {

    /**
     * Identificador usado quando não há tenant no contexto. Não corresponde a nenhuma linha
     * da tabela {@code tenant}: consultas em entidades isoladas não retornam nada e inserts
     * falham na FK. Ou seja, a ausência de tenant bloqueia o acesso (fail-closed).
     */
    public static final UUID NO_TENANT = new UUID(0L, 0L);

    private static final ThreadLocal<UUID> CURRENT = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void set(UUID tenantId) {
        if (tenantId == null) {
            throw new IllegalArgumentException("tenantId não pode ser nulo");
        }
        CURRENT.set(tenantId);
    }

    public static Optional<UUID> get() {
        return Optional.ofNullable(CURRENT.get());
    }

    public static UUID require() {
        return get().orElseThrow(() -> new IllegalStateException("Nenhum tenant no contexto da requisição"));
    }

    public static void clear() {
        CURRENT.remove();
    }

    /**
     * Executa {@code action} com o tenant informado e depois restaura o tenant anterior.
     * Usado nos fluxos sem JWT (login e onboarding). O tenant precisa estar definido
     * antes de abrir a transação, porque o Hibernate o lê ao abrir a sessão.
     */
    public static <T> T callAs(UUID tenantId, Supplier<T> action) {
        UUID previous = CURRENT.get();
        set(tenantId);
        try {
            return action.get();
        } finally {
            if (previous == null) {
                CURRENT.remove();
            } else {
                CURRENT.set(previous);
            }
        }
    }
}
