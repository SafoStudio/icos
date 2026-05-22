-- V3: Recruitment Module Schema

-- Pipeline Templates
CREATE TABLE pipeline_templates (
    id UUID PRIMARY KEY,
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Pipeline Stages (Templates)
CREATE TABLE pipeline_stage_templates (
    id UUID PRIMARY KEY,
    template_id UUID NOT NULL REFERENCES pipeline_templates(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL, -- SCREENING, TECHNICAL, HR, OFFER, etc.
    order_index INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Vacancies
CREATE TABLE vacancies (
    id UUID PRIMARY KEY,
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    department_id UUID REFERENCES departments(id) ON DELETE SET NULL,
    position_id UUID REFERENCES positions(id) ON DELETE SET NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    requirements TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT', -- DRAFT, OPEN, CLOSED, PAUSED
    created_by UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Vacancy Pipeline (Snapshot of template for a specific vacancy)
CREATE TABLE vacancy_stages (
    id UUID PRIMARY KEY,
    vacancy_id UUID NOT NULL REFERENCES vacancies(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    order_index INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Vacancy Assignments (Hiring Team)
CREATE TABLE vacancy_assignments (
    vacancy_id UUID NOT NULL REFERENCES vacancies(id) ON DELETE CASCADE,
    employee_id UUID NOT NULL REFERENCES memberships(id) ON DELETE CASCADE, -- Reference to membership
    assignment_type VARCHAR(50) NOT NULL, -- HIRING_MANAGER, RECRUITER, COORDINATOR
    PRIMARY KEY (vacancy_id, employee_id, assignment_type)
);

-- Candidates (External persons)
CREATE TABLE candidates (
    id UUID PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    source VARCHAR(255),
    resume_url TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Applications (Link between Candidate and Vacancy)
CREATE TABLE applications (
    id UUID PRIMARY KEY,
    candidate_id UUID NOT NULL REFERENCES candidates(id) ON DELETE CASCADE,
    vacancy_id UUID NOT NULL REFERENCES vacancies(id) ON DELETE CASCADE,
    current_stage_id UUID REFERENCES vacancy_stages(id) ON DELETE SET NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, HIRED, REJECTED, WITHDRAWN
    rejection_reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Interviews
CREATE TABLE interviews (
    id UUID PRIMARY KEY,
    application_id UUID NOT NULL REFERENCES applications(id) ON DELETE CASCADE,
    stage_id UUID NOT NULL REFERENCES vacancy_stages(id),
    title VARCHAR(255) NOT NULL,
    scheduled_at TIMESTAMP WITH TIME ZONE NOT NULL,
    duration_minutes INTEGER NOT NULL DEFAULT 60,
    location VARCHAR(255),
    meeting_link TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'SCHEDULED', -- SCHEDULED, COMPLETED, CANCELLED
    feedback TEXT,
    rating INTEGER, -- 1-5
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Interview Assignments (Interviewers)
CREATE TABLE interview_assignments (
    interview_id UUID NOT NULL REFERENCES interviews(id) ON DELETE CASCADE,
    employee_id UUID NOT NULL REFERENCES memberships(id) ON DELETE CASCADE,
    PRIMARY KEY (interview_id, employee_id)
);

-- Triggers for updated_at
CREATE TRIGGER update_pipeline_templates_updated_at BEFORE UPDATE ON pipeline_templates FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_pipeline_stage_templates_updated_at BEFORE UPDATE ON pipeline_stage_templates FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_vacancies_updated_at BEFORE UPDATE ON vacancies FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_vacancy_stages_updated_at BEFORE UPDATE ON vacancy_stages FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_candidates_updated_at BEFORE UPDATE ON candidates FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_applications_updated_at BEFORE UPDATE ON applications FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_interviews_updated_at BEFORE UPDATE ON interviews FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Permissions
INSERT INTO permissions (id, name, description, version, created_at, updated_at)
VALUES (gen_random_uuid(), 'recruitment:read', 'Read recruitment data', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (gen_random_uuid(), 'recruitment:manage', 'Manage vacancies and pipelines', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (gen_random_uuid(), 'application:manage', 'Manage candidate applications', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       (gen_random_uuid(), 'interview:manage', 'Manage interviews', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

DO $$
DECLARE
    admin_id UUID;
    owner_id UUID;
    head_id UUID;
    spec_id UUID;
    p_rec_read UUID;
    p_rec_manage UUID;
    p_app_manage UUID;
    p_int_manage UUID;
BEGIN
    SELECT id INTO admin_id FROM roles WHERE name = 'ADMIN';
    SELECT id INTO owner_id FROM roles WHERE name = 'OWNER';
    SELECT id INTO head_id FROM roles WHERE name = 'HEAD';
    SELECT id INTO spec_id FROM roles WHERE name = 'SPECIALIST';

    SELECT id INTO p_rec_read FROM permissions WHERE name = 'recruitment:read';
    SELECT id INTO p_rec_manage FROM permissions WHERE name = 'recruitment:manage';
    SELECT id INTO p_app_manage FROM permissions WHERE name = 'application:manage';
    SELECT id INTO p_int_manage FROM permissions WHERE name = 'interview:manage';

    -- ADMIN & OWNER get everything
    INSERT INTO roles_permissions (role_id, permission_id) VALUES 
    (admin_id, p_rec_read), (admin_id, p_rec_manage), (admin_id, p_app_manage), (admin_id, p_int_manage),
    (owner_id, p_rec_read), (owner_id, p_rec_manage), (owner_id, p_app_manage), (owner_id, p_int_manage);

    -- HEAD
    INSERT INTO roles_permissions (role_id, permission_id) VALUES 
    (head_id, p_rec_read), (head_id, p_app_manage), (head_id, p_int_manage);

    -- SPECIALIST
    INSERT INTO roles_permissions (role_id, permission_id) VALUES 
    (spec_id, p_rec_read);
END $$;
