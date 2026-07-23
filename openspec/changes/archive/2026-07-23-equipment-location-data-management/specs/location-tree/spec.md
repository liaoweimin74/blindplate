## MODIFIED Requirements

### Requirement: Location Tree Structure
The system SHALL manage blind plate locations in a hierarchical tree structure with four levels: FACTORY → EQUIPMENT → UNIT → ISOLATION_POINT, where FACTORY nodes are root nodes, EQUIPMENT nodes are children of FACTORY nodes, UNIT nodes are children of EQUIPMENT nodes, and ISOLATION_POINT nodes are children of UNIT nodes.

#### Scenario: Get location tree
- **WHEN** a user requests the location tree
- **THEN** the system returns a nested tree structure with FACTORY nodes as root nodes, EQUIPMENT nodes as children of FACTORY, UNIT nodes as children of EQUIPMENT, and ISOLATION_POINT nodes as children of UNIT

#### Scenario: Add new factory
- **WHEN** an admin creates a new factory with name, code, and description
- **THEN** the factory is added as a root node in the location tree with type FACTORY and level 0

#### Scenario: Add equipment under factory
- **WHEN** an admin creates a new equipment node with a parent whose type is FACTORY
- **THEN** the equipment is added as a child of that factory node with type EQUIPMENT and level 1

#### Scenario: Add unit under equipment
- **WHEN** an admin creates a new unit node with a parent whose type is EQUIPMENT
- **THEN** the unit is added as a child of that equipment node with type UNIT and level 2

#### Scenario: Add isolation point under unit
- **WHEN** an admin creates a new isolation point node with a parent whose type is UNIT
- **THEN** the isolation point is added as a child of that unit node with type ISOLATION_POINT and level 3

#### Scenario: Reject invalid parent type
- **WHEN** an admin tries to create an EQUIPMENT node with a parent whose type is not FACTORY
- **THEN** the system returns error code 400 with message "装置节点必须挂在工厂节点下"

---

### Requirement: Location CRUD Operations
The system SHALL support creating, reading, updating, and deleting location nodes with dependency checks, unique code validation, and hierarchy level constraints.

#### Scenario: Update location name
- **WHEN** an admin updates a location node's name or description
- **THEN** the changes are persisted without affecting child nodes

#### Scenario: Delete location with children
- **WHEN** an admin tries to delete a location node that has children
- **THEN** the system returns error code 400 with message "该位置下存在子节点，无法删除"

#### Scenario: Delete location with blind plates
- **WHEN** an admin tries to delete a location that has blind plates assigned
- **THEN** the system returns error code 400 with message "该位置下存在盲板，无法删除"

#### Scenario: Reject duplicate code
- **WHEN** an admin creates or updates a location with a code that already exists on another node
- **THEN** the system returns error code 400 with message "位置编码已存在"

#### Scenario: Reject missing code for isolation point
- **WHEN** an admin creates or updates an ISOLATION_POINT location without a code
- **THEN** the system returns error code 400 with message "隔离点编码不能为空"

#### Scenario: Enforce hierarchy level constraint
- **WHEN** an admin creates a location node whose parent type does not match the expected parent type for the new node type
- **THEN** the system returns error code 400 with a message describing the allowed parent-child relationship

---

### Requirement: Blind Plate Location Assignment
The system SHALL support assigning blind plates only to ISOLATION_POINT type locations in the tree.

#### Scenario: Assign blind plate to isolation point
- **WHEN** a user assigns a blind plate to an ISOLATION_POINT location
- **THEN** the blind plate's location_id is updated and the location shows the assigned plate

#### Scenario: Reject assignment to non-isolation-point
- **WHEN** a user attempts to assign a blind plate to a location whose type is not ISOLATION_POINT
- **THEN** the system returns error code 400 with message "盲板只能绑定到隔离点位置"

#### Scenario: Query blind plates by location hierarchy
- **WHEN** a user queries blind plates filtered by FACTORY or EQUIPMENT or UNIT
- **THEN** only plates assigned to ISOLATION_POINT nodes within the specified location hierarchy are returned
