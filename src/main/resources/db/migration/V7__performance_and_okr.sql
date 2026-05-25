-- Performance & OKR Schema

CREATE TABLE IF NOT EXISTS okr_objectives (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    progress DECIMAL(5,2) DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, ACHIEVED, FAILED, CANCELLED
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS okr_key_results (
    id UUID PRIMARY KEY,
    objective_id UUID NOT NULL REFERENCES okr_objectives(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    target_value DECIMAL(19,4) NOT NULL,
    current_value DECIMAL(19,4) NOT NULL DEFAULT 0,
    unit VARCHAR(20),
    weight DECIMAL(5,2) DEFAULT 1.0, -- Relative weight of this KR in the objective
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS performance_review_cycles (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL, -- e.g. 'Annual Review 2025'
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, CLOSED
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS performance_reviews (
    id UUID PRIMARY KEY,
    cycle_id UUID NOT NULL REFERENCES performance_review_cycles(id) ON DELETE CASCADE,
    employee_id UUID NOT NULL,
    manager_id UUID NOT NULL,
    rating INTEGER, -- 1-5 scale
    summary TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING', -- PENDING, FINALIZED
    finalized_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(cycle_id, employee_id)
);

-- Indexes
CREATE INDEX idx_okr_user ON okr_objectives(user_id);
CREATE INDEX idx_okr_objective_kr ON okr_key_results(objective_id);
CREATE INDEX idx_performance_employee ON performance_reviews(employee_id);
CREATE INDEX idx_performance_cycle ON performance_reviews(cycle_id);
