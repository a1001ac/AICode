<template>
  <div id="userLoginPage">
    <div class="login-layout">
      <!-- 左侧展示区 -->
      <div class="login-left">
        <div class="brand">
          <div class="logo">🤖</div>
          <h1>零代码应用生成平台后台管理系统</h1>
        </div>

        <div class="illustration">
          <img
            src="@/assets/bg.png"
            alt="login"
          />
        </div>
      </div>

      <!-- 右侧登录区 -->
      <div class="login-right">
        <div class="login-box">
          <h2 class="title">欢迎回来</h2>
          <p class="subtitle">输入您的账号和密码登录</p>

          <a-form
            :model="formState"
            name="loginForm"
            autocomplete="off"
            @finish="handleSubmit"
          >
            <a-form-item
              name="userAccount"
              :rules="[{ required: true, message: '请输入账号' }]"
            >
              <a-input
                v-model:value="formState.userAccount"
                size="large"
                placeholder="账号"
              >
                <template #prefix>
                  <UserOutlined />
                </template>
              </a-input>
            </a-form-item>

            <a-form-item
              name="userPassword"
              :rules="[{ required: true, message: '请输入密码' }]"
            >
              <a-input-password
                v-model:value="formState.userPassword"
                size="large"
                placeholder="密码"
              >
                <template #prefix>
                  <LockOutlined />
                </template>
              </a-input-password>
            </a-form-item>

            <div class="login-options">
              <a-checkbox v-model:checked="formState.rememberMe">
                记住我
              </a-checkbox>
              <RouterLink to="/forgot_password">忘记密码？</RouterLink>
            </div>

            <a-button
              type="primary"
              html-type="submit"
              size="large"
              block
              class="login-btn"
            >
              登录
            </a-button>
          </a-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive } from 'vue'
import { userLogin } from '@/api/userController.ts'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'

const formState = reactive<API.UserLoginRequest>({
  userAccount: '',
  userPassword: '',
  rememberMe: false,
})

const router = useRouter()
const route = useRoute()
const loginUserStore = useLoginUserStore()

const handleSubmit = async (values: any) => {
  const res = await userLogin({
    ...values,
    rememberMe: formState.rememberMe,
  })
  if (res.data.code === 0 && res.data.data) {
    await loginUserStore.fetchLoginUser()
    const loginUser = loginUserStore.loginUser
    if (loginUser.userRole !== 'admin') {
      message.error('无管理员权限，无法进入后台')
      return
    }

    // 判断是否有重定向参数
    const redirect = route.query.redirect as string
    if (redirect) {
      await router.push(redirect)
    } else {
      await router.push('/admin/dashBoard')
    }
  } else {
    message.error('登录失败，' + res.data.message)
  }
}
</script>

<style scoped>
#userLoginPage {
  height: 100vh;
  background: #f5f7fb;
}

.login-layout {
  display: flex;
  height: 100%;
}

/* 左侧 */
.login-left {
  flex: 3;
  background: linear-gradient(135deg, #4f8cff, #269ff5);
  color: #fff;
  padding: 60px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.brand h1 {
  font-size: 28px;
  margin: 12px 0;
}

.brand p {
  opacity: 0.85;
}

.logo {
  font-size: 42px;
}

.illustration {
  text-align: center;
}

.illustration img {
  width: 80%;
  max-width: 420px;
}

/* 右侧 */
.login-right {
  flex: 2;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-box {
  width: 360px;
}

.title {
  font-size: 26px;
  font-weight: 600;
}

.subtitle {
  color: #999;
  margin-bottom: 30px;
}

.login-options {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.login-btn {
  height: 44px;
  font-size: 16px;
}

/* 响应式 */
@media (max-width: 768px) {
  .login-left {
    display: none;
  }

  .login-right {
    flex: 1;
  }
}
</style>
