-- SEED DATA: PERMISSIONS

INSERT INTO permissions (id, name, description, version, created_at, updated_at)
VALUES
    -- 1. ORG MODULE
    (gen_random_uuid(), 'company:read', 'View company information', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'company:manage', 'Manage company settings', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'department:read', 'View departments', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'department:manage', 'Manage departments', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'position:read', 'View positions', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'position:manage', 'Manage positions', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- 2. RECRUITMENT MODULE
    (gen_random_uuid(), 'recruitment:read', 'View recruitment data', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'recruitment:manage', 'Manage recruitment', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'vacancy:read', 'View vacancies', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'vacancy:manage', 'Manage vacancies', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'candidate:read', 'View candidates', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'candidate:manage', 'Manage candidates', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'application:read', 'View applications', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'application:manage', 'Manage applications', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'interview:read', 'View interviews', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'interview:manage', 'Manage interviews', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'pipeline:read', 'View pipelines', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'pipeline:manage', 'Manage pipelines', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- 3. ONBOARDING MODULE
    (gen_random_uuid(), 'onboarding:read', 'View onboarding processes', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'onboarding:manage', 'Manage onboarding', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'onboarding:task.complete', 'Complete onboarding tasks', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'onboarding:template.read', 'View onboarding templates', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'onboarding:template.manage', 'Manage onboarding templates', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- 4. PROFILE MODULE
    (gen_random_uuid(), 'profile:edit.self', 'Edit own profile', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'profile:edit.all', 'Edit any user profile', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'profile:read', 'View user profiles', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'user:read', 'View users', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'user:manage', 'Manage users', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'role:read', 'View roles', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'role:manage', 'Manage roles', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- 5. MEMBERSHIP MODULE
    (gen_random_uuid(), 'membership:read', 'View memberships', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'membership:manage', 'Manage memberships', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'invite:read', 'View invites', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'invite:manage', 'Manage invites', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- 6. COMPENSATION MODULE
    (gen_random_uuid(), 'compensation:read', 'View compensation data', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'compensation:manage', 'Manage compensation', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'salary:read', 'View salary data', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'salary:manage', 'Manage salary', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- 7. TIME OFF MODULE
    (gen_random_uuid(), 'timeoff:read', 'View time off requests', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'timeoff:manage', 'Manage time off', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'timeoff:approve', 'Approve time off', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'timeoff:request', 'Request time off', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- 8. PERFORMANCE MODULE
    (gen_random_uuid(), 'performance:read', 'View performance data', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'performance:manage', 'Manage performance', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'performance:review', 'Review performance', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- 9. CALENDAR / EVENTS MODULE
    (gen_random_uuid(), 'calendar:read', 'View calendar', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'calendar:manage', 'Manage calendar events', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- 10. TASKS / KANBAN MODULE
    (gen_random_uuid(), 'task:read', 'View tasks', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'task:manage', 'Manage tasks', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'task:assign', 'Assign tasks', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'kanban:read', 'View kanban board', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'kanban:manage', 'Manage kanban board', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- 11. REPORTING MODULE
    (gen_random_uuid(), 'report:read', 'View reports', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'report:manage', 'Manage reports', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- 12. CUSTOM FIELDS MODULE
    (gen_random_uuid(), 'customfield:read', 'View custom fields', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'customfield:manage', 'Manage custom fields', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- 13. DOCUMENT / STORAGE MODULE
    (gen_random_uuid(), 'document:read', 'View documents', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'document:upload', 'Upload documents', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'document:manage', 'Manage documents', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- 14. NOTIFICATION MODULE
    (gen_random_uuid(), 'notification:read', 'View notifications', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'notification:manage', 'Manage notifications', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- 15. AUDIT MODULE
    (gen_random_uuid(), 'audit:read', 'View audit logs', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'audit:manage', 'Manage audit logs', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- 16. APPROVAL MODULE
    (gen_random_uuid(), 'approval:read', 'View approvals', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'approval:manage', 'Manage approvals', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'approval:approve', 'Approve requests', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ASSIGN ALL PERMISSIONS TO ADMIN & OWNER
DO $$
DECLARE
admin_id UUID;
    owner_id UUID;
BEGIN
SELECT id INTO admin_id FROM roles WHERE name = 'ADMIN';
SELECT id INTO owner_id FROM roles WHERE name = 'OWNER';

INSERT INTO roles_permissions (role_id, permission_id)
SELECT admin_id, id FROM permissions;

INSERT INTO roles_permissions (role_id, permission_id)
SELECT owner_id, id FROM permissions;
END $$;
