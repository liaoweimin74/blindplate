<script setup lang="ts">
import { Check, View, Operation } from '@element-plus/icons-vue'

defineProps<{
  onSave: () => void
  onPreview: () => void
  onLayerToggle: () => void
}>()
</script>

<template>
  <div class="board-toolbar">
    <div class="toolbar-left">
      <el-button type="primary" @click="onSave">
        <el-icon><Check /></el-icon>
        保存
      </el-button>
      <el-button @click="onPreview">
        <el-icon><View /></el-icon>
        预览
      </el-button>
    </div>
    <div class="toolbar-right">
      <el-button @click="onLayerToggle">
        <el-icon><Operation /></el-icon>
        图层
      </el-button>
    </div>
  </div>
</template>

<style scoped>
.board-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 16px;
  background-color: var(--brand-bg-white);
  border-bottom: 1px solid var(--brand-border-light);
  flex-shrink: 0;
}

.toolbar-left,
.toolbar-right {
  display: flex;
  gap: 8px;
  align-items: center;
}
</style>