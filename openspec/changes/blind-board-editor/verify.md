# Blind Board Editor — Verification Plan

## Build Verification
- [ ] `cd blindplate-web && npm run build` exits with code 0
- [ ] No TypeScript errors (vue-tsc passes)
- [ ] No lint errors

## Frontend Verification
- [ ] "盲板组态" menu item visible in sidebar after "盲板管理"
- [ ] Click menu opens editor tab with title "盲板组态"
- [ ] Editor tab is closable
- [ ] MtEdit component renders with left library panel + canvas
- [ ] Custom toolbar renders above MtEdit with Save, Preview, Layers buttons
- [ ] Click Layers button opens el-drawer from right
- [ ] Layer panel lists elements on canvas (if any placed)
- [ ] Toggle visibility in layer panel → element hidden/shown on canvas
- [ ] Toggle lock in layer panel → element locked/unlocked
- [ ] Click layer item name → element selected on canvas
- [ ] Save button triggers API call with JSON payload
- [ ] Preview button navigates to preview route
- [ ] BlindBoardPreview renders MtPreview with saved data

## Backend Verification
- [ ] POST `/api/blindboard/projects` returns 201 with project ID
- [ ] GET `/api/blindboard/projects` returns project list
- [ ] GET `/api/blindboard/projects/:id` returns full project JSON
- [ ] PUT `/api/blindboard/projects/:id` updates project
- [ ] DELETE `/api/blindboard/projects/:id` returns 204
- [ ] All endpoints return 401 without valid token
- [ ] DB migration runs successfully

## Edge Cases
- [ ] Empty canvas: layer panel shows no items
- [ ] Multiple elements: layer panel scrolls correctly
- [ ] Large SVG JSON: save/load handles large payloads
- [ ] Network error on save: shows error toast
- [ ] Preview with invalid ID: shows 404 page