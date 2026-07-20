# location-tree Specification

## Purpose
TBD - created by archiving change init-project-skeleton. Update Purpose after archive.
## Requirements
### Requirement: Location Tree Structure
The system SHALL manage blind plate locations in a hierarchical tree structure with three levels: device_area → pipeline_no → flange_no.

#### Scenario: Get location tree
- **WHEN** a user requests the location tree
- **THEN** the system returns a nested tree structure with device areas as root nodes, pipelines as children, and flanges as leaf nodes

#### Scenario: Add new device area
- **WHEN** an admin creates a new device area with name and description
- **THEN** the area is added as a root node in the location tree

#### Scenario: Add pipeline under area
- **WHEN** an admin creates a pipeline under a specific device area
- **THEN** the pipeline is added as a child of that area node

#### Scenario: Add flange under pipeline
- **WHEN** an admin creates a flange under a specific pipeline
- **THEN** the flange is added as a child of that pipeline node

### Requirement: Location CRUD Operations
The system SHALL support creating, reading, updating, and deleting location nodes with dependency checks.

#### Scenario: Update location name
- **WHEN** an admin updates a location node's name or description
- **THEN** the changes are persisted without affecting child nodes

#### Scenario: Delete location with children
- **WHEN** an admin tries to delete a location node that has children
- **THEN** the system returns error code 400 with message "该位置下存在子节点，无法删除"

#### Scenario: Delete location with blind plates
- **WHEN** an admin tries to delete a location that has blind plates assigned
- **THEN** the system returns error code 400 with message "该位置下存在盲板，无法删除"

### Requirement: Blind Plate Location Assignment
The system SHALL support assigning blind plates to specific locations in the tree.

#### Scenario: Assign blind plate to location
- **WHEN** a user assigns a blind plate to a flange location
- **THEN** the blind plate's location_id is updated and the location shows the assigned plate

#### Scenario: Query blind plates by location
- **WHEN** a user queries blind plates filtered by device area or pipeline
- **THEN** only plates in the specified location hierarchy are returned

