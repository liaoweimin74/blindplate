# blindplate-stocktake Specification

## Purpose
TBD - created by archiving change blind-plate-master-data. Update Purpose after archive.
## Requirements
### Requirement: Blind Plate Stocktake Batch Management
The system SHALL support creating stocktake batches, recording scanned blind plate codes, and automatically comparing against database records to generate difference reports.

#### Scenario: Create stocktake batch
- **WHEN** a user creates a new stocktake batch with batch name and operator
- **THEN** a stocktake batch record is created with a unique batch number (format ST-{yyyyMMdd}-{4-digit sequence}), creation time, and status "in_progress"

#### Scenario: Record scanned codes to stocktake batch
- **WHEN** a user submits a list of scanned blind plate codes to an in-progress stocktake batch
- **THEN** the system records each scanned code with the batch ID, scan time, and marks whether the code exists in the database

#### Scenario: Close stocktake batch
- **WHEN** a user closes an in-progress stocktake batch
- **THEN** the batch status changes to "closed" and the difference report is generated

### Requirement: Blind Plate Stocktake Difference Report
The system SHALL automatically generate a difference report when a stocktake batch is closed, showing matched plates, missing plates (in database but not scanned), and unexpected plates (scanned but not in database).

#### Scenario: All plates matched
- **WHEN** a stocktake batch is closed and all in-stock blind plates were scanned
- **THEN** the difference report shows zero missing and zero unexpected plates

#### Scenario: Missing plates detected
- **WHEN** a stocktake batch is closed and 3 blind plates with status "in_stock" were not scanned
- **THEN** the difference report lists those 3 plates as "missing" with their codes and details

#### Scenario: Unexpected plates detected
- **WHEN** a stocktake batch is closed and 2 scanned codes do not exist in the database
- **THEN** the difference report lists those 2 codes as "unexpected"

#### Scenario: Location mismatch detected
- **WHEN** a stocktake batch is closed and a scanned plate has status "in_use" (expected to be installed) but was scanned in storage
- **THEN** the difference report lists that plate as "location_mismatch" with a note

#### Scenario: Query stocktake batch difference report
- **WHEN** a user queries the difference report for a closed stocktake batch
- **THEN** the system returns the complete difference report with matched, missing, unexpected, and location_mismatch sections

### Requirement: Blind Plate Stocktake Batch Query
The system SHALL support querying stocktake batches with filters and pagination.

#### Scenario: Query stocktake batches by status
- **WHEN** a user queries stocktake batches with status filter "closed"
- **THEN** only closed batches are returned in paginated format sorted by creation time descending

#### Scenario: Query stocktake batch by batch number
- **WHEN** a user queries a stocktake batch by its batch number
- **THEN** the system returns the batch details including all scanned codes and the difference report if closed

