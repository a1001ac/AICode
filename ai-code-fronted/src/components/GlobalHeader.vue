<template>
  <a-layout-header class="header" style="height: 60px">
    <a-row :wrap="false" align="middle">
      <!-- 左侧：Logo和标题 -->
      <a-col flex="200px">
        <div class="header-left">
          <!-- 将按钮移出 RouterLink，避免点击按钮触发跳转到主页 -->
          <a-button class="mobile-menu-button" type="text" @click.stop="toggleMobileSidebar">
            <MenuOutlined />
          </a-button>

          <RouterLink to="/" class="logo-link">
            <div class="logo-container">
              <img class="logo" src="@/assets/logo.png" alt="Logo" />
            </div>
            <h1 class="site-title">零代码应用生成</h1>
          </RouterLink>
        </div>
      </a-col>

      <!-- 中间：导航菜单 -->
      <a-col flex="auto">
        <a-menu
          v-model:selectedKeys="selectedKeys"
          mode="horizontal"
          :items="originItems"
          @click="handleMenuClick"
          class="nav-menu"
        />
      </a-col>

      <!-- 右侧：用户操作区域 -->
      <a-col>
        <div class="user-login-status">
          <div v-if="loginUserStore.loginUser.id" class="user-dropdown">
            <a-dropdown placement="bottomRight">
              <a-space class="user-info">
                <a-avatar :src="loginUserStore.loginUser.userAvatar" size="small" />
                <span class="user-name">{{ loginUserStore.loginUser.userAccount ?? '无名' }}</span>
              </a-space>
              <template #overlay>
                <a-menu @click="handleUserMenuClick">
                  <a-menu-item key="profile">
                    <UserOutlined />
                    个人信息
                  </a-menu-item>
                  <a-menu-item key="logout">
                    <LogoutOutlined />
                    退出登录
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </div>
          <div v-else>
            <a-button type="primary" href="/user/login" size="small" class="login-button">
              登录
            </a-button>
          </div>
        </div>
      </a-col>
    </a-row>

    <!-- 移动端侧边栏 Drawer -->
    <a-drawer class="mobile-drawer" v-model:open="showMobileSidebar" placement="left" :width="280" :closable="false" :bodyStyle="{ padding: 0 }">
      <a-menu
        mode="inline"
        :items="originItems"
        v-model:selectedKeys="selectedKeys"
        @click="onMobileMenuClick"
        style="height:100%; border-right:0"
      />
    </a-drawer>
  </a-layout-header>
</template>

<script setup lang="ts">
import { h, ref } from 'vue'
import { useRouter } from 'vue-router'
import { type MenuProps, message, Modal } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import { userLogout } from '@/api/userController.ts'
import { LogoutOutlined, HomeOutlined, UserOutlined, MenuOutlined } from '@ant-design/icons-vue'

const loginUserStore = useLoginUserStore()
const router = useRouter()
// 当前选中菜单
const selectedKeys = ref<string[]>(['/'])
// 监听路由变化，更新当前选中菜单
router.afterEach((to, from, next) => {
  selectedKeys.value = [to.path]
})

// 新增：控制移动端侧边栏显示
const showMobileSidebar = ref(false)
const toggleMobileSidebar = () => {
  showMobileSidebar.value = !showMobileSidebar.value
}
const closeMobileSidebar = () => {
  showMobileSidebar.value = false
}

// 菜单配置项
const originItems = [
  {
    key: '/',
    icon: () => h(HomeOutlined),
    label: '主页',
    title: '主页',
  },
  {
    key: '接口文档',
    label: h(
      'a',
      { href: 'http://101.126.151.232/api/doc.html', target: '_blank' },
      '接口文档',
    ),
    title: '接口文档',
  },
  {
    key: '后台管理',
    label: h(
      'a',
      { href: 'http://101.126.151.232:8888', target: '_blank' },
      '后台管理',
    ),
    title: '后台管理',
  },
]

// 处理菜单点击
const handleMenuClick: MenuProps['onClick'] = (e) => {
  const key = e.key as string
  selectedKeys.value = [key]
  // 跳转到对应页面
  if (key.startsWith('/')) {
    router.push(key)
  }
}

// 新增：移动端菜单点击处理（导航并关闭侧边栏）
const onMobileMenuClick: MenuProps['onClick'] = (e) => {
  handleMenuClick(e)
  closeMobileSidebar()
}

// 处理用户下拉菜单点击
const handleUserMenuClick = ({ key }: { key: string }) => {
  if (key === 'logout') {
    Modal.confirm({
      title: '确定要退出登录吗?',
      okText: '确定',
      cancelText: '取消',
      onOk: async () => {
        await doLogout()
      },
    })
  } else if (key === 'profile') {
    router.push('/user/profile')
  }
}

// 退出登录
const doLogout = async () => {
  const res = await userLogout()
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({
      userName: '未登录',
    })
    message.success('退出登录成功')
    await router.push('/user/login')
  } else {
    message.error('退出登录失败，' + res.data.message)
  }
}
</script>

<style scoped>
.user-info:hover {
  background: #f5f5f5;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.header {
  background: #ffffff;
  padding: 0 24px;
  height: 50px;
  line-height: 56px;
  z-index: 19;
  width: 100%;
  -webkit-backdrop-filter: blur(8px);
  backdrop-filter: blur(8px);
  transition: background-color 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 30px;
}

.logo-container {
  padding-left: 12px;
}

.logo {
  display: flex;
  height: 30px;
  width: 30px;
  border-radius: 6px;
  margin: 0 auto;
}

.logo-link {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
}

.site-title {
  margin: 0;
  font-size: 19px;
  font-weight: 700;
  color: #000000;
}

.ant-menu-horizontal {
  border-bottom: none !important;
}

.nav-menu {
  background: transparent !important;
  border-bottom: none !important;
}

.nav-menu :deep(.ant-menu-item) {
  font-weight: 700;
  font-size: 15px;
  color: #000000;
  transition: all 0.3s ease;
  border-radius: 6px;
  margin: 0 2px;
}

/* 新增：移动端只显示头像（隐藏用户名）并收紧间距，以及显示侧边栏触发按钮并隐藏顶部菜单 */
.mobile-menu-button {
  display: none;
  align-items: center;
  justify-content: center;
  padding: 6px;
  margin-right: 6px;
}

@media (max-width: 768px) {
  .user-name {
    display: none;
  }
  .user-info {
    gap: 0; /* 去掉用户名消失后留下的间隙 */
    padding: 4px; /* 可选：让头像的点击区域在移动端略微增大 */
  }

  /* 隐藏顶部横向菜单，使用侧边栏替代 */
  .nav-menu {
    display: none;
  }

  /* 在移动端显示菜单按钮并稍微收紧 header-left 的间距 */
  .mobile-menu-button {
    display: flex;
  }
  .header-left {
    gap: 10px;
  }
  /* 若头像尺寸需要在手机端微调，可启用下列规则：*/
  .user-info :deep(.ant-avatar) {
    width: 38px;
    height: 38px;
  }
  .site-title {
    display: none !important;
  }

  /* 移动端侧边栏字体黑色加粗（包含链接和已选中态） */
  .mobile-drawer :deep(.ant-menu-item),
  .mobile-drawer :deep(.ant-menu-item a) {
    color: #000 !important;
    font-weight: 700 !important;
  }
  .mobile-drawer :deep(.ant-menu-item-selected),
  .mobile-drawer :deep(.ant-menu-item-selected a) {
    color: #000 !important;
    font-weight: 700 !important;
  }
}
</style>
