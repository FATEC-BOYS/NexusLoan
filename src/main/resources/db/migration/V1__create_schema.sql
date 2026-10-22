CREATE TABLE tenant (
    id          UUID         PRIMARY KEY,
    name        VARCHAR(120) NOT NULL,
    slug        VARCHAR(60)  NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL,
    CONSTRAINT uk_tenant_slug UNIQUE (slug)
);

CREATE TABLE app_user (
    id             UUID         PRIMARY KEY,
    tenant_id      UUID         NOT NULL REFERENCES tenant (id),
    name           VARCHAR(120) NOT NULL,
    email          VARCHAR(160) NOT NULL,
    password_hash  VARCHAR(100) NOT NULL,
    role           VARCHAR(20)  NOT NULL CHECK (role IN ('ADMIN', 'USER')),
    created_at     TIMESTAMPTZ  NOT NULL,
    updated_at     TIMESTAMPTZ  NOT NULL,
    CONSTRAINT uk_app_user_tenant_email UNIQUE (tenant_id, email)
);

CREATE TABLE cliente (
    id              UUID           PRIMARY KEY,
    tenant_id       UUID           NOT NULL REFERENCES tenant (id),
    name            VARCHAR(120)   NOT NULL,
    document        VARCHAR(14)    NOT NULL,
    email           VARCHAR(160)   NOT NULL,
    monthly_income  NUMERIC(15, 2) NOT NULL CHECK (monthly_income >= 0),
    created_at      TIMESTAMPTZ    NOT NULL,
    updated_at      TIMESTAMPTZ    NOT NULL,
    CONSTRAINT uk_cliente_tenant_document UNIQUE (tenant_id, document),
    -- Alvo da FK composta de emprestimo: garante no banco que o cliente pertence ao mesmo tenant.
    CONSTRAINT uk_cliente_tenant_id UNIQUE (tenant_id, id)
);

CREATE TABLE emprestimo (
    id          UUID           PRIMARY KEY,
    tenant_id   UUID           NOT NULL REFERENCES tenant (id),
    cliente_id  UUID           NOT NULL,
    amount      NUMERIC(15, 2) NOT NULL CHECK (amount > 0),
    status      VARCHAR(20)    NOT NULL CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    reason      VARCHAR(255),
    created_at  TIMESTAMPTZ    NOT NULL,
    updated_at  TIMESTAMPTZ    NOT NULL,
    CONSTRAINT fk_emprestimo_cliente_same_tenant
        FOREIGN KEY (tenant_id, cliente_id) REFERENCES cliente (tenant_id, id)
);

CREATE INDEX idx_emprestimo_tenant_cliente ON emprestimo (tenant_id, cliente_id);
CREATE INDEX idx_emprestimo_tenant_created ON emprestimo (tenant_id, created_at DESC);
CREATE INDEX idx_cliente_tenant_created ON cliente (tenant_id, created_at DESC);
