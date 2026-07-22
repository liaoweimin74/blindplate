<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { MtEdit } from 'maotu'
import BoardToolbar from '@/components/BoardToolbar.vue'
import BoardLayerPanel from '@/components/BoardLayerPanel.vue'
import { useBoardLayers } from '@/composables/useBoardLayers'
import { useBoardStore } from '@/stores/board'
import { ElMessage, ElInput, ElDialog, ElButton } from 'element-plus'

const router = useRouter()
const boardStore = useBoardStore()

const mtEditRef = ref<InstanceType<typeof MtEdit> | null>(null)
const layerVisible = ref(false)
const showSaveDialog = ref(false)
const projectName = ref('')

const { layers, initFromCanvas, toggleVisibility, toggleLock, selectElement } = useBoardLayers(mtEditRef)

function handleSave() {
  if (!projectName.value.trim()) {
    projectName.value = `盲板组态_${Date.now()}`
  }
  showSaveDialog.value = true
}

async function confirmSave() {
  try {
    const edit = mtEditRef.value as any
    const json = edit?.getJson?.() || {}
    await boardStore.saveProject(projectName.value, json)
    ElMessage.success('保存成功')
    showSaveDialog.value = false
  } catch {
    ElMessage.error('保存失败')
  }
}

function handlePreview() {
  if (boardStore.currentProject) {
    router.push(`/blindplate-preview/${boardStore.currentProject.id}`)
  } else {
    ElMessage.warning('请先保存项目')
  }
}

function handleLayerToggle() {
  layerVisible.value = !layerVisible.value
}

onMounted(() => {
  setTimeout(() => {
    initFromCanvas()
  }, 500)
})
</script>

<template>
  <div class="blindboard-editor">
    <BoardToolbar
      :on-save="handleSave"
      :on-preview="handlePreview"
      :on-layer-toggle="handleLayerToggle"
    />
    <div class="editor-body">
      <div class="editor-canvas">
        <MtEdit ref="mtEditRef" />
      </div>
    </div>
    <BoardLayerPanel
      v-model="layerVisible"
      :layers="layers"
      @toggle-visibility="toggleVisibility"
      @toggle-lock="toggleLock"
      @select="selectElement"
    />

    <el-dialog v-model="showSaveDialog" title="保存项目" width="400px">
      <el-form>
        <el-form-item label="项目名称">
          <el-input v-model="projectName" placeholder="请输入项目名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showSaveDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.blindboard-editor {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

.editor-body {
  flex: 1;
  display: flex;
  min-height: 0;
}

.editor-canvas {
  flex: 1;
  position: relative;
  overflow: hidden;
}
</style>