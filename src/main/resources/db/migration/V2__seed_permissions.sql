-- SEED DATA: PERMISSIONS

INSERT INTO permissions (id, name, description, version, created_at, updated_at)
VALUES
    -- 1. ORG MODULE
    (gen_random_uuid(), 'company:read', 'View company details and settings', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'company:manage', 'Edit company name, tax ID and policy settings', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'department:read', 'View department list and details', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'department:manage', 'Create, edit and delete departments', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'position:read', 'View position list and details', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'position:manage', 'Create, edit and delete positions', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- 2. RECRUITMENT MODULE
    (gen_random_uuid(), 'recruitment:read', 'View recruitment dashboard and data', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'recruitment:manage', 'Manage recruitment processes and settings', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'vacancy:read', 'View job vacancies', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'vacancy:manage', 'Create, edit and close job vacancies', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'candidate:read', 'View candidate profiles and resumes', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'candidate:manage', 'Add, edit and delete candidates', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'application:read', 'View job applications', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'application:manage', 'Review, accept and reject applications', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'interview:read', 'View interview schedules and feedback', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'interview:manage', 'Schedule, reschedule and conduct interviews', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'pipeline:read', 'View recruitment pipelines', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'pipeline:manage', 'Create and manage recruitment pipelines', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- 3. ONBOARDING MODULE
    (gen_random_uuid(), 'onboarding:read', 'View onboarding processes and progress', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'onboarding:manage', 'Create and manage onboarding processes', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'onboarding:task.complete', 'Complete assigned onboarding tasks', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'onboarding:template.read', 'View onboarding templates', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'onboarding:template.manage', 'Create and manage onboarding templates', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- 4. PROFILE MODULE
    (gen_random_uuid(), 'profile:edit.self', 'Edit your own profile information', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'profile:edit.all', 'Edit any user profile in the company', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'profile:read', 'View user profiles and contact details', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'user:read', 'View user list and basic information', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'user:manage', 'Activate, suspend or deactivate users', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'role:read', 'View role definitions and permissions', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'role:manage', 'Create, edit and delete roles', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- 5. MEMBERSHIP MODULE
    (gen_random_uuid(), 'membership:read', 'View company memberships', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'membership:manage', 'Assign, edit and remove memberships', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'invite:read', 'View sent and received invitations', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'invite:manage', 'Send, resend and cancel invitations', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- 6. COMPENSATION MODULE
    (gen_random_uuid(), 'compensation:read', 'View compensation plans and structures', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'compensation:manage', 'Create and manage compensation plans', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'salary:read', 'View salary information', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'salary:manage', 'Manage salaries and salary revisions', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- 7. TIME OFF MODULE
    (gen_random_uuid(), 'timeoff:read', 'View time off requests and balances', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'timeoff:manage', 'Manage time off policies and settings', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'timeoff:approve', 'Approve or reject time off requests', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'timeoff:request', 'Request time off', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- 8. PERFORMANCE MODULE
    (gen_random_uuid(), 'performance:read', 'View performance reviews and metrics', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'performance:manage', 'Create and manage performance reviews', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'performance:review', 'Submit and complete performance reviews', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- 9. CALENDAR / EVENTS MODULE
    (gen_random_uuid(), 'calendar:read', 'View calendar and events', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'calendar:manage', 'Create, edit and delete events', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- 10. TASKS / KANBAN MODULE
    (gen_random_uuid(), 'task:read', 'View tasks and task details', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'task:manage', 'Create, edit and delete tasks', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'task:assign', 'Assign tasks to users', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'kanban:read', 'View kanban board', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'kanban:manage', 'Manage kanban board and columns', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- 11. REPORTING MODULE
    (gen_random_uuid(), 'report:read', 'View reports and analytics', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'report:manage', 'Create, schedule and manage reports', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- 12. CUSTOM FIELDS MODULE
    (gen_random_uuid(), 'customfield:read', 'View custom fields', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'customfield:manage', 'Create and manage custom fields', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- 13. DOCUMENT / STORAGE MODULE
    (gen_random_uuid(), 'document:read', 'View documents and files', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'document:upload', 'Upload new documents and files', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'document:manage', 'Manage documents, folders and storage', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- 14. NOTIFICATION MODULE
    (gen_random_uuid(), 'notification:read', 'View notifications', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'notification:manage', 'Manage notification settings', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- 15. AUDIT MODULE
    (gen_random_uuid(), 'audit:read', 'View audit logs and activity history', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'audit:manage', 'Manage audit settings and logs', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    -- 16. APPROVAL MODULE
    (gen_random_uuid(), 'approval:read', 'View approval requests', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'approval:manage', 'Manage approval workflows', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'approval:approve', 'Approve or reject requests', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    (gen_random_uuid(), 'offboarding:read', 'View offboarding processes, tasks, and templates', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'offboarding:manage', 'Initiate, approve, transition, and manage offboarding processes', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'offboarding:task.read', 'View assigned offboarding tasks', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (gen_random_uuid(), 'offboarding:task.complete', 'Update, complete, or skip offboarding tasks', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

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
