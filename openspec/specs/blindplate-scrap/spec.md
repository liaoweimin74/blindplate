# blindplate-scrap Specification

## Purpose
TBD - created by archiving change blind-plate-master-data. Update Purpose after archive.
## Requirements
### Requirement: Blind Plate Scrap Application
The system SHALL support submitting a scrap application for a blind plate, including a reason and applicant information, transitioning the scrap record to "pending" approval state.

#### Scenario: Submit scrap application
- **WHEN** a user submits a scrap application for a blind plate with reason and applicant name
- **THEN** a scrap record is created with status "pending", applicant, applyTime, and reason, and the blind plate remains in its current status

#### Scenario: Scrap application for already scrapped plate
- **WHEN** a user submits a scrap application for a blind plate that already has status "scrapped"
- **THEN** the system returns error code 400 with message "该盲板已报废，无法重复申请"

#### Scenario: Scrap application for plate with existing pending application
- **WHEN** a user submits a scrap application for a blind plate that already has a pending scrap application
- **THEN** the system returns error code 400 with message "该盲板已有待审批的报废申请"

### Requirement: Blind Plate Scrap Approval
The system SHALL support approving or rejecting scrap applications, with an approver and approval comment. Approved scrap applications remove the blind plate from available inventory.

#### Scenario: Approve scrap application
- **WHEN** an approver approves a pending scrap application with a comment
- **THEN** the scrap record status changes to "approved", the approver and approvalTime are recorded, and the blind plate's status changes to "scrapped" and lifecycleStatus changes to "scrapped"

#### Scenario: Reject scrap application
- **WHEN** an approver rejects a pending scrap application with a comment
- **THEN** the scrap record status changes to "rejected", the approver and approvalTime are recorded, and the blind plate retains its original status

#### Scenario: Query scrap records
- **WHEN** a user queries scrap records with optional filters (status, applicant, date range)
- **THEN** the system returns paginated scrap records sorted by applyTime descending

#### Scenario: Scrap history for a blind plate
- **WHEN** a user queries the scrap history for a specific blind plate
- **THEN** the system returns all scrap records for that blind plate ordered by applyTime descending

### Requirement: Blind Plate Scrap Inventory Removal
The system SHALL ensure that scrapped blind plates are excluded from available inventory queries.

#### Scenario: Scrapped plate excluded from available inventory
- **WHEN** a user queries blind plates with status filter "in_stock"
- **THEN** blind plates with status "scrapped" are not included in the results

#### Scenario: Scrapped plate excluded from import
- **WHEN** an Excel import contains a code that belongs to a scrapped blind plate
- **THEN** the system returns an error for that row indicating the code belongs to a scrapped plate

