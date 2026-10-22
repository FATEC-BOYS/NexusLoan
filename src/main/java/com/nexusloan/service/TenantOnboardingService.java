package com.nexusloan.service;

import java.util.UUID;

import com.nexusloan.dto.TenantOnboardingRequest;
import com.nexusloan.dto.TenantOnboardingResponse;
import com.nexusloan.dto.UserResponse;
import com.nexusloan.entity.Tenant;
import com.nexusloan.entity.User;
import com.nexusloan.enums.Role;
import com.nexusloan.exception.ConflictException;
import com.nexusloan.repository.TenantRepository;
import com.nexusloan.repository.UserRepository;
import com.nexusloan.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/** Cria um tenant e o primeiro usuário ADMIN dele, de forma atômica. */
@Service
@RequiredArgsConstructor
public class TenantOnboardingService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TransactionTemplate transactionTemplate;

    public TenantOnboardingResponse onboard(TenantOnboardingRequest request) {
        UUID tenantId = UUID.randomUUID();
        // O tenant entra no contexto antes da transação abrir, para que a sessão do Hibernate
        // já nasça com ele e o ADMIN seja gravado com o tenant_id correto.
        return TenantContext.callAs(tenantId, () -> transactionTemplate.execute(status -> {
            if (tenantRepository.existsBySlug(request.tenantSlug())) {
                throw new ConflictException("Já existe um tenant com o slug '" + request.tenantSlug() + "'");
            }
            Tenant tenant = tenantRepository.save(new Tenant(tenantId, request.tenantName(), request.tenantSlug()));
            User admin = userRepository.save(new User(
                    request.adminName(),
                    EmailNormalizer.normalize(request.adminEmail()),
                    passwordEncoder.encode(request.adminPassword()),
                    Role.ADMIN));
            return new TenantOnboardingResponse(tenant.getId(), tenant.getName(), tenant.getSlug(), UserResponse.from(admin));
        }));
    }
}
