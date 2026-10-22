package com.nexusloan.service;

import java.util.Optional;
import java.util.UUID;

import com.nexusloan.dto.LoginRequest;
import com.nexusloan.dto.TokenResponse;
import com.nexusloan.entity.User;
import com.nexusloan.repository.TenantRepository;
import com.nexusloan.repository.UserRepository;
import com.nexusloan.security.JwtService;
import com.nexusloan.tenant.TenantContext;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final String INVALID_CREDENTIALS = "Tenant, e-mail ou senha inválidos";

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Hash de uma senha aleatória, gerado na inicialização. Quando o tenant ou o usuário não
     * existem, a senha informada é comparada com ele, para que o tempo de resposta não revele
     * se o e-mail está cadastrado.
     */
    private final String unmatchableHash;

    public AuthService(TenantRepository tenantRepository, UserRepository userRepository,
                       PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.unmatchableHash = passwordEncoder.encode(UUID.randomUUID().toString());
    }

    public TokenResponse login(LoginRequest request) {
        String email = EmailNormalizer.normalize(request.email());

        // Tenant não é entidade isolada: a busca por slug funciona sem tenant no contexto.
        // Já a busca do usuário roda dentro do tenant encontrado.
        Optional<User> user = tenantRepository.findBySlug(request.tenant())
                .flatMap(tenant -> TenantContext.callAs(tenant.getId(), () -> userRepository.findByEmail(email)));

        String hash = user.map(User::getPasswordHash).orElse(unmatchableHash);
        boolean matches = passwordEncoder.matches(request.password(), hash);
        if (user.isEmpty() || !matches) {
            throw new BadCredentialsException(INVALID_CREDENTIALS);
        }
        return TokenResponse.bearer(jwtService.generate(user.get()), jwtService.expirationSeconds());
    }
}
