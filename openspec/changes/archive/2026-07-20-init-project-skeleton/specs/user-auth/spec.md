## ADDED Requirements

### Requirement: User Login Authentication
The system SHALL authenticate users via username and password credentials and return a JWT token upon successful authentication.

#### Scenario: Successful login with valid credentials
- **WHEN** a user submits valid username and password
- **THEN** the system returns a JWT token with 24-hour expiry and user profile information

#### Scenario: Failed login with invalid credentials
- **WHEN** a user submits invalid username or password
- **THEN** the system returns error code 401 with message "用户名或密码错误"

#### Scenario: Account locked after repeated failures
- **WHEN** a user fails login 5 times within 1 minute
- **THEN** the account is locked for 15 minutes and returns error code 423

### Requirement: JWT Token Validation
The system SHALL validate JWT tokens on all protected API endpoints and reject requests with invalid or expired tokens.

#### Scenario: Request with valid token
- **WHEN** a request includes a valid JWT token in Authorization header
- **THEN** the request is processed normally

#### Scenario: Request with expired token
- **WHEN** a request includes an expired JWT token
- **THEN** the system returns error code 401 with message "Token已过期，请重新登录"

#### Scenario: Request without token
- **WHEN** a request to a protected endpoint has no Authorization header
- **THEN** the system returns error code 401 with message "请先登录"

### Requirement: User Management CRUD
The system SHALL support creating, reading, updating, and deactivating user accounts with role assignments.

#### Scenario: Create new user
- **WHEN** an admin creates a new user with username, name, phone, and role
- **THEN** the user is created with BCrypt-encrypted password and default status "active"

#### Scenario: Update user information
- **WHEN** an admin updates user profile information
- **THEN** the changes are persisted and audit fields are updated

#### Scenario: Deactivate user account
- **WHEN** an admin deactivates a user account
- **THEN** the user can no longer login but historical data is preserved

### Requirement: Role-Based Access Control
The system SHALL enforce role-based access control with four predefined roles: admin, team_leader, operator, inspector.

#### Scenario: Admin accesses user management
- **WHEN** a user with admin role accesses /api/v1/users
- **THEN** the request is allowed

#### Scenario: Operator accesses user management
- **WHEN** a user with operator role accesses /api/v1/users
- **THEN** the system returns error code 403 with message "无权限访问"

#### Scenario: Role-menu association
- **WHEN** an admin assigns menus to a role
- **THEN** users with that role can only access assigned menu items

### Requirement: Password Security
The system SHALL store passwords using BCrypt encryption and enforce password complexity rules.

#### Scenario: Password storage
- **WHEN** a user sets or changes password
- **THEN** the password is stored as BCrypt hash, never in plaintext

#### Scenario: Password change
- **WHEN** a user changes password with correct old password
- **THEN** the new password is encrypted and stored, old token is invalidated
