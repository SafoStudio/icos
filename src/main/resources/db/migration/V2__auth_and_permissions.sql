-- Permissions
INSERT INTO permissions (id, name, description, version, created_at, updated_at) VALUES
    (gen_random_uuid(), 'users:read', 'Read user data', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'users:write', 'Modify user data', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'email:change', 'Change user email', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'departments:read', 'Read department data', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'departments:write', 'Modify department data', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'roles:manage', 'Manage roles and permissions', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'company:manage', 'Manage company settings', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Get Role IDs
DO $$
DECLARE
    admin_id UUID;
    owner_id UUID;
    head_id UUID;
    spec_id UUID;
    p_users_read UUID;
    p_users_write UUID;
    p_email_change UUID;
    p_dept_read UUID;
    p_dept_write UUID;
    p_roles_manage UUID;
    p_company_manage UUID;
BEGIN
    SELECT id INTO admin_id FROM roles WHERE name = 'ADMIN';
    SELECT id INTO owner_id FROM roles WHERE name = 'OWNER';
    SELECT id INTO head_id FROM roles WHERE name = 'HEAD';
    SELECT id INTO spec_id FROM roles WHERE name = 'SPECIALIST';

    SELECT id INTO p_users_read FROM permissions WHERE name = 'users:read';
    SELECT id INTO p_users_write FROM permissions WHERE name = 'users:write';
    SELECT id INTO p_email_change FROM permissions WHERE name = 'email:change';
    SELECT id INTO p_dept_read FROM permissions WHERE name = 'departments:read';
    SELECT id INTO p_dept_write FROM permissions WHERE name = 'departments:write';
    SELECT id INTO p_roles_manage FROM permissions WHERE name = 'roles:manage';
    SELECT id INTO p_company_manage FROM permissions WHERE name = 'company:manage';

    -- ADMIN permissions (all)
    INSERT INTO roles_permissions (role_id, permission_id) VALUES
        (admin_id, p_users_read), (admin_id, p_users_write), (admin_id, p_email_change),
        (admin_id, p_dept_read), (admin_id, p_dept_write), (admin_id, p_roles_manage), (admin_id, p_company_manage);

    -- OWNER permissions (all)
    INSERT INTO roles_permissions (role_id, permission_id) VALUES
        (owner_id, p_users_read), (owner_id, p_users_write), (owner_id, p_email_change),
        (owner_id, p_dept_read), (owner_id, p_dept_write), (owner_id, p_roles_manage), (owner_id, p_company_manage);

    -- HEAD permissions
    INSERT INTO roles_permissions (role_id, permission_id) VALUES
        (head_id, p_users_read), (head_id, p_dept_read), (head_id, p_dept_write);

    -- SPECIALIST permissions
    INSERT INTO roles_permissions (role_id, permission_id) VALUES
        (spec_id, p_users_read);
END $$;
