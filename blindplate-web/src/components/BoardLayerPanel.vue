<script setup lang="ts">
import type { LayerItem } from '@/composables/useBoardLayers'
import { View, Lock, Unlock } from '@element-plus/icons-vue'

defineProps<{
  layers: LayerItem[]
  modelValue: boolean
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'toggle-visibility': [id: string]
  'toggle-lock': [id: string]
  'select': [id: string]
}>()
</script>

<template>
  <el-drawer
    :model-value="modelValue"
    @update:model-value="emit('update:modelValue', $event)"
    direction="rtl"
    size="280px"
    title="图层管理"
  >
    <div class="layer-list">
      <div
        v-for="layer in layers"
        :key="layer.id"
        class="layer-item"
        @click="emit('select', layer.id)"
      >
        <el-button
          text
          :icon="layer.visible ? View : View"
          :type="layer.visible ? 'primary' : 'info'"
          @click.stop="emit('toggle-visibility', layer.id)"
          class="layer-btn"
        />
        <el-button
          text
          :icon="layer.locked ? Lock : Unlock"
          :type="layer.locked ? 'warning' : 'default'"
          @click.stop="emit('toggle-lock', layer.id)"
          class="layer-btn"
        />
        <span class="layer-name" :class="{ 'layer-hidden': !layer.visible }">
          {{ layer.name }}
        </span>
      </div>
      <el-empty v-if="layers.length === 0" description="暂无图层" />
    </div>
  </el-drawer>
</template>

<style scoped>
.layer-list {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.layer-item {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 8px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.layer-item:hover {
  background-color: var(--el-fill-color-light);
}

.layer-btn {
  padding: 4px;
  min-width: auto;
}

.layer-name {
  font-size: 13px;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.layer-hidden {
  opacity: 0.5;
  text-decoration: line-through;
}
</style>