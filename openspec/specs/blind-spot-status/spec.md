# blind-spot-status Specification

## Purpose
TBD - created by archiving change blind-spot-status-ledger. Update Purpose after archive.
## Requirements
### Requirement: Plant-Wide Blind Spot Status Overview
The system SHALL provide a read-only ledger that lists every isolation point with its current pass/blind status computed in real time from completed operation order history, without requiring a persistent status table.

#### Scenario: View all isolation point statuses
- **WHEN** a user navigates to the blind spot status ledger page
- **THEN** the system displays a table of all isolation points, each showing its location name, location path, current status (通/盲/未知), current blind plate code, related operation order, last operation time, status duration, and abnormal flag

#### Scenario: Isolation point with no operation history
- **WHEN** an isolation point has no completed INSTALL or REMOVE operation orders
- **THEN** its current status is displayed as "未知" (unknown) with no abnormal flag

#### Scenario: Isolation point with latest INSTALL operation
- **WHEN** the most recent completed operation on an isolation point is of type INSTALL
- **THEN** its current status is displayed as "盲" (blind) with the installed blind plate's code shown

#### Scenario: Isolation point with latest REMOVE operation
- **WHEN** the most recent completed operation on an isolation point is of type REMOVE
- **THEN** its current status is displayed as "通" (pass) with no current blind plate

#### Scenario: Status duration computation
- **WHEN** an isolation point has a computed current status derived from its latest operation
- **THEN** the system displays the duration in hours since that operation's actual date

---

### Requirement: Blind Spot Status Filtering
The system SHALL support filtering the blind spot status ledger by device/area hierarchy, current status, and abnormal-only toggle.

#### Scenario: Filter by device or area
- **WHEN** a user selects a device or area from the location tree filter
- **THEN** the ledger displays only isolation points within that location subtree

#### Scenario: Filter by current status
- **WHEN** a user selects a status value (通/盲/未知) from the status filter
- **THEN** the ledger displays only isolation points matching that status

#### Scenario: Filter to abnormal only
- **WHEN** a user toggles the "abnormal only" switch on
- **THEN** the ledger displays only isolation points with an active abnormal flag

---

### Requirement: Abnormal State Detection
The system SHALL automatically detect and highlight abnormal isolation point states, including long-term blind without removal and status conflicts.

#### Scenario: Long-term blind without removal
- **WHEN** an isolation point's current status is "盲" and the status duration exceeds 720 hours (30 days)
- **THEN** the system flags the isolation point as abnormal with description "盲板已挂载超过30天未拆除" and highlights the row

#### Scenario: Status conflict detection
- **WHEN** an isolation point has two or more consecutive completed INSTALL operations without an intervening completed REMOVE operation
- **THEN** the system flags the isolation point as abnormal with description "存在连续安装操作无拆除记录，状态冲突" and highlights the row

#### Scenario: Normal state not flagged
- **WHEN** an isolation point's current status is "盲" with duration under 720 hours and no status conflict
- **THEN** the system does not flag the isolation point as abnormal

---

### Requirement: Status History Timeline
The system SHALL provide a per-isolation-point status change timeline traceable from operation order history.

#### Scenario: View status history for an isolation point
- **WHEN** a user clicks an isolation point row in the ledger
- **THEN** the system displays a timeline dialog showing all INSTALL, REMOVE, and INSPECT operations on that isolation point, ordered by actual date descending

#### Scenario: Timeline entry content
- **WHEN** the status history timeline is displayed
- **THEN** each entry shows the operation time, operation type, blind plate code, blind plate model, and the resulting pass/blind status after that operation

#### Scenario: Isolation point with no history
- **WHEN** a user views the status history of an isolation point that has no operation orders
- **THEN** the timeline displays an empty state message indicating no operation records

---

### Requirement: Blind Spot Status API
The system SHALL expose read-only API endpoints for the blind spot status ledger, returning computed status data without persisting a status table.

#### Scenario: List status with no filters
- **WHEN** a client sends GET `/api/v1/blind-spot-status` with no query parameters
- **THEN** the system returns a Result-wrapped list of BlindSpotStatusDTO for all isolation points

#### Scenario: List status with filters
- **WHEN** a client sends GET `/api/v1/blind-spot-status?locationId=1&status=盲&abnormalOnly=true`
- **THEN** the system returns only isolation points within location 1's subtree that have status "盲" and an active abnormal flag

#### Scenario: Get status history
- **WHEN** a client sends GET `/api/v1/blind-spot-status/{locationId}/history`
- **THEN** the system returns a Result-wrapped list of StatusHistoryDTO ordered by operation time descending

#### Scenario: Unauthenticated access
- **WHEN** a client sends a request to any blind spot status endpoint without a valid JWT token
- **THEN** the system returns HTTP 401 Unauthorized

