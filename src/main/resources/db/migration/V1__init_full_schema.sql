-- V1: Initial Tables and Functions
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
    description TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE positions (
    id UUID PRIMARY KEY,
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
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
    position_id UUID REFERENCES positions(id) ON DELETE SET NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    UNIQUE(user_id, company_id)
);

-- Invites table
CREATE TABLE invites (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    company_id UUID NOT NULL,
    department_id UUID NOT NULL,
    manager_id UUID,
    invited_by_id UUID NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
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

-- Indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_memberships_user_id ON memberships(user_id);
CREATE INDEX idx_memberships_company_id ON memberships(company_id);
CREATE INDEX idx_memberships_active ON memberships(active);
CREATE INDEX idx_refresh_tokens_membership_id ON refresh_tokens(membership_id);
CREATE INDEX idx_refresh_tokens_token_hash ON refresh_tokens(token_hash);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);
CREATE INDEX idx_invites_token ON invites(token);
CREATE INDEX idx_invites_email ON invites(email);
CREATE INDEX idx_invites_status ON invites(status);
CREATE INDEX idx_invites_company ON invites(company_id);

-- Updated at Trigger
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

-- Data Seeding
INSERT INTO roles (id, name, created_at, updated_at) VALUES
    (gen_random_uuid(), 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'OWNER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'HEAD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'SPECIALIST', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO permissions (id, name, description, version, created_at, updated_at) VALUES
    (gen_random_uuid(), 'user:read', 'Read user data', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'user:write', 'Modify user data', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'email:change', 'Change user email', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'department:read', 'Read department data', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'department:write', 'Modify department data', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'role:manage', 'Manage roles and permissions', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'company:manage', 'Manage company settings', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'department:manage', 'Management of departments', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'position:manage', 'Management of positions', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'profile:edit.self', 'Ability to edit own profile', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'profile:edit.all', 'Ability to edit any user profile', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'invite:manage', 'Can create, cancel and resend invitations', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

DO $$
DECLARE
    admin_id UUID;
    owner_id UUID;
    head_id UUID;
    spec_id UUID;
    p_user_read UUID;
    p_user_write UUID;
    p_email_change UUID;
    p_dept_read UUID;
    p_dept_write UUID;
    p_role_manage UUID;
    p_company_manage UUID;
    p_dept_manage UUID;
    p_pos_manage UUID;
    p_profile_self UUID;
    p_profile_all UUID;
    p_invite_manage UUID;
BEGIN
    SELECT id INTO admin_id FROM roles WHERE name = 'ADMIN';
    SELECT id INTO owner_id FROM roles WHERE name = 'OWNER';
    SELECT id INTO head_id FROM roles WHERE name = 'HEAD';
    SELECT id INTO spec_id FROM roles WHERE name = 'SPECIALIST';

    SELECT id INTO p_user_read FROM permissions WHERE name = 'user:read';
    SELECT id INTO p_user_write FROM permissions WHERE name = 'user:write';
    SELECT id INTO p_email_change FROM permissions WHERE name = 'email:change';
    SELECT id INTO p_dept_read FROM permissions WHERE name = 'department:read';
    SELECT id INTO p_dept_write FROM permissions WHERE name = 'department:write';
    SELECT id INTO p_role_manage FROM permissions WHERE name = 'role:manage';
    SELECT id INTO p_company_manage FROM permissions WHERE name = 'company:manage';
    SELECT id INTO p_dept_manage FROM permissions WHERE name = 'department:manage';
    SELECT id INTO p_pos_manage FROM permissions WHERE name = 'position:manage';
    SELECT id INTO p_profile_self FROM permissions WHERE name = 'profile:edit.self';
    SELECT id INTO p_profile_all FROM permissions WHERE name = 'profile:edit.all';
    SELECT id INTO p_invite_manage FROM permissions WHERE name = 'invite:manage';

    -- ADMIN & OWNER get everything
    INSERT INTO roles_permissions (role_id, permission_id) VALUES
        (admin_id, p_user_read), (admin_id, p_user_write), (admin_id, p_email_change),
        (admin_id, p_dept_read), (admin_id, p_dept_write), (admin_id, p_role_manage), (admin_id, p_company_manage),
        (admin_id, p_dept_manage), (admin_id, p_pos_manage), (admin_id, p_profile_all), (admin_id, p_invite_manage),
        (owner_id, p_user_read), (owner_id, p_user_write), (owner_id, p_email_change),
        (owner_id, p_dept_read), (owner_id, p_dept_write), (owner_id, p_role_manage), (owner_id, p_company_manage),
        (owner_id, p_dept_manage), (owner_id, p_pos_manage), (owner_id, p_profile_all), (owner_id, p_invite_manage);

    -- HEAD
    INSERT INTO roles_permissions (role_id, permission_id) VALUES
        (head_id, p_user_read), (head_id, p_dept_read), (head_id, p_dept_write),
        (head_id, p_dept_manage), (head_id, p_pos_manage), (head_id, p_profile_self), (head_id, p_invite_manage);

    -- ALL roles can edit self
    INSERT INTO roles_permissions (role_id, permission_id) VALUES
        (spec_id, p_user_read), (spec_id, p_profile_self), (spec_id, p_invite_manage);
END $$;
