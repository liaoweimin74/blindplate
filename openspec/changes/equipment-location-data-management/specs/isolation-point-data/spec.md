## ADDED Requirements

### Requirement: Isolation Point Detail Entity
The system SHALL maintain an IsolationPointDetail entity in a 1:1 relationship with ISOLATION_POINT type Location nodes, storing PID diagram reference, medium, pressure, temperature, hazard level, isolation type, three-dimensional coordinates, and associated diagram project ID.

#### Scenario: Auto-create detail on isolation point creation
- **WHEN** an admin creates a new ISOLATION_POINT type Location node
- **THEN** the system SHALL automatically create an associated IsolationPointDetail record linked via location_id foreign key

#### Scenario: Query isolation point detail by location id
- **WHEN** a user queries the detail of an ISOLATION_POINT location by its location id
- **THEN** the system returns the full IsolationPointDetail record including pid_diagram_ref, medium, pressure, temperature, hazard_level, isolation_type, coord_x, coord_y, coord_z, and diagram_id

#### Scenario: Update isolation point detail fields
- **WHEN** an admin updates the IsolationPointDetail fields (medium, pressure, temperature, hazard_level, isolation_type, coordinates, or diagram reference)
- **THEN** the changes are persisted and a LocationChangeRecord is generated tracking the field-level differences

#### Scenario: Reject detail creation for non-isolation-point
- **WHEN** the system attempts to create an IsolationPointDetail for a Location whose type is not ISOLATION_POINT
- **THEN** the system returns error code 400 with message "隔离点详情只能关联隔离点类型位置"

---

### Requirement: Hazard Level Per PRD Risk Classification
The system SHALL support four hazard levels (A, B, C, D) on IsolationPointDetail, aligned with PRD 7.10.1 risk classification.

#### Scenario: Set hazard level on isolation point detail
- **WHEN** an admin sets the hazard_level field to one of A, B, C, or D
- **THEN** the value is persisted and validated against the allowed enum

#### Scenario: Reject invalid hazard level
- **WHEN** an admin sets the hazard_level field to a value outside of A, B, C, or D
- **THEN** the system returns error code 400 with message "危害等级必须为A、B、C或D"

---

### Requirement: Isolation Type Classification
The system SHALL support classifying isolation points by isolation type (BLIND_PLATE, DOUBLE_BLOCK, VALVE, OTHER).

#### Scenario: Set isolation type on isolation point detail
- **WHEN** an admin sets the isolation_type field to one of BLIND_PLATE, DOUBLE_BLOCK, VALVE, or OTHER
- **THEN** the value is persisted and validated

#### Scenario: Reject invalid isolation type
- **WHEN** an admin sets the isolation_type field to a value outside the allowed enumeration
- **THEN** the system returns error code 400 with message "隔离类型无效"

---

### Requirement: Isolation Point Location Coordinate Binding
The system SHALL store three-dimensional coordinates (coord_x, coord_y, coord_z) on IsolationPointDetail and an optional diagram_id referencing an existing BlindBoardProject for location marking on flow diagrams or 3D models.

#### Scenario: Set coordinates on isolation point
- **WHEN** an admin sets coord_x, coord_y, coord_z values on an IsolationPointDetail
- **THEN** the coordinates are persisted as double precision values

#### Scenario: Associate isolation point with diagram project
- **WHEN** an admin sets diagram_id on an IsolationPointDetail to an existing BlindBoardProject ID
- **THEN** the association is persisted and the isolation point can be located on the referenced diagram

#### Scenario: Reject coordinates out of range
- **WHEN** an admin sets coord_x, coord_y, or coord_z to null or a non-numeric value
- **THEN** the system returns error code 400 with message "坐标值无效"

---

### Requirement: Excel Batch Import of Isolation Point Master Data
The system SHALL support importing isolation point master data via Excel template upload, with validation of each row and a per-row error report.

#### Scenario: Download import template
- **WHEN** a user requests the Excel import template
- **THEN** the system returns an Excel file with columns: code, name, type, parent_code, medium, pressure, temperature, hazard_level, isolation_type

#### Scenario: Upload valid Excel file
- **WHEN** a user uploads an Excel file where all rows are valid
- **THEN** the system creates Location nodes (type=ISOLATION_POINT) and associated IsolationPointDetail records for each row, and returns a success summary with the count of imported records

#### Scenario: Upload Excel with partial errors
- **WHEN** a user uploads an Excel file where some rows fail validation (missing code, duplicate code, invalid parent_code, or invalid hazard_level)
- **THEN** the system commits the valid rows, returns a list of error rows with row numbers and error messages, and does not roll back the successfully imported records

#### Scenario: Reject non-Excel file upload
- **WHEN** a user uploads a file that is not a valid .xlsx or .xls file
- **THEN** the system returns error code 400 with message "请上传Excel文件"
