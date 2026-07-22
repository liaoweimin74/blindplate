import { ref, type Ref } from 'vue'

export interface LayerItem {
  id: string
  name: string
  visible: boolean
  locked: boolean
}

export function useBoardLayers(mtEditRef: Ref<any>) {
  const layers = ref<LayerItem[]>([])

  function initFromCanvas() {
    const edit = mtEditRef.value
    if (!edit) return

    const elements = edit.getJson?.() || []
    layers.value = elements.map((el: any) => ({
      id: el.id,
      name: el.title || el.name || el.id,
      visible: true,
      locked: false
    }))
  }

  function toggleVisibility(id: string) {
    const layer = layers.value.find(l => l.id === id)
    if (!layer) return
    layer.visible = !layer.visible

    const edit = mtEditRef.value
    if (edit?.toggleVisible) {
      edit.toggleVisible(id, layer.visible)
    }
  }

  function toggleLock(id: string) {
    const layer = layers.value.find(l => l.id === id)
    if (!layer) return
    layer.locked = !layer.locked

    const edit = mtEditRef.value
    if (edit?.toggleLock) {
      edit.toggleLock(id, layer.locked)
    }
  }

  function selectElement(id: string) {
    const edit = mtEditRef.value
    if (edit?.selectElement) {
      edit.selectElement(id)
    }
  }

  function setLayers(newLayers: LayerItem[]) {
    layers.value = newLayers
  }

  return {
    layers,
    initFromCanvas,
    toggleVisibility,
    toggleLock,
    selectElement,
    setLayers
  }
}