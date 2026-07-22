<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { MtPreview } from 'maotu'
import { useBoardStore } from '@/stores/board'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const boardStore = useBoardStore()

const projectJson = ref<object | null>(null)
const loading = ref(true)

onMounted(async () => {
  const id = Number(route.params.id)
  if (!id) {
    ElMessage.error('无效的项目ID')
    router.push('/blindplate-editor')
    return
  }

  try {
    const project = await boardStore.fetchProject(id)
    if (project?.svgJson) {
      projectJson.value = typeof project.svgJson === 'string'
        ? JSON.parse(project.svgJson)
        : project.svgJson
    }
  } catch {
    ElMessage.error('加载项目失败')
  } finally {
    loading.value = false
  }
})

function goBack() {
  router.push('/blindplate-editor')
}
</script>

<template>
  <div class="preview-container">
    <div class="preview-header">
      <el-button @click="goBack">返回编辑</el-button>
      <span v-if="boardStore.currentProject" class="preview-title">
        {{ boardStore.currentProject.name }}
      </span>
    </div>
    <div class="preview-body" v-loading="loading">
      <MtPreview v-if="projectJson" :json="projectJson" />
      <el-empty v-else-if="!loading" description="暂无数据" />
    </div>
  </div>
</template>

<style scoped>
.preview-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

.preview-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 16px;
  background-color: var(--brand-bg-white);
  border-bottom: 1px solid var(--brand-border-light);
  flex-shrink: 0;
}

.preview-title {
  font-size: 16px;
  font-weight: 600;
}

.preview-body {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 0;
}
</style>