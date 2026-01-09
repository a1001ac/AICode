<template>
  <a-menu v-model:selectedKeys="selectedKeys" mode="inline" @click="handleMenuClick">
    <a-menu-item key="/admin/dashBoard">
      <template #icon><DashboardOutlined /></template>
      <span>仪表盘</span>
    </a-menu-item>
    <a-menu-item key="/admin/userManage">
      <template #icon><UserOutlined /></template>
      <span>用户管理</span>
    </a-menu-item>
    <a-menu-item key="/admin/appManage">
      <template #icon><AppstoreOutlined /></template>
      <span>应用管理</span>
    </a-menu-item>
    <a-menu-item key="/admin/chatManage">
      <template #icon><MessageOutlined /></template>
      <span>对话管理</span>
    </a-menu-item>
  </a-menu>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { UserOutlined, AppstoreOutlined, MessageOutlined, DashboardOutlined} from '@ant-design/icons-vue';

const router = useRouter();
const route = useRoute();
const selectedKeys = ref<string[]>([route.path]);

// 监听路由变化，更新选中菜单
watch(() => route.path, (newPath) => {
  selectedKeys.value = [newPath];
});

const handleMenuClick = ({ key }: { key: string }) => {
  router.push(key);
};
</script>
