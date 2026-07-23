# blindplate-inspection Specification

## Purpose
TBD - created by archiving change blind-plate-master-data. Update Purpose after archive.
## Requirements
### Requirement: Blind Plate Inspection Record Management
The system SHALL support creating, reading, updating, and deleting blind plate inspection records, including inspection date, result, next inspection date, inspector, and remarks.

#### Scenario: Create inspection record
- **WHEN** a user submits an inspection record for a blind plate with inspection date, result (qualified/unqualified), next inspection date, and inspector
- **THEN** the record is created and the blind plate's nextInspectionDate field is updated to match the record's next inspection date

#### Scenario: Query inspection records for a blind plate
- **WHEN** a user queries inspection records for a specific blind plate
- **THEN** the system returns all inspection records ordered by inspection date descending

#### Scenario: Update inspection record
- **WHEN** a user updates an inspection record's result from "unqualified" to "qualified"
- **THEN** the change is persisted and the blind plate's lifecycleStatus is recalculated

#### Scenario: Delete inspection record
- **WHEN** a user deletes an inspection record
- **THEN** the record is removed from the database

### Requirement: Blind Plate Inspection Due Reminder
The system SHALL automatically scan blind plates daily and identify those with nextInspectionDate approaching or overdue, generating alerts.

#### Scenario: Inspection due in 7 days
- **WHEN** the daily scheduled scan runs and a blind plate has nextInspectionDate within 7 days from today
- **THEN** the blind plate's lifecycleStatus is updated to "到期检验" (inspection_due) and an alert is generated

#### Scenario: Inspection overdue
- **WHEN** the daily scheduled scan runs and a blind plate has nextInspectionDate before today and no recent qualified inspection
- **THEN** the blind plate's lifecycleStatus is updated to "超期" (overdue) and an alert is generated

#### Scenario: Inspection completed on time
- **WHEN** a user creates a qualified inspection record with nextInspectionDate in the future
- **THEN** the blind plate's lifecycleStatus is updated to "正常" (normal)

#### Scenario: Query inspection due alerts
- **WHEN** a user queries the inspection alert list
- **THEN** the system returns all blind plates with lifecycleStatus "到期检验" or "超期", sorted by nextInspectionDate ascending

### Requirement: Blind Plate Lifecycle Status Management
The system SHALL maintain a lifecycleStatus field on each blind plate with values: normal (正常), inspection_due (到期检验), overdue (超期), scrapped (报废), reflecting the current lifecycle state based on inspection records and scrap status.

#### Scenario: New blind plate lifecycle
- **WHEN** a new blind plate is created with a nextInspectionDate in the future
- **THEN** lifecycleStatus is set to "normal"

#### Scenario: Lifecycle becomes scrapped
- **WHEN** a blind plate's scrap application is approved
- **THEN** lifecycleStatus is updated to "scrapped" and status is set to "scrapped"

#### Scenario: Lifecycle resets after qualified inspection
- **WHEN** a qualified inspection record is created for a blind plate with lifecycleStatus "overdue" or "inspection_due"
- **THEN** lifecycleStatus is updated to "normal"

