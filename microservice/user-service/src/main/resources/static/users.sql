CREATE TABLE users (
    id VARCHAR(40) PRIMARY KEY,
    username VARCHAR(100) UNIQUE,
    email VARCHAR(255) UNIQUE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone VARCHAR(20),
    avatar_url TEXT,
	profile_photo_url TEXT,
    status VARCHAR(20) NOT NULL,
    email_verified BOOLEAN DEFAULT FALSE,
    last_login_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ
);



CREATE TABLE roles (
    id VARCHAR(40) PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    created_at TIMESTAMPTZ
);


CREATE TABLE user_roles (
    user_id VARCHAR(40) NOT NULL,
    role_id VARCHAR(40) NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role
        FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);


CREATE TABLE refresh_tokens (
    id VARCHAR(40) PRIMARY KEY,
    token_id VARCHAR(255) UNIQUE NOT NULL,
    user_id VARCHAR(40) NOT NULL,
    token_hash TEXT NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ,
    device_id VARCHAR(100),

    CONSTRAINT fk_refresh_token_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_refresh_tokens_token_id ON refresh_tokens(token_id);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);


CREATE TABLE user_addresses (
    id VARCHAR(40) PRIMARY KEY,
    user_id VARCHAR(40) NOT NULL,
    name VARCHAR(100),
    phone VARCHAR(20),
    address_line TEXT NOT NULL,
    city VARCHAR(100),
    state VARCHAR(100),
    pincode VARCHAR(20),
    address_type VARCHAR(20) NOT NULL,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ,

    CONSTRAINT fk_user_address_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);



CREATE TABLE user_auth_providers (
    id VARCHAR(40) PRIMARY KEY,
    user_id VARCHAR(40) NOT NULL,
    provider VARCHAR(50) NOT NULL,
    provider_user_id VARCHAR(255) NOT NULL,
    linked_at TIMESTAMPTZ,

    CONSTRAINT fk_user_auth_provider_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    CONSTRAINT uq_provider_provider_user
        UNIQUE (provider, provider_user_id)
);


CREATE TABLE user_credentials (
    user_id VARCHAR(40) PRIMARY KEY,
    password_hash TEXT NOT NULL,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ,

    CONSTRAINT fk_user_credentials_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);


CREATE TABLE password_reset_tokens (
    id VARCHAR(40) PRIMARY KEY,
    token_id VARCHAR(255),
    user_id VARCHAR(40) NOT NULL,
    token_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    used_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_password_reset_user ON password_reset_tokens (user_id);
CREATE INDEX idx_password_reset_expiry ON password_reset_tokens (expires_at);