<template>
  <contextHolder />
  <a-layout class="main-layout">
    <a-layout-sider
      v-model:collapsed="collapsed"
      :trigger="null"
      collapsible
      theme="light"
      class="sider-container"
      :width="190"
    >
      <div class="logo">
        <img src="@/assets/logo.png" alt="logo" />
        <span v-if="!collapsed">后台管理系统</span>
      </div>
      <SideMenu />
    </a-layout-sider>

    <a-layout>
      <a-layout-header class="header-container">
        <GlobalHeader v-model:collapsed="collapsed" />
      </a-layout-header>

      <TagsView />

      <a-layout-content class="content-container">
        <router-view />
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { notification } from 'ant-design-vue'
import SideMenu from '@/components/SideMenu.vue'
import GlobalHeader from '@/components/GlobalHeader.vue'
import TagsView from '@/components/TagsView.vue'

const collapsed = ref(false)

// 使用官方推荐 API
const [api, contextHolder] = notification.useNotification()

onMounted(() => {
  // 防止刷新/切路由重复弹
  if (sessionStorage.getItem('WELCOME_SHOWN')) return

  api.success({
    message: `登录成功！`,
    description: `欢迎回来，零代码应用生成平台后台管理系统！`,
    placement: 'topRight',
    duration: 4.5,
  })

  sessionStorage.setItem('WELCOME_SHOWN', '1')
})
</script>

<style lang="less" scoped>
.main-layout {
  min-height: 100vh;
  .logo {
    height: 64px;
    padding: 16px;
    display: flex;
    align-items: center;
    gap: 12px;
    font-size: 18px;
    font-weight: bold;
    overflow: hidden;
    white-space: nowrap;
    img { height: 32px; }
  }
  .header-container {
    background: #fff;
    padding: 0 24px;
    border-bottom: 1px solid #f0f0f0;
  }
}
</style>
