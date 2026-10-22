package com.nexusloan.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexusloan.support.TestDocuments;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/** Sobe a aplicação inteira com um PostgreSQL real e exercita a API pela camada HTTP. */
@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class MultiTenantIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void jwtSecret(DynamicPropertyRegistry registry) {
        byte[] secret = new byte[48];
        new SecureRandom().nextBytes(secret);
        registry.add("nexusloan.security.jwt.secret", () -> Base64.getEncoder().encodeToString(secret));
    }

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String tokenA;
    private String tokenB;

    @BeforeEach
    void onboardTwoTenants() throws Exception {
        tokenA = onboardAndLogin();
        tokenB = onboardAndLogin();
    }

    @Test
    void fullFlowCreatesClienteAndDecidesLoans() throws Exception {
        String clienteId = createCliente(tokenA, "5000.00");

        perform(post("/api/emprestimos"), tokenA, Map.of("clienteId", clienteId, "amount", "50000.00"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty())
                .andExpect(jsonPath("$.amount").value(50000.00))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.reason").value(startsWith("Aprovado")));

        String rejectedId = json(perform(post("/api/emprestimos"), tokenA,
                Map.of("clienteId", clienteId, "amount", "50000.01"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("REJECTED"))
                .andExpect(jsonPath("$.reason").value(startsWith("Reprovado"))))
                .get("id").asText();

        perform(get("/api/emprestimos/{id}", rejectedId), tokenA, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").value(clienteId));
        perform(get("/api/emprestimos/cliente/{id}", clienteId), tokenA, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));
        perform(get("/api/emprestimos"), tokenA, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void tenantCannotSeeOrUseAnotherTenantsData() throws Exception {
        String clienteA = createCliente(tokenA, "3000.00");
        String emprestimoA = json(perform(post("/api/emprestimos"), tokenA,
                Map.of("clienteId", clienteA, "amount", "1000.00"))
                .andExpect(status().isCreated()))
                .get("id").asText();

        perform(get("/api/clientes/{id}", clienteA), tokenB, null).andExpect(status().isNotFound());
        perform(get("/api/emprestimos/{id}", emprestimoA), tokenB, null).andExpect(status().isNotFound());
        perform(get("/api/emprestimos/cliente/{id}", clienteA), tokenB, null).andExpect(status().isNotFound());
        perform(get("/api/clientes"), tokenB, null).andExpect(jsonPath("$.totalElements").value(0));
        perform(get("/api/emprestimos"), tokenB, null).andExpect(jsonPath("$.totalElements").value(0));
        perform(post("/api/emprestimos"), tokenB, Map.of("clienteId", clienteA, "amount", "10.00"))
                .andExpect(status().isNotFound());

        perform(get("/api/clientes/{id}", clienteA), tokenA, null).andExpect(status().isOk());
    }

    @Test
    void sameDocumentIsAllowedInDifferentTenantsButNotTwiceInTheSameTenant() throws Exception {
        Map<String, String> body = clienteBody(TestDocuments.randomCpf(), "1000.00");

        perform(post("/api/clientes"), tokenA, body).andExpect(status().isCreated());
        perform(post("/api/clientes"), tokenB, body).andExpect(status().isCreated());
        perform(post("/api/clientes"), tokenA, body).andExpect(status().isConflict());
    }

    @Test
    void userRoleCannotManageUsers() throws Exception {
        String email = "user-" + UUID.randomUUID() + "@example.com";
        String password = UUID.randomUUID().toString();
        perform(post("/api/users"), tokenA, Map.of("name", "Operador", "email", email, "password", password, "role", "USER"))
                .andExpect(status().isCreated());

        perform(get("/api/users"), tokenA, null).andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2));
        String userToken = login(currentSlugA, email, password);

        perform(post("/api/users"), userToken,
                Map.of("name", "X", "email", "x-" + UUID.randomUUID() + "@example.com", "password", password, "role", "ADMIN"))
                .andExpect(status().isForbidden());
        perform(post("/api/clientes"), userToken, clienteBody(TestDocuments.randomCpf(), "100.00"))
                .andExpect(status().isCreated());
    }

    @Test
    void rejectsMissingOrInvalidToken() throws Exception {
        mvc.perform(get("/api/clientes")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/clientes").header(HttpHeaders.AUTHORIZATION, "Bearer invalid"))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/actuator/health")).andExpect(status().isOk());
    }

    @Test
    void rejectsWrongPassword() throws Exception {
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "tenant", currentSlugA, "email", currentAdminEmailA, "password", "wrong-password"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void validatesRequestBody() throws Exception {
        perform(post("/api/clientes"), tokenA, clienteBody("12345678900", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(2)));
    }

    // ---------------------------------------------------------------- helpers

    private String currentSlugA;
    private String currentAdminEmailA;

    private String onboardAndLogin() throws Exception {
        String slug = "tenant-" + UUID.randomUUID().toString().substring(0, 8);
        String email = "admin@" + slug + ".example.com";
        String password = UUID.randomUUID().toString();
        mvc.perform(post("/api/tenants").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "tenantName", "Tenant " + slug,
                                "tenantSlug", slug,
                                "adminName", "Admin",
                                "adminEmail", email,
                                "adminPassword", password))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.admin.role").value("ADMIN"));
        if (currentSlugA == null) {
            currentSlugA = slug;
            currentAdminEmailA = email;
        }
        return login(slug, email, password);
    }

    private String login(String slug, String email, String password) throws Exception {
        return json(mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "tenant", slug, "email", email, "password", password))))
                .andExpect(status().isOk()))
                .get("accessToken").asText();
    }

    private String createCliente(String token, String income) throws Exception {
        return json(perform(post("/api/clientes"), token, clienteBody(TestDocuments.randomCpf(), income))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.createdAt").isNotEmpty()))
                .get("id").asText();
    }

    private static Map<String, String> clienteBody(String document, String income) {
        return Map.of(
                "name", "Cliente Teste",
                "document", document,
                "email", "cliente-" + UUID.randomUUID() + "@example.com",
                "monthlyIncome", income);
    }

    private ResultActions perform(
            org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder request,
            String token, Object body) throws Exception {
        request.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        if (body != null) {
            request.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(body));
        }
        return mvc.perform(request);
    }

    private JsonNode json(ResultActions result) throws Exception {
        return objectMapper.readTree(result.andReturn().getResponse().getContentAsString());
    }
}
