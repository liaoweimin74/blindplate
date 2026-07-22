# Blind Board Editor — Proposal

## Summary

Add a blind board topology editor to the existing blind plate management system, built on `maotu-webtopo` (MtEdit). Users access it via a new sidebar menu item, work in the existing tab layout, and get a new layer panel feature on top of the standard MtEdit functionality.

## Motivation

Blind plate operators need a visual way to design and document blind plate topologies — mapping valves, pipes, instruments, and flanges on a 2D canvas. The current system only has a list-based blind plate inventory, which lacks spatial context.

## Approach: Option C — Lightweight Wrapper

**Why not A (direct embed)?** — MtEdit's default layout doesn't have a layer panel. Adding it requires wrapping, so pure direct embed isn't sufficient.

**Why not B (full re-encapsulation)?** — Too much effort for this phase. MtEdit's internal API is not fully documented, and tight coupling would make future upgrades harder.

**Why C (lightweight wrapper)?** — Best balance of delivery speed and functionality. MtEdit stays as-is, we add a custom toolbar + layer drawer above/around it. Layer panel communicates with MtEdit through its public API.

## Key Features

1. **Topology Editor** — Full MtEdit capability: drag SVG symbols, connect elements, zoom/pan
2. **Layer Panel** — New feature showing all canvas elements with visibility/lock controls
3. **Tab Integration** — Opens in existing tab system like all other pages
4. **Save & Preview** — Projects stored in backend, previewed with MtPreview

## Scope

### In Scope
- `BlindBoardEditor.vue` — main editor page
- `BoardToolbar.vue` — save, preview, layer toggle, zoom
- `BoardLayerPanel.vue` — layer drawer with element list
- `useBoardLayers.ts` — layer state management
- `board.ts` — Pinia store for projects
- `BlindBoardPreview.vue` — read-only preview
- Route + sidebar menu + backend API + DB table

### Out of Scope (v1)
- Advanced layer grouping/hierarchy
- Layer reordering via drag
- Auto-layout algorithms
- Template/project sharing

## Risks

| Risk | Mitigation |
|------|-----------|
| MtEdit doesn't expose canvas element API | Fallback to parsing export JSON |
| MtEdit internal layout conflicts with custom toolbar | Use CSS isolation, position toolbar outside MtEdit container |
| maotu LGPL-3.0 license compliance | Using as npm dependency (linking), not modifying core library |
| SVG symbols for blind plates not available | Start with maotu built-in shapes, add custom SVGs later |