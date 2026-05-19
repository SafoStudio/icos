CREATE TABLE companies (
                           id UUID PRIMARY KEY,
                           name VARCHAR(255) NOT NULL,
                           tax_id VARCHAR(50) UNIQUE,
                           policy_version INTEGER NOT NULL DEFAULT 1,
                           created_at TIMESTAMP NOT NULL,
                           updated_at TIMESTAMP NOT NULL
);

CREATE TABLE departments (
                             id UUID PRIMARY KEY,
                             company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
                             name VARCHAR(255) NOT NULL,
                             manager_id UUID,
                             created_at TIMESTAMP NOT NULL,
                             updated_at TIMESTAMP NOT NULL
);

CREATE TABLE roles (
                       id UUID PRIMARY KEY,
                       name VARCHAR(50) UNIQUE NOT NULL,
                       created_at TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP NOT NULL
);

CREATE TABLE permissions (
                             id UUID PRIMARY KEY,
                             name VARCHAR(100) UNIQUE NOT NULL,
                             description TEXT,
                             version INTEGER NOT NULL DEFAULT 1,
                             created_at TIMESTAMP NOT NULL,
                             updated_at TIMESTAMP NOT NULL
);

CREATE TABLE roles_permissions (
                                   role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
                                   permission_id UUID NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
                                   PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       first_name VARCHAR(255) NOT NULL,
                       last_name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                       created_by UUID,
                       created_at TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP NOT NULL
);

CREATE TABLE auth_credentials (
                                  user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
                                  password_hash VARCHAR(255) NOT NULL,
                                  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE user_profiles (
                               user_id UUID PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
                               phone_number VARCHAR(50),
                               address TEXT,
                               bio TEXT,
                               birth_date DATE,
                               created_at TIMESTAMP NOT NULL,
                               updated_at TIMESTAMP NOT NULL
);

CREATE TABLE memberships (
                             id UUID PRIMARY KEY,
                             user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                             company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
                             role_id UUID NOT NULL REFERENCES roles(id),
                             department_id UUID REFERENCES departments(id) ON DELETE SET NULL,
                             active BOOLEAN NOT NULL DEFAULT TRUE,
                             created_at TIMESTAMP NOT NULL,
                             updated_at TIMESTAMP NOT NULL,
                             UNIQUE(user_id, company_id)
);

CREATE TABLE invites (
                         id UUID PRIMARY KEY,
                         email VARCHAR(255) NOT NULL,
                         company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
                         token VARCHAR(255) UNIQUE NOT NULL,
                         role_id UUID NOT NULL REFERENCES roles(id),
                         department_id UUID REFERENCES departments(id) ON DELETE SET NULL,
                         expires_at TIMESTAMP NOT NULL,
                         accepted BOOLEAN NOT NULL DEFAULT FALSE,
                         created_at TIMESTAMP NOT NULL,
                         updated_at TIMESTAMP NOT NULL
);

CREATE TABLE refresh_tokens (
                                id UUID PRIMARY KEY,
                                token_hash VARCHAR(255) UNIQUE NOT NULL,
                                membership_id UUID NOT NULL REFERENCES memberships(id) ON DELETE CASCADE,
                                device_info VARCHAR(255),
                                expires_at TIMESTAMP NOT NULL,
                                revoked BOOLEAN NOT NULL DEFAULT FALSE,
                                created_at TIMESTAMP NOT NULL,
                                updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_memberships_user_id ON memberships(user_id);
CREATE INDEX idx_memberships_company_id ON memberships(company_id);
CREATE INDEX idx_memberships_active ON memberships(active);
CREATE INDEX idx_refresh_tokens_membership_id ON refresh_tokens(membership_id);
CREATE INDEX idx_refresh_tokens_token_hash ON refresh_tokens(token_hash);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_auth_credentials_updated_at BEFORE UPDATE ON auth_credentials FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_memberships_updated_at BEFORE UPDATE ON memberships FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_refresh_tokens_updated_at BEFORE UPDATE ON refresh_tokens FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();


INSERT INTO roles (id, name, created_at, updated_at) VALUES
                                                         (gen_random_uuid(), 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                         (gen_random_uuid(), 'OWNER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                         (gen_random_uuid(), 'HEAD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                         (gen_random_uuid(), 'SPECIALIST', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);