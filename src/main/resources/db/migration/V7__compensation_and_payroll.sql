-- Compensation & Payroll Management Schema

CREATE TABLE IF NOT EXISTS compensation_records (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    base_salary DECIMAL(19,4) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'USD',
    pay_frequency VARCHAR(50) NOT NULL DEFAULT 'MONTHLY',
    effective_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS compensation_revisions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    previous_salary DECIMAL(19,4),
    new_salary DECIMAL(19,4) NOT NULL,
    reason VARCHAR(255),
    effective_date DATE NOT NULL,
    approved_by UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS bonuses (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    type VARCHAR(50) NOT NULL, -- PERFORMANCE, REFERRAL, RETENTION, etc.
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING', -- PENDING, APPROVED, PAID, CANCELLED
    payment_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS payroll_periods (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL, -- e.g. 'May 2026'
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT', -- DRAFT, OPEN, CLOSED, PAID
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS payroll_entries (
    id UUID PRIMARY KEY,
    payroll_period_id UUID NOT NULL REFERENCES payroll_periods(id) ON DELETE CASCADE,
    user_id UUID NOT NULL,
    base_amount DECIMAL(19,4) NOT NULL,
    bonus_amount DECIMAL(19,4) DEFAULT 0,
    deduction_amount DECIMAL(19,4) DEFAULT 0,
    total_amount DECIMAL(19,4) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    UNIQUE(payroll_period_id, user_id)
);

-- Indexes
CREATE INDEX idx_compensation_user ON compensation_records(user_id);
CREATE INDEX idx_revisions_user ON compensation_revisions(user_id);
CREATE INDEX idx_bonuses_user ON bonuses(user_id);
CREATE INDEX idx_payroll_entries_period ON payroll_entries(payroll_period_id);
