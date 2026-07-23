## ADDED Requirements

### Requirement: Location Change Record Entity
The system SHALL maintain a LocationChangeRecord entity that captures field-level change snapshots for Location and IsolationPointDetail modifications, including change type, applicant, status, and approval metadata.

#### Scenario: Record change on location create
- **WHEN** an admin creates a new Location node
- **THEN** the system creates a LocationChangeRecord with change_type=CREATE, status=APPROVED (auto-approved for create), field_name set to "*" and new_value set to a JSON snapshot of the created entity

#### Scenario: Record change on location update
- **WHEN** an admin updates a Location or IsolationPointDetail field
- **THEN** the system creates a LocationChangeRecord with change_type=UPDATE, one record per changed field containing field_name, old_value, and new_value, and status=PENDING

#### Scenario: Record change on location delete
- **WHEN** an admin deletes a Location node
- **THEN** the system creates a LocationChangeRecord with change_type=DELETE and status=PENDING before removing the node

#### Scenario: Record change on location move
- **WHEN** an admin changes a Location node's parentId to move it within the tree
- **THEN** the system creates a LocationChangeRecord with change_type=MOVE, field_name=parentId, old_value and new_value capturing the previous and new parent ids

---

### Requirement: Change Approval Workflow
The system SHALL implement a single-stage approval workflow for Location changes of type UPDATE, DELETE, and MOVE. CREATE changes are auto-approved. The workflow stages are PENDING, APPROVED, and REJECTED.

#### Scenario: Submit change for approval
- **WHEN** an admin triggers an UPDATE, DELETE, or MOVE change on a Location
- **THEN** the system creates a LocationChangeRecord with status=PENDING and the change is not applied to the live entity until approved

#### Scenario: Approve pending change
- **WHEN** an admin with the ADMIN role approves a PENDING LocationChangeRecord
- **THEN** the system sets status=APPROVED, records approver_id and approval_comment, applies the change to the live entity, and sets approved_at timestamp

#### Scenario: Reject pending change
- **WHEN** an admin with the ADMIN role rejects a PENDING LocationChangeRecord
- **THEN** the system sets status=REJECTED, records approver_id and the rejection reason in approval_comment, and discards the proposed change without modifying the live entity

#### Scenario: Reject approval by non-admin
- **WHEN** a user without the ADMIN role attempts to approve or reject a PENDING LocationChangeRecord
- **THEN** the system returns error code 403 with message "仅管理员可审批变更"

#### Scenario: Reject duplicate approval
- **WHEN** an admin attempts to approve or reject a LocationChangeRecord whose status is already APPROVED or REJECTED
- **THEN** the system returns error code 400 with message "该变更已处理"

---

### Requirement: Change History Query
The system SHALL support querying LocationChangeRecord history filtered by location_id, change_type, status, applicant_id, approver_id, and time range.

#### Scenario: Query change history by location
- **WHEN** a user queries change records filtered by a specific location_id
- **THEN** the system returns all LocationChangeRecord entries for that location sorted by created_at descending

#### Scenario: Filter change history by status
- **WHEN** a user queries change records filtered by status=PENDING
- **THEN** only records with status=PENDING are returned

#### Scenario: Filter change history by time range
- **WHEN** a user queries change records filtered by a start and end time
- **THEN** only records with created_at within the time range are returned
