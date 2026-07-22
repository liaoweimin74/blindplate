# Blind Board Editor — Design

## 1. Purpose

The blind board configuration editor allows users to visually design blind plate topology diagrams by dragging SVG symbols onto a canvas. It is built on top of the `maotu-webtopo` library and embedded into the existing tab-based application layout.

## 2. Routes

| Path | Component | Title | Notes |
|------|-----------|-------|-------|
| `/blindplate-editor` | `BlindBoardEditor.vue` | Blind Board Editor | Editor tab, closable |
| `/blindplate-preview/:id` | `BlindBoardPreview.vue` | Blind Board Preview | Preview tab, closable |

Both are children of the AppLayout route, inheriting the tab system.

## 3. Menu

Add to AppSidebar menu items, after "盲板管理":

```typescript
{ key: '/blindplate-editor', title: '盲板组态', icon: Edit, path: '/blindplate-editor' }
```

## 4. Editor Layout

```
┌────────────────────────────────────────────────────────┐
│  BoardToolbar                                           │
│  [Save] [Preview] [Layers] [Zoom In] [Zoom Out] [Fit]  │
├──────────┬───────────────────────────────┬──────────────┤
│          │                               │              │
│  MtEdit  │      MtEdit Canvas            │  Layer Panel │
│  Library │                               │  (el-drawer) │
│  (left)  │                               │  (right)     │
│          │                               │              │
│          │                               │              │
├──────────┴───────────────────────────────┴──────────────┤
│  Status Bar (optional)                                   │
└──────────────────────────────────────────────────────────┘
```

### Layout rules:
- `BlindBoardEditor.vue` fills the tab content area (`height: 100%`)
- MtEdit fills the remaining space after toolbar
- Layer panel is an `el-drawer` from the right side, size ~280px
- Toolbar is a horizontal bar with action buttons

## 5. Component Tree

```
AppLayout
├── AppSidebar (modified: add menu item)
├── AppTabs
└── <router-view>
    ├── BlindBoardEditor
    │   ├── BoardToolbar
    │   ├── <mt-edit> (from maotu-webtopo)
    │   └── BoardLayerPanel (el-drawer)
    │       └── Layer items (v-for)
    └── BlindBoardPreview
        └── <mt-preview> (from maotu-webtopo)
```

## 6. Layer Panel Design

### Trigger
- Toolbar button "Layers" toggles el-drawer visibility
- Default: closed

### Layer Item
```
[☑/☐] [🔒/🔓] Element Name
```

| Control | Action |
|---------|--------|
| ☑/☐ (eye icon) | Toggle element visibility on canvas |
| 🔒/🔓 (lock icon) | Toggle element lock state (prevent drag) |
| Element name | Click to select & highlight element on canvas |

### Data Source
Via MtEdit API — `MtEditRef.value?.getCanvasElements()` or equivalent method that returns the list of placed elements with their IDs, names, and properties.

## 7. Data Model

### BoardProject (backend)
```typescript
interface BoardProject {
  id: number
  name: string
  svgJson: object      // Complete MtEdit export JSON
  thumbnail?: string   // Base64 or URL
  createdBy: number
  createdAt: string
  updatedAt: string
}
```

### Pinia Store (frontend)
```typescript
// stores/board.ts
interface BoardState {
  currentProject: BoardProject | null
  projectList: BoardProject[]
  loading: boolean
}
```

## 8. API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/blindboard/projects` | Create/save project |
| GET | `/api/blindboard/projects` | List all projects |
| GET | `/api/blindboard/projects/:id` | Get single project |
| PUT | `/api/blindboard/projects/:id` | Update project |
| DELETE | `/api/blindboard/projects/:id` | Delete project |

## 9. Tech Stack

- Vue 3 + TypeScript + Element Plus
- Pinia for state management
- `maotu` npm package (maotu-webtopo)
- Axios for API calls
- Existing backend framework (blindplate-server)