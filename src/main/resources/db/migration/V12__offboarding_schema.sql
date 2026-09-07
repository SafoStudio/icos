-- Offboarding Module Schema

-- 1. Offboarding Templates
CREATE TABLE IF NOT EXISTS offboarding_templates (
    id UUID PRIMARY KEY,
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. Offboarding Task Templates
CREATE TABLE IF NOT EXISTS offboarding_task_templates (
    id UUID PRIMARY KEY,
    template_id UUID NOT NULL REFERENCES offboarding_templates(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    days_from_start INTEGER NOT NULL DEFAULT 0,
    assignee_role VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. Offboarding Processes
CREATE TABLE IF NOT EXISTS offboarding_processes (
    id UUID PRIMARY KEY,
    membership_id UUID NOT NULL REFERENCES memberships(id) ON DELETE CASCADE,
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    template_id UUID REFERENCES offboarding_templates(id) ON DELETE SET NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING_APPROVAL',
    start_date DATE NOT NULL,
    last_working_day DATE NOT NULL,
    reason TEXT,
    initiated_by VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 4. Offboarding Tasks
CREATE TABLE IF NOT EXISTS offboarding_tasks (
    id UUID PRIMARY KEY,
    process_id UUID NOT NULL REFERENCES offboarding_processes(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    assignee_id UUID NOT NULL REFERENCES users(id),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    due_date DATE,
    completed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

