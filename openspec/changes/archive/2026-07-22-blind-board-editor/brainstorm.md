# Blind Board Editor — Brainstorm

## Project Context

- Vue 3 + Element Plus + Pinia + Vue Router + TypeScript
- Existing tab-based layout (AppLayout/AppSidebar/AppTabs)
- Existing `BlindPlateList.vue` for blind plate inventory management
- Using `maotu-webtopo` (npm package `maotu`) as the topology editor engine
- Reference layout: https://mt-edit.yaolm.top/

## User Needs

1. Click "盲板组态" in left sidebar → opens editor in a new tab
2. Editor based on maotu-webtopo's MtEdit component
3. Layout and functionality reference mt-edit.yaolm.top
4. **New feature**: Layer panel showing canvas elements
5. Editor + Preview (read-only) capabilities

## Design Decisions (Approved)

### Approach: Option C — Lightweight Wrapper

Minimal wrapper around MtEdit, layer panel as el-drawer, balance of delivery speed and functionality.

### Route & Menu
- `/blindplate-editor` → `BlindBoardEditor.vue`
- `/blindplate-preview/:id` → `BlindBoardPreview.vue`
- Both as children of AppLayout, embedded in tab system
- New sidebar menu item: "盲板组态" under "盲板管理"

### Editor Layout
```
[Toolbar: Save | Preview | Layers | Zoom]
[Left: MtEdit component library] [Canvas: MtEdit] [Right: Layer drawer]
```
- MtEdit fills content area
- Custom toolbar above MtEdit
- Layer panel as el-drawer from right

### Layer Panel
- Lists all SVG elements on canvas
- Per item: visibility toggle + name + lock/unlock
- Click layer item → select element on canvas
- Data via MtEdit API (`getCanvasElements()` or equivalent)

### Components
| File | Role |
|------|------|
| `BlindBoardEditor.vue` | Page container, combines MtEdit + toolbar + layer |
| `BoardToolbar.vue` | Save, preview, layer toggle, zoom controls |
| `BoardLayerPanel.vue` | el-drawer with element layer list |
| `useBoardLayers.ts` | Layer state management composable |
| `board.ts` (Pinia) | Board project store |
| `BlindBoardPreview.vue` | Read-only preview with `<mt-preview>` |

### Data Storage
- Pinia store for editor state
- Backend API for persistence
- Project fields: id, name, svg_json, thumbnail, created_by, created_at, updated_at

### Backend API
```
POST   /api/blindboard/projects
GET    /api/blindboard/projects
GET    /api/blindboard/projects/:id
PUT    /api/blindboard/projects/:id
DELETE /api/blindboard/projects/:id
```