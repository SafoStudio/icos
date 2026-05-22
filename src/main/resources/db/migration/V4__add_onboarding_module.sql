-- V4: Onboarding Module Schema

-- Onboarding Templates
CREATE TABLE onboarding_templates (
    id UUID PRIMARY KEY,
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Task Templates (for templates)
CREATE TABLE onboarding_task_templates (
    id UUID PRIMARY KEY,
    template_id UUID NOT NULL REFERENCES onboarding_templates(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    days_from_start INTEGER NOT NULL DEFAULT 0, -- Recommended due date relative to start
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Onboarding Processes (Specific to a Member)
CREATE TABLE onboarding_processes (
    id UUID PRIMARY KEY,
    membership_id UUID NOT NULL REFERENCES memberships(id) ON DELETE CASCADE,
    template_id UUID REFERENCES onboarding_templates(id) ON DELETE SET NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'NOT_STARTED', -- NOT_STARTED, IN_PROGRESS, COMPLETED, STALLED
    start_date DATE NOT NULL,
    target_completion_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Concrete Onboarding Tasks
CREATE TABLE onboarding_tasks (
    id UUID PRIMARY KEY,
    process_id UUID NOT NULL REFERENCES onboarding_processes(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    assignee_id UUID NOT NULL REFERENCES users(id), -- Who completes the task
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING', -- PENDING, DONE, WAITING
    due_date DATE,
    completed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Triggers for updated_at
CREATE TRIGGER update_onboarding_templates_updated_at BEFORE UPDATE ON onboarding_templates FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_onboarding_task_templates_updated_at BEFORE UPDATE ON onboarding_task_templates FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_onboarding_processes_updated_at BEFORE UPDATE ON onboarding_processes FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_onboarding_tasks_updated_at BEFORE UPDATE ON onboarding_tasks FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Permissions
INSERT INTO permissions (id, name, description, version, created_at, updated_at)
VALUES (gen_random_uuid(), 'onboarding:read', 'Read onboarding processes', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (gen_random_uuid(), 'onboarding:manage', 'Manage onboarding templates and processes', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (gen_random_uuid(), 'onboarding:task.complete', 'Complete assigned onboarding tasks', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

DO $$
DECLARE
    admin_id UUID;
    owner_id UUID;
    head_id UUID;
    spec_id UUID;
    p_onb_read UUID;
    p_onb_manage UUID;
    p_onb_task UUID;
BEGIN
    SELECT id INTO admin_id FROM roles WHERE name = 'ADMIN';
    SELECT id INTO owner_id FROM roles WHERE name = 'OWNER';
    SELECT id INTO head_id FROM roles WHERE name = 'HEAD';
    SELECT id INTO spec_id FROM roles WHERE name = 'SPECIALIST';

    SELECT id INTO p_onb_read FROM permissions WHERE name = 'onboarding:read';
    SELECT id INTO p_onb_manage FROM permissions WHERE name = 'onboarding:manage';
    SELECT id INTO p_onb_task FROM permissions WHERE name = 'onboarding:task.complete';

    -- ADMIN & OWNER get everything
    INSERT INTO roles_permissions (role_id, permission_id) VALUES 
    (admin_id, p_onb_read), (admin_id, p_onb_manage), (admin_id, p_onb_task),
    (owner_id, p_onb_read), (owner_id, p_onb_manage), (owner_id, p_onb_task);

    -- HEAD
    INSERT INTO roles_permissions (role_id, permission_id) VALUES 
    (head_id, p_onb_read), (head_id, p_onb_task);

    -- SPECIALIST
    INSERT INTO roles_permissions (role_id, permission_id) VALUES 
    (spec_id, p_onb_read), (spec_id, p_onb_task);
END $$;
