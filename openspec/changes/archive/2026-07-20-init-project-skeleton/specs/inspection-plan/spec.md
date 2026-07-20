## ADDED Requirements

### Requirement: Inspection Plan Management
The system SHALL support creating, updating, and deactivating inspection plans with configurable cycles.

#### Scenario: Create daily inspection plan
- **WHEN** an admin creates a plan with cycle "daily" and assigns a responsible person
- **THEN** the plan is created with status "active"

#### Scenario: Create weekly inspection plan
- **WHEN** an admin creates a plan with cycle "weekly"
- **THEN** the plan is created and the system calculates the next inspection date

#### Scenario: Deactivate inspection plan
- **WHEN** an admin deactivates an inspection plan
- **THEN** the plan status changes to "inactive" and no new inspections are generated

### Requirement: Inspection Execution
The system SHALL support executing inspections by recording check results for each blind plate in scope.

#### Scenario: Execute inspection for a plan
- **WHEN** an inspector executes an inspection for a plan
- **THEN** an inspection record is created with inspector_id, inspect_time, and overall result

#### Scenario: Record individual check items
- **WHEN** an inspector records check results for each blind plate
- **THEN** inspection items are created with check_item, check_result (normal/abnormal), and optional abnormal_desc

#### Scenario: Mark inspection as normal
- **WHEN** all check items in an inspection are normal
- **THEN** the inspection record result is set to "normal"

#### Scenario: Mark inspection as abnormal
- **WHEN** any check item in an inspection is abnormal
- **THEN** the inspection record result is set to "abnormal" and abnormal descriptions are required

### Requirement: Inspection Anomaly Closure
The system SHALL track anomaly resolution from discovery through rectification to verification.

#### Scenario: Report anomaly from inspection
- **WHEN** an inspection identifies an abnormal item
- **THEN** an anomaly record is created with blindplate_id, abnormal_desc, and discovery_time

#### Scenario: Record rectification action
- **WHEN** a responsible person records rectification action for an anomaly
- **THEN** the anomaly status changes to "rectified" with rectification_desc and rectification_time

#### Scenario: Verify rectification
- **WHEN** a supervisor verifies the rectification is complete
- **THEN** the anomaly status changes to "closed" with verification_time and verifier_id

### Requirement: Inspection Record Query
The system SHALL support querying inspection records with filtering and pagination.

#### Scenario: Query records by date range
- **WHEN** a user queries inspection records within a date range
- **THEN** only records with inspect_time in the range are returned

#### Scenario: Query records by result
- **WHEN** a user queries inspection records filtered by result (normal/abnormal)
- **THEN** only matching records are returned

#### Scenario: Query records by inspector
- **WHEN** a user queries inspection records for a specific inspector
- **THEN** only records where inspector_id matches are returned
