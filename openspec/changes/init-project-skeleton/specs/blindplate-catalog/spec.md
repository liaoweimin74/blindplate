## ADDED Requirements

### Requirement: Blind Plate Catalog CRUD
The system SHALL support creating, reading, updating, and deleting blind plate records with full specification details.

#### Scenario: Create new blind plate record
- **WHEN** a user submits blind plate data including code, name, spec, material, diameter, pressure, and manufacturer
- **THEN** the record is created with auto-generated ID and timestamps

#### Scenario: Query blind plates with filters
- **WHEN** a user queries blind plates with optional filters (keyword, status, material, diameter range)
- **THEN** the system returns paginated results sorted by creation time descending

#### Scenario: Update blind plate information
- **WHEN** a user updates blind plate details
- **THEN** the changes are persisted and audit fields are updated

#### Scenario: Delete blind plate record
- **WHEN** a user deletes a blind plate that has no associated operation records
- **THEN** the record is removed from the database

#### Scenario: Delete blind plate with dependencies
- **WHEN** a user tries to delete a blind plate that has operation records
- **THEN** the system returns error code 400 with message "该盲板存在操作记录，无法删除"

### Requirement: Blind Plate Code Uniqueness
The system SHALL enforce unique blind plate codes across the entire catalog.

#### Scenario: Duplicate code rejection
- **WHEN** a user creates or updates a blind plate with a code that already exists
- **THEN** the system returns error code 400 with message "盲板编号已存在"

### Requirement: Blind Plate Status Management
The system SHALL track blind plate status through the following states: available, installed, under_inspection, maintenance_required.

#### Scenario: Status transition on installation
- **WHEN** a blind plate is installed via operation order
- **THEN** its status changes from "available" to "installed"

#### Scenario: Status transition on removal
- **WHEN** an installed blind plate is removed via operation order
- **THEN** its status changes from "installed" to "available"

#### Scenario: Status query
- **WHEN** a user queries blind plates by status
- **THEN** only plates matching the specified status are returned
