<template>
  <div class="tags-view">
    <a-tag
      v-for="tab in tabs"
      :key="tab.path"
      :closable="tabs.length > 1"
      :color="tab.path === route.path ? 'blue' : 'default'"
      @close="handleClose(tab.path)"
      @click="go(tab.path)"
      class="tag-item"
    >
      {{ tab.title }}
    </a-tag>
  </div>
</template>

<script setup lang="ts">
import { computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useTabsViewStore } from '@/stores/tabsView'

const route = useRoute()
const router = useRouter()
const tabsStore = useTabsViewStore()

const tabs = computed(() => tabsStore.tabs)

// 路由变化时，自动添加标签
watch(
  () => route.fullPath,
  () => {
    tabsStore.addTab(route)
  },
  { immediate: true }
)

// 关闭标签
const handleClose = (path: string) => {
  const isActive = path === route.path
  tabsStore.removeTab(path)

  // 如果关闭的是当前页，跳转到最后一个标签
  if (isActive && tabs.value.length) {
    router.push(tabs.value[tabs.value.length - 1].path)
  }
}

// 点击标签切换
const go = (path: string) => {
  if (path !== route.path) {
    router.push(path)
  }
}
</script>

<style scoped>
.tags-view {
  padding: 8px 16px;
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.tag-item {
  cursor: pointer;
  user-select: none;
}
</style>
