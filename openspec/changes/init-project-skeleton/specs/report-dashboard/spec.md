## ADDED Requirements

### Requirement: Statistics Overview Dashboard
The system SHALL provide a statistics overview dashboard with key metrics for blind plate management.

#### Scenario: Get overview statistics
- **WHEN** a user requests the statistics overview
- **THEN** the system returns total blind plates, installed count, available count, pending orders, and inspection completion rate

#### Scenario: Statistics by device area
- **WHEN** a user requests statistics filtered by device area
- **THEN** the system returns metrics scoped to the specified area

### Requirement: Blind Plate Usage Report
The system SHALL generate reports on blind plate usage patterns and trends.

#### Scenario: Monthly usage report
- **WHEN** a user requests a monthly usage report for a specific month
- **THEN** the system returns installation/removal counts, top device areas, and usage trends

#### Scenario: Usage report by material
- **WHEN** a user requests a usage report grouped by material type
- **THEN** the system returns usage counts per material type

### Requirement: Inspection Completion Report
The system SHALL generate reports on inspection plan execution and completion rates.

#### Scenario: Daily inspection completion rate
- **WHEN** a user requests daily inspection completion for a date range
- **THEN** the system returns planned vs completed inspections and completion rate percentage

#### Scenario: Abnormal rate report
- **WHEN** a user requests an abnormal rate report for a date range
- **THEN** the system returns total inspections, abnormal count, and abnormal rate

### Requirement: Operation Order Report
The system SHALL generate reports on operation order statistics and processing efficiency.

#### Scenario: Order status distribution
- **WHEN** a user requests order status distribution for a date range
- **THEN** the system returns counts for each status (pending/approved/completed/rejected)

#### Scenario: Average processing time
- **WHEN** a user requests average order processing time
- **THEN** the system returns average days from creation to completion

### Requirement: Report Export
The system SHALL support exporting reports to Excel format for offline analysis.

#### Scenario: Export blind plate list
- **WHEN** a user requests to export the blind plate list
- **THEN** the system generates an Excel file with all blind plate data

#### Scenario: Export inspection report
- **WHEN** a user requests to export an inspection report
- **THEN** the system generates an Excel file with inspection records and statistics
