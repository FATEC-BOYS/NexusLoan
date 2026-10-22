package com.nexusloan.tenant;

import java.util.Map;
import java.util.UUID;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

/**
 * Informa ao Hibernate o tenant de cada sessão. Com isso, toda entidade que tem um campo
 * {@code @TenantId} recebe automaticamente um {@code where tenant_id = ?} nas consultas
 * (inclusive {@code findById}), e o tenant_id é preenchido automaticamente no insert.
 */
@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver<UUID>, HibernatePropertiesCustomizer {

    @Override
    public UUID resolveCurrentTenantIdentifier() {
        return TenantContext.get().orElse(TenantContext.NO_TENANT);
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, this);
    }
}
