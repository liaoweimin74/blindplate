# Blind Board Editor — Implementation Plan

## Phase 1: Backend (Database + API)

### Step 1.1: BoardProject DB migration
- Create migration for `board_projects` table:
  ```sql
  CREATE TABLE board_projects (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    svg_json JSON NOT NULL,
    thumbnail VARCHAR(500),
    created_by INT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
  );
  ```
- File: `blindplate-server/src/main/resources/db/migration/...`

### Step 1.2: BoardProject API
- CRUD endpoints in blindplate-server
- POST `/api/blindboard/projects` — create
- GET `/api/blindboard/projects` — list (summary only, no svg_json)
- GET `/api/blindboard/projects/:id` — get full
- PUT `/api/blindboard/projects/:id` — update
- DELETE `/api/blindboard/projects/:id` — delete
- All endpoints require auth middleware

## Phase 2: Frontend — Foundation

### Step 2.1: Install maotu
```bash
cd blindplate-web
npm install maotu
```

### Step 2.2: Add routes
- Add `/blindplate-editor` → `BlindBoardEditor.vue`
- Add `/blindplate-preview/:id` → `BlindBoardPreview.vue`
- Both as children of AppLayout
- meta: `{ requiresAuth: true, title: '盲板组态', closable: true }`

### Step 2.3: Add sidebar menu item
- In AppSidebar, add after `盲板管理`:
  ```typescript
  { key: '/blindplate-editor', title: '盲板组态', icon: Edit, path: '/blindplate-editor' }
  ```

## Phase 3: Frontend — Editor

### Step 3.1: BlindBoardEditor.vue
- Full-height container
- Import MtEdit from `maotu` package
- MtEdit ref for API access
- Top: BoardToolbar
- Center: MtEdit (`flex: 1`)
- Right: BoardLayerPanel (el-drawer)
- Handle save → API call
- Handle preview → router push

### Step 3.2: BoardToolbar.vue
- Props: `onSave`, `onPreview`, `onLayerToggle`
- Buttons: Save, Preview, Layer toggle
- Optional zoom controls

### Step 3.3: useBoardLayers.ts
```typescript
interface LayerItem {
  id: string
  name: string
  visible: boolean
  locked: boolean
}
```
- `initFromCanvas(mtEditRef)` — fetch elements from MtEdit
- `toggleVisibility(id)` — toggle layer visible/hidden
- `toggleLock(id)` — toggle layer locked/unlocked
- `selectElement(id)` — select element on MtEdit canvas
- Reactive `layers` ref

### Step 3.4: BoardLayerPanel.vue
- el-drawer: `direction="rtl"`, `size="280px"`
- Props: `layers`, `modelValue` (visible)
- Emits: `update:modelValue`, `toggle-visibility`, `toggle-lock`, `select`
- Each layer item row:
  - Eye icon (visible/hidden toggle)
  - Lock icon (locked/unlocked toggle)
  - Element name (click to select)

### Step 3.5: board.ts Pinia store
```typescript
// Actions
saveProject(name: string, svgJson: object) → POST/PUT
fetchProject(id: number) → GET /:id
fetchProjects() → GET /
deleteProject(id: number) → DELETE /:id
```

## Phase 4: Frontend — Preview

### Step 4.1: BlindBoardPreview.vue
- Load project from store or API by route param `:id`
- Render `<mt-preview>` with project JSON
- Back button to return to editor
- No editing UI

## Verification

1. `npm run build` passes with no type errors
2. Sidebar "盲板组态" menu visible and clickable
3. Editor tab opens with MtEdit rendering
4. Layer panel shows canvas elements
5. Toggle visibility hides/shows elements on canvas
6. Save triggers API call
7. Preview loads and renders read-only
8. Backend CRUD endpoints respond correctly