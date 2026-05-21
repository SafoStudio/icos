-- Migration V2: Remove manager_id from departments, add manager_id to memberships

-- 1. Remove manager_id from departments
ALTER TABLE departments DROP COLUMN manager_id;

-- 2. Add manager_id to memberships
-- Хранит ссылку на непосредственного руководителя в рамках данной компании/отдела
ALTER TABLE memberships
    ADD COLUMN manager_id UUID REFERENCES users (id) ON DELETE SET NULL;

-- 3. Add index for manager_id in memberships
CREATE INDEX idx_memberships_manager_id ON memberships (manager_id);
