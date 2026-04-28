<template>
  <div class="header-content">
    <!-- 左侧 -->
    <div class="left">
      <menu-unfold-outlined v-if="collapsed" class="trigger" @click="toggle" />
      <menu-fold-outlined v-else class="trigger" @click="toggle" />

      <a-breadcrumb class="breadcrumb">
        <a-breadcrumb-item>
          <router-link to="/">
            <home-outlined />
          </router-link>
        </a-breadcrumb-item>
        <a-breadcrumb-item v-for="item in routes" :key="item.path">
          {{ item.name }}
        </a-breadcrumb-item>
      </a-breadcrumb>
    </div>

    <!-- 右侧 -->
    <div class="right">
      <a-space size="large">
        <!-- 已登录 -->
        <a-dropdown
          v-if="loginUserStore.loginUser.id"
          placement="bottomRight"
        >
          <a-space class="user-info">
            <a-avatar
              :src="loginUserStore.loginUser.userAvatar"
              size="small"
            />
            <span class="user-name">
              {{ loginUserStore.loginUser.userAccount ?? '无名' }}
            </span>
          </a-space>

          <template #overlay>
            <a-menu @click="handleUserMenuClick">
              <a-menu-item key="profile">
                <UserOutlined />
                个人中心
              </a-menu-item>
              <a-menu-item key="logout">
                <LogoutOutlined />
                退出登录
              </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
      </a-space>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/loginUser'
import { message, Modal } from 'ant-design-vue'
import {
  MenuUnfoldOutlined,
  MenuFoldOutlined,
  HomeOutlined,
  UserOutlined,
  LogoutOutlined,
} from '@ant-design/icons-vue'
import { userLogout } from '@/api/userController.ts'

const props = defineProps<{ collapsed: boolean }>()
const emit = defineEmits(['update:collapsed'])

const toggle = () => emit('update:collapsed', !props.collapsed)

const route = useRoute()
const router = useRouter()
const loginUserStore = useLoginUserStore()

// 面包屑
const routes = computed(() => {
  return route.matched.filter(item => item.name)
})

// 下拉菜单点击
const handleUserMenuClick = ({ key }: { key: string }) => {
  if (key === 'profile') {
    router.push('/admin/userProfile')
  }

  if (key === 'logout') {
    Modal.confirm({
      title: '确定要退出登录吗?',
      okText: '确定',
      cancelText: '取消',
      onOk: async () => {
        await doLogout()
      },
    })
  }
}
// 退出登录
const doLogout = async () => {
  await userLogout()
  // 先清空登录状态
  loginUserStore.clearLoginUser()
  message.success('退出登录成功')
  // 再跳转
  await router.replace('/user/login')
  sessionStorage.removeItem('WELCOME_SHOWN')
}
</script>

<style scoped>
.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.left {
  display: flex;
  align-items: center;
  gap: 18px;
}

.trigger {
  font-size: 18px;
  cursor: pointer;
  transition: color 0.3s;
}

.right {
  display: flex;
  align-items: center;
}

.user-info {
  cursor: pointer;
}

.user-name {
  font-size: 14px;
  color: #333;
}

@media (max-width: 768px) {
  .user-name {
    display: none;
  }
}
</style>
