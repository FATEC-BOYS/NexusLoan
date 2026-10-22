package com.nexusloan.security;

import java.io.IOException;
import java.util.List;

import com.nexusloan.tenant.TenantContext;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Lê o header {@code Authorization: Bearer <jwt>}, autentica a requisição e coloca o tenant do
 * token no {@link TenantContext}. O tenant vem apenas do token assinado, nunca de header,
 * path ou body, então o cliente não consegue escolher outro tenant.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            AuthenticatedUser user = jwtService.parse(header.substring(BEARER_PREFIX.length()));
            authenticate(user, request);
            TenantContext.set(user.tenantId());
        } catch (JwtException ex) {
            // Sem autenticação: se a rota for protegida, o entry point responde 401.
            log.debug("JWT rejeitado: {}", ex.getMessage());
        }

        try {
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private static void authenticate(AuthenticatedUser user, HttpServletRequest request) {
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.role().name()));
        var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
        authentication.setDetails(request.getRemoteAddr());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }
}
