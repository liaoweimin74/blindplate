## MODIFIED Requirements

### Requirement: Blind Plate Catalog CRUD
The system SHALL support creating, reading, updating, and deleting blind plate records with full specification details aligned to PRD 6.2 data model, including fields for model type, thickness, factory code, purchase date, current location, install count, total usage days, lifecycle status, next inspection date, RFID tag, and QR code.

#### Scenario: Create new blind plate record
- **WHEN** a user submits blind plate data including code, model type, diameter, thickness, pressure, material, and manufacturer
- **THEN** the record is created with auto-generated ID, auto-generated QR code (format BP-{yyyyMMdd}-{6-digit sequence}), auto-generated RFID tag (UUID), and timestamps

#### Scenario: Query blind plates with filters and pagination
- **WHEN** a user queries blind plates with optional filters (keyword, modelType, material, status, lifecycleStatus) and pagination parameters (page, size)
- **THEN** the system returns paginated results sorted by creation time descending, containing only plates matching all specified filters

#### Scenario: Update blind plate information
- **WHEN** a user updates blind plate details
- **THEN** the changes are persisted and audit fields (updatedAt) are updated

#### Scenario: Delete blind plate record
- **WHEN** a user deletes a blind plate that has no associated operation records
- **THEN** the record is removed from the database

#### Scenario: Delete blind plate with dependencies
- **WHEN** a user tries to delete a blind plate that has operation records
- **THEN** the system returns error code 400 with message "该盲板存在操作记录，无法删除"

---

## ADDED Requirements

### Requirement: Blind Plate Batch Import
The system SHALL support importing blind plate records from Excel files with template download, data validation, and per-row error reporting.

#### Scenario: Download import template
- **WHEN** a user requests the Excel import template
- **THEN** the system returns an Excel file with predefined column headers (code, modelType, diameter, thickness, pressure, material, manufacturer, factoryCode, purchaseDate) and validation hints

#### Scenario: Successful batch import
- **WHEN** a user uploads an Excel file with 50 valid blind plate records
- **THEN** all 50 records are created with auto-generated QR codes and RFID tags, and the system returns a success summary with count

#### Scenario: Import with validation errors
- **WHEN** a user uploads an Excel file where row 3 has a duplicate code and row 7 is missing required field diameter
- **THEN** the system skips invalid rows, imports valid rows, and returns an error report listing row numbers and error descriptions for each failed row

#### Scenario: Import limit enforcement
- **WHEN** a user uploads an Excel file exceeding 5000 rows
- **THEN** the system rejects the upload with message "单次导入不能超过5000行"

### Requirement: Blind Plate QR Code and RFID Auto-Generation
The system SHALL automatically generate a unique QR code and RFID tag for each blind plate upon creation, whether through manual entry or batch import.

#### Scenario: Auto-generation on manual creation
- **WHEN** a user creates a new blind plate via manual entry without specifying QR code or RFID tag
- **THEN** the system generates QR code in format `BP-{yyyyMMdd}-{6-digit sequence}` and RFID tag as UUID

#### Scenario: Auto-generation on batch import
- **WHEN** a user imports blind plates via Excel without QR code or RFID columns
- **THEN** the system auto-generates QR codes and RFID tags for all imported records

#### Scenario: QR code uniqueness
- **WHEN** multiple users simultaneously create blind plates
- **THEN** the system ensures no QR code collisions by using database sequence or UUID suffix

### Requirement: Blind Plate Excel Export
The system SHALL support exporting blind plate records to Excel format with current filters applied.

#### Scenario: Export filtered results
- **WHEN** a user clicks export after filtering blind plates by status "in_stock"
- **THEN** the system generates and downloads an Excel file containing only blind plates with status in_stock, including all fields from PRD 6.2

#### Scenario: Export all records
- **WHEN** a user clicks export with no filters applied
- **THEN** the system generates an Excel file containing all blind plate records

### Requirement: Blind Plate Status Change History
The system SHALL automatically record every blind plate status change to a history table, preserving the previous status, new status, change time, operator, and reason.

#### Scenario: Status change recorded on manual update
- **WHEN** a user changes a blind plate status from "in_stock" to "under_inspection"
- **THEN** a status history record is created with previousStatus="in_stock", newStatus="under_inspection", changedAt=current timestamp, operator=current user, and a reason field if provided

#### Scenario: Status history query
- **WHEN** a user queries the status history for a specific blind plate
- **THEN** the system returns all status change records ordered by change time descending

#### Scenario: No status change no history
- **WHEN** a user updates blind plate fields other than status
- **THEN** no status history record is created

### Requirement: Blind Plate Status Enum Alignment
The system SHALL use the following status values aligned with PRD 6.2: in_stock, in_use, under_inspection, scrapped, lost. The previous status values (available, installed, removed, maintenance) MUST be migrated to the new values.

#### Scenario: Migration of existing available status
- **WHEN** the system starts with existing records having status "available"
- **THEN** these records are migrated to status "in_stock"

#### Scenario: Migration of existing installed status
- **WHEN** the system starts with existing records having status "installed"
- **THEN** these records are migrated to status "in_use"

#### Scenario: Migration of existing maintenance status
- **WHEN** the system starts with existing records having status "maintenance"
- **THEN** these records are migrated to status "under_inspection"

#### Scenario: Status query with new enum
- **WHEN** a user queries blind plates by status "in_stock"
- **THEN** only plates with status in_stock are returned, including migrated records
