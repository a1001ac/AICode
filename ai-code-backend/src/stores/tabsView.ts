import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { RouteLocationNormalized } from 'vue-router'

export interface TabItem {
  title: string
  path: string
  name?: string | symbol
}

export const useTabsViewStore = defineStore('tabsView', () => {
  const tabs = ref<TabItem[]>([])

  // 添加标签
  const addTab = (route: RouteLocationNormalized) => {
    if (!route.name) return

    const exists = tabs.value.find(tab => tab.path === route.path)
    if (exists) return

    tabs.value.push({
      title: route.name as string,
      path: route.path,
      name: route.name,
    })
  }

  // 删除标签
  const removeTab = (path: string) => {
    const index = tabs.value.findIndex(tab => tab.path === path)
    if (index !== -1) {
      tabs.value.splice(index, 1)
    }
  }

  return {
    tabs,
    addTab,
    removeTab,
  }
})
