-- Positions Table
CREATE TABLE positions (
    id UUID PRIMARY KEY,
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Add position_id to memberships
ALTER TABLE memberships ADD COLUMN position_id UUID REFERENCES positions(id) ON DELETE SET NULL;

-- New Permissions
INSERT INTO permissions (id, name, description, version, created_at, updated_at) VALUES
    (gen_random_uuid(), 'org:manage', 'Full management of company structure', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'dept:manage', 'Management of departments', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'pos:manage', 'Management of positions', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'profile:edit.self', 'Ability to edit own profile', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'profile:edit.all', 'Ability to edit any user profile', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Assign permissions to roles
DO $$
DECLARE
    admin_id UUID;
    owner_id UUID;
    head_id UUID;
    spec_id UUID;
    p_org_manage UUID;
    p_dept_manage UUID;
    p_pos_manage UUID;
    p_profile_self UUID;
    p_profile_all UUID;
BEGIN
    SELECT id INTO admin_id FROM roles WHERE name = 'ADMIN';
    SELECT id INTO owner_id FROM roles WHERE name = 'OWNER';
    SELECT id INTO head_id FROM roles WHERE name = 'HEAD';
    SELECT id INTO spec_id FROM roles WHERE name = 'SPECIALIST';

    SELECT id INTO p_org_manage FROM permissions WHERE name = 'org:manage';
    SELECT id INTO p_dept_manage FROM permissions WHERE name = 'dept:manage';
    SELECT id INTO p_pos_manage FROM permissions WHERE name = 'pos:manage';
    SELECT id INTO p_profile_self FROM permissions WHERE name = 'profile:edit.self';
    SELECT id INTO p_profile_all FROM permissions WHERE name = 'profile:edit.all';

    -- ADMIN & OWNER get everything
    INSERT INTO roles_permissions (role_id, permission_id) VALUES
        (admin_id, p_org_manage), (admin_id, p_dept_manage), (admin_id, p_pos_manage), (admin_id, p_profile_all),
        (owner_id, p_org_manage), (owner_id, p_dept_manage), (owner_id, p_pos_manage), (owner_id, p_profile_all);

    -- HEAD can manage dept and positions
    INSERT INTO roles_permissions (role_id, permission_id) VALUES
        (head_id, p_dept_manage), (head_id, p_pos_manage), (head_id, p_profile_self);

    -- ALL roles can edit self
    INSERT INTO roles_permissions (role_id, permission_id) VALUES
        (spec_id, p_profile_self), (admin_id, p_profile_self), (owner_id, p_profile_self);
END $$;
