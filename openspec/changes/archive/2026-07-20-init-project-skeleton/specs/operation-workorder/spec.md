## ADDED Requirements

### Requirement: Operation Order Creation
The system SHALL support creating operation orders for blind plate installation, removal, or restoration with required fields.

#### Scenario: Create installation order
- **WHEN** a user creates an order with type "install", blindplate_id, location_id, operator_id, and planned_date
- **THEN** the order is created with status "pending" and a unique order_no is generated

#### Scenario: Create removal order
- **WHEN** a user creates an order with type "remove" for an installed blind plate
- **THEN** the order is created with status "pending"

#### Scenario: Create restoration order
- **WHEN** a user creates an order with type "restore" for a removed blind plate
- **THEN** the order is created with status "pending"

### Requirement: Operation Order Approval Workflow
The system SHALL enforce an approval workflow with states: pending → approved → completed OR rejected.

#### Scenario: Approve pending order
- **WHEN** a supervisor approves a pending order
- **THEN** the order status changes to "approved" and the approver_id and approval_time are recorded

#### Scenario: Reject pending order
- **WHEN** a supervisor rejects a pending order with rejection reason
- **THEN** the order status changes to "rejected" and the rejection reason is stored

#### Scenario: Complete approved order
- **WHEN** an operator completes an approved order
- **THEN** the order status changes to "completed", actual_date is recorded, and the blind plate status is updated accordingly

#### Scenario: Cannot approve completed order
- **WHEN** a user tries to approve an order that is already completed
- **THEN** the system returns error code 400 with message "该工单已完成，无法审批"

### Requirement: Operation Record Logging
The system SHALL log all operation actions with operator, timestamp, and remarks.

#### Scenario: Log installation action
- **WHEN** an installation order is completed
- **THEN** an operation record is created with action "install", operator_id, and operate_time

#### Scenario: Log removal action
- **WHEN** a removal order is completed
- **THEN** an operation record is created with action "remove"

#### Scenario: Query operation history
- **WHEN** a user queries operation records for a specific blind plate
- **THEN** the system returns all historical operations sorted by time descending

### Requirement: Operation Order Query and Filtering
The system SHALL support querying operation orders with multiple filter criteria.

#### Scenario: Filter orders by status
- **WHEN** a user queries orders with status filter
- **THEN** only orders matching the specified status are returned

#### Scenario: Filter orders by date range
- **WHEN** a user queries orders within a date range
- **THEN** only orders with planned_date in the range are returned

#### Scenario: Filter orders by type
- **WHEN** a user queries orders with type filter (install/remove/restore)
- **THEN** only orders of the specified type are returned
