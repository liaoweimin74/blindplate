# Blind Board Editor — Tasks

## Frontend Tasks

### F1: Install maotu dependency
- Add `maotu` npm package to blindplate-web
- Import Maotu CSS in main.ts or editor component

### F2: Add route and sidebar menu
- Add route `/blindplate-editor` and `/blindplate-preview/:id` to router
- Add "盲板组态" menu item to AppSidebar

### F3: Create BlindBoardEditor.vue
- Page container using MtEdit component
- Custom toolbar (BoardToolbar) above MtEdit
- Layer panel (BoardLayerPanel) as el-drawer
- Integrate useBoardLayers composable

### F4: Create BoardToolbar.vue
- Save button → triggers MtEdit on-save-click
- Preview button → navigate to preview route
- Layer toggle button → open/close layer drawer
- Zoom controls (optional: zoom in/out/fit)

### F5: Create BoardLayerPanel.vue
- el-drawer component sliding from right
- Fetch canvas elements via MtEdit API
- Display layer list with visibility + lock controls
- Click layer item → select element on canvas

### F6: Create useBoardLayers.ts composable
- State: layers list, visible layer ids, locked layer ids
- Methods: toggleVisibility, toggleLock, selectElement
- Sync with MtEdit canvas state

### F7: Create board.ts Pinia store
- State: currentProject, projectList, loading
- Actions: fetchProjects, fetchProject, saveProject, deleteProject
- API calls via axios

### F8: Create BlindBoardPreview.vue
- Load project JSON from backend by route param :id
- Render with MtPreview component
- Read-only: no editing controls

## Backend Tasks

### B1: Create BoardProject table and model
- Database migration: board_projects table
- Fields: id, name, svg_json (TEXT/JSON), thumbnail, created_by, created_at, updated_at

### B2: Create board project CRUD API
- POST /api/blindboard/projects
- GET /api/blindboard/projects
- GET /api/blindboard/projects/:id
- PUT /api/blindboard/projects/:id
- DELETE /api/blindboard/projects/:id
- Authentication middleware on all endpoints

## Estimated Effort

| Task | Est. | Depends On |
|------|------|------------|
| F1: Install maotu | 0.5h | — |
| F2: Route + menu | 0.5h | F1 |
| F3: Editor page | 2h | F1, F2 |
| F4: Toolbar | 1h | F3 |
| F5: Layer panel | 2h | F3, F6 |
| F6: Layer composable | 1h | F3 |
| F7: Pinia store | 1h | — |
| F8: Preview page | 1h | F7 |
| B1: DB migration | 1h | — |
| B2: CRUD API | 2h | B1 |
| **Total** | **12h** | |