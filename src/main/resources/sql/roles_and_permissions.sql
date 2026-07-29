-- Roles
INSERT INTO roles (name)
VALUES ('CUSTOMER'),
       ('UNVERIFIED'),
       ('SELLER');

-- Permissions
INSERT INTO permissions (name)
VALUES ('READ_SALES_POST'),
       ('CREATE_SALES_POST'),
       ('UPDATE_PROFILE'),
       ('UPDATE_SALES_POST'),
       ('DELETE_SALES_POST'),
       ('REQUEST_VERIFICATION_EMAIL_RESEND');

-- Role <-> Permission mapping
-- CUSTOMER: READ_SALES_POST, UPDATE_PROFILE
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r,
     permissions p
WHERE r.name = 'CUSTOMER'
  AND p.name IN ('READ_SALES_POST', 'UPDATE_PROFILE');

-- UNVERIFIED: REQUEST_VERIFICATION_EMAIL_RESEND
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r,
     permissions p
WHERE r.name = 'UNVERIFIED'
  AND p.name = 'REQUEST_VERIFICATION_EMAIL_RESEND';

-- SELLER: all permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r,
     permissions p
WHERE r.name = 'SELLER'
  AND p.name IN (
                 'CREATE_SALES_POST',
                 'UPDATE_SALES_POST',
                 'DELETE_SALES_POST'
    );