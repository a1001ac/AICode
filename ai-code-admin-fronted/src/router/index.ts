import { createRouter, createWebHistory } from 'vue-router'
import type {RouteRecordRaw} from 'vue-router'
import MainLayout from '@/layouts/MainLayout.vue'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import { message } from 'ant-design-vue'

// 定义路由
const routes: RouteRecordRaw[] = [
  {
    path: '/user/login',
    name: '用户登录',
    component: () => import('@/pages/user/UserLoginPage.vue'),
    meta: {
      hidden: true,
      isLoginPage: true,
    },
  },
  {
    path: '/forgot_password',
    name: '找回密码',
    component: () => import('@/pages/user/UserForgotPasswordPage.vue'),
  },
  {
    path: '/reset_password',
    name: '重置密码',
    component: () => import('@/pages/user/UserResetPasswordPage.vue'),
  },
  {
    path: '/admin',
    component: MainLayout,
    redirect: '/admin/dashBoard',
    meta: {
      requiresAuth: true,
      roles: ['admin'],
    },
    children: [
      {
        path: 'dashBoard',
        name: '仪表盘',
        component: () => import('@/pages/dash/DashBoard.vue'),
      },
      {
        path: 'userProfile',
        name: '个人中心',
        component: () => import('@/pages/user/UserProfilePage.vue'),
      },
      {
        path: 'userManage',
        name: '用户管理',
        component: () => import('@/pages/user/UserManagePage.vue'),
      },
      {
        path: 'appManage',
        name: '应用管理',
        component: () => import('@/pages/app/AppManagePage.vue'),
      },
      {
        path: '/admin/chatManage',
        name: '对话管理',
        component: () => import('@/pages/chat/ChatManagePage.vue'),
      },
      {
        path: '/app/chat/:id',
        name: '应用对话',
        component: () => import('@/pages/app/AppChatPage.vue'),
      },
      {
        path: '/app/edit/:id',
        name: '编辑应用',
        component: () => import('@/pages/app/AppEditPage.vue'),
      },
      {
        path: '/admin/configManage',
        name: '配置管理',
        component: () => import('@/pages/config/ConfigManagePage.vue'),
      },
    ],
  },
  // 3️⃣ 根路径重定向到登录页
  {
    path: '/',
    redirect: '/user/login',
  },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

router.beforeEach(async (to, from, next) => {
  const loginUserStore = useLoginUserStore()

  // 尝试获取登录用户
  if (
    !loginUserStore.loginUser ||
    loginUserStore.loginUser.userAccount === '未登录'
  ) {
    try {
      await loginUserStore.fetchLoginUser()
    } catch (e) {
      // 未登录
    }
  }

  const loginUser = loginUserStore.loginUser
  const isLogin = loginUser?.userAccount !== '未登录'

  // 已登录用户访问 登录页 或 /
  if (isLogin && (to.path === '/' || to.meta.isLoginPage)) {
    return next('/admin/dashBoard')
  }

  // 需要登录但未登录
  if (to.meta.requiresAuth && !isLogin) {
    return next({
      path: '/user/login',
      query: { redirect: to.fullPath },
    })
  }

  // 需要角色权限
  if (to.meta.roles) {
    const roles = to.meta.roles as string[]
    if (!roles.includes(<string>loginUser.userRole)) {
      message.error('无权限访问该页面')
      return next('/user/login')
      // 或者 next('/') / next('/403')
    }
  }

  next()
})

export default router
