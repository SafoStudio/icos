-- Leave Management Schema

CREATE TABLE IF NOT EXISTS leave_types (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    requires_balance BOOLEAN NOT NULL DEFAULT TRUE,
    color VARCHAR(20),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS leave_balances (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    leave_type_id UUID NOT NULL REFERENCES leave_types(id),
    total_days DECIMAL(5,2) NOT NULL DEFAULT 0,
    used_days DECIMAL(5,2) NOT NULL DEFAULT 0,
    remaining_days DECIMAL(5,2) NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, leave_type_id)
);

CREATE TABLE IF NOT EXISTS leave_requests (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    leave_type_id UUID NOT NULL REFERENCES leave_types(id),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    total_days DECIMAL(5,2) NOT NULL,
    reason TEXT,
    status VARCHAR(50) NOT NULL,
    approved_at TIMESTAMP,
    rejected_at TIMESTAMP,
    cancelled_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS leave_audit_logs (
    id UUID PRIMARY KEY,
    leave_request_id UUID NOT NULL REFERENCES leave_requests(id) ON DELETE CASCADE,
    actor_id UUID NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    metadata TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Initial leave types
INSERT INTO leave_types (id, name, code, requires_balance, color) VALUES 
(gen_random_uuid(), 'Annual Leave', 'ANNUAL', true, '#4CAF50'),
(gen_random_uuid(), 'Sick Leave', 'SICK', false, '#F44336'),
(gen_random_uuid(), 'Unpaid Leave', 'UNPAID', false, '#9E9E9E'),
(gen_random_uuid(), 'Remote Day', 'REMOTE', false, '#2196F3')
ON CONFLICT DO NOTHING;

-- Indexes
CREATE INDEX idx_leave_requests_user ON leave_requests(user_id);
CREATE INDEX idx_leave_requests_status ON leave_requests(status);
CREATE INDEX idx_leave_balances_user ON leave_balances(user_id);
