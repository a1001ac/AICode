import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '@/pages/HomePage.vue'
import UserLoginPage from '@/pages/user/UserLoginPage.vue'
import UserRegisterPage from '@/pages/user/UserRegisterPage.vue'
import UserForgotPasswordPage from '@/pages/user/UserForgotPasswordPage.vue'
import UserResetPasswordPage from '@/pages/user/UserResetPasswordPage.vue'
import AppChatPage from '@/pages/app/AppChatPage.vue'
import AppEditPage from '@/pages/app/AppEditPage.vue'
import UserProfilePage from '@/pages/user/UserProfilePage.vue'
import { useLoginUserStore } from '@/stores/loginUser.ts'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: '主页',
      component: HomePage,
    },
    {
      path: '/user/login',
      name: '用户登录',
      component: UserLoginPage,
    },
    {
      path: '/user/register',
      name: '用户注册',
      component: UserRegisterPage,
    },
    {
      path: '/user/forgot_password',
      name: '找回密码',
      component: UserForgotPasswordPage,
    },
    {
      path: '/user/reset_password',
      name: '重置密码',
      component: UserResetPasswordPage,
    },
    {
      path: '/user/profile',
      name: '用户信息',
      component: UserProfilePage,
    },
    {
      path: '/app/chat/:id',
      name: '应用对话',
      component: AppChatPage,
    },
    {
      path: '/app/edit/:id',
      name: '编辑应用',
      component: AppEditPage,
    },
  ],
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

  next()
})
export default router
