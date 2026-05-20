-- 1. Company
DELETE FROM permissions WHERE name = 'org:manage' AND EXISTS (SELECT 1 FROM permissions WHERE name = 'company:manage');
UPDATE permissions SET name = 'company:manage' WHERE name = 'org:manage';

-- 2. User
DELETE FROM permissions WHERE name = 'users:read' AND EXISTS (SELECT 1 FROM permissions WHERE name = 'user:read');
UPDATE permissions SET name = 'user:read' WHERE name = 'users:read';

DELETE FROM permissions WHERE name = 'users:write' AND EXISTS (SELECT 1 FROM permissions WHERE name = 'user:write');
UPDATE permissions SET name = 'user:write' WHERE name = 'users:write';

-- 3. Department
DELETE FROM permissions WHERE name = 'dept:manage' AND EXISTS (SELECT 1 FROM permissions WHERE name = 'department:manage');
UPDATE permissions SET name = 'department:manage' WHERE name = 'dept:manage';

DELETE FROM permissions WHERE name = 'departments:read' AND EXISTS (SELECT 1 FROM permissions WHERE name = 'department:read');
UPDATE permissions SET name = 'department:read' WHERE name = 'departments:read';

DELETE FROM permissions WHERE name = 'departments:write' AND EXISTS (SELECT 1 FROM permissions WHERE name = 'department:write');
UPDATE permissions SET name = 'department:write' WHERE name = 'departments:write';

-- 4. Position
DELETE FROM permissions WHERE name = 'pos:manage' AND EXISTS (SELECT 1 FROM permissions WHERE name = 'position:manage');
UPDATE permissions SET name = 'position:manage' WHERE name = 'pos:manage';

-- 5. Role
DELETE FROM permissions WHERE name = 'roles:manage' AND EXISTS (SELECT 1 FROM permissions WHERE name = 'role:manage');
UPDATE permissions SET name = 'role:manage' WHERE name = 'roles:manage';