<template>
  <div id="userLoginPage">
    <!-- 科技感网格背景 -->
    <div class="grid-background"></div>

    <div class="login-wrapper">
      <div class="login-card">
        <div class="login-header">
          <div class="logo">
            <div class="logo-icon">🤖</div>
          </div>
          <p class="slogan">AI驱动开发，构建生成应用</p>
        </div>

        <a-form :model="formState" name="loginForm" autocomplete="off" @finish="handleSubmit">
          <a-form-item name="userAccount" :rules="[{ required: true, message: '请输入账号' }]">
            <a-input
              v-model:value="formState.userAccount"
              placeholder="请输入账号"
              size="large"
              class="custom-input"
            >
              <template #prefix>
                <UserOutlined class="input-icon" />
              </template>
            </a-input>
          </a-form-item>

          <a-form-item
            name="userPassword"
            :rules="[
    { required: true, message: '请输入密码' },
    { min: 6, message: '密码长度不能小于 6 位' },
  ]"
            style="margin-bottom: 12px;"
          >
            <a-input-password
              v-model:value="formState.userPassword"
              placeholder="请输入密码"
              size="large"
              class="custom-input"
            >
              <template #prefix>
                <LockOutlined class="input-icon" />
              </template>
            </a-input-password>
          </a-form-item>

          <div class="quick-actions">
            <div class="remember-me">
              <a-checkbox v-model:checked="formState.rememberMe">
                <span class="action-text">记住我</span>
              </a-checkbox>
            </div>
            <RouterLink to="/user/forgot_password" class="forgot-password-link">忘记密码？</RouterLink>
          </div>

          <a-form-item style="margin-top: 24px;">
            <a-button type="primary" html-type="submit" size="large" block class="login-button">
              登录
            </a-button>
          </a-form-item>
        </a-form>

        <div class="register-footer">
          <span class="no-account">没有账号？</span>
          <RouterLink to="/user/register" class="register-link">立即注册</RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive } from 'vue'
import { userLogin } from '@/api/userController.ts'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'

const formState = reactive<API.UserLoginRequest>({
  userAccount: '',
  userPassword: '',
  rememberMe: false,
})

const router = useRouter()
const loginUserStore = useLoginUserStore()

const handleSubmit = async (values: any) => {
  const res = await userLogin({
    ...values,
    rememberMe: formState.rememberMe,
  })
  if (res.data.code === 0 && res.data.data) {
    await loginUserStore.fetchLoginUser()
    message.success('登录成功')
    router.push({
      path: '/',
      replace: true,
    })
  } else {
    message.error('登录失败，' + res.data.message)
  }
}
</script>

<style scoped>
#userLoginPage {
  min-height: 100vh;
  background: linear-gradient(135deg, #e3e7f0 0%, #d8dfea 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  position: relative;
  overflow: hidden;
}

/* 科技感网格背景 */
.grid-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image:
    linear-gradient(rgba(102, 126, 234, 0.04) 1px, transparent 1px),
    linear-gradient(90deg, rgba(102, 126, 234, 0.04) 1px, transparent 1px),
    linear-gradient(rgba(139, 92, 246, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(139, 92, 246, 0.03) 1px, transparent 1px);
  background-size:
    90px 90px,
    90px 90px,
    18px 18px,
    18px 18px;
  pointer-events: none;
  animation: gridFloat 20s ease-in-out infinite;
}

@keyframes gridFloat {
  0%,
  100% {
    transform: translate(0, 0);
    background-position:
      0 0,
      0 0,
      0 0,
      0 0;
  }
  25% {
    transform: translate(1.5px, -1.5px);
    background-position:
      4px 0,
      0 4px,
      1.5px 0,
      0 1.5px;
  }
  50% {
    transform: translate(-1.5px, 1.5px);
    background-position:
      0 4px,
      4px 0,
      0 1.5px,
      1.5px 0;
  }
  75% {
    transform: translate(1.5px, 1.5px);
    background-position:
      4px 4px,
      4px 4px,
      1.5px 1.5px,
      1.5px 1.5px;
  }
}

.login-wrapper {
  width: 100%;
  max-width: 420px;
  position: relative;
  z-index: 1;
  /* 往上移动 50px */
  transform: translateY(-50px);
}

.login-card {
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(8px);
  border-radius: 18px;
  padding: 40px 35px;
  box-shadow:
    0 12px 30px rgba(0, 0, 0, 0.08),
    0 2px 8px rgba(0, 0, 0, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.3);
  position: relative;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 15px;
}

.logo-icon {
  font-size: 42px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.slogan {
  color: #777;
  font-size: 14px;
  margin: 0;
  font-weight: 400;
}

:deep(.custom-input) {
  border-radius: 11px;
  border: 1px solid #e8ecef;
  background: rgba(255, 255, 255, 0.85);
  transition: all 0.3s ease;
}

:deep(.custom-input:hover) {
  border-color: #1890ff;
  box-shadow: 0 3px 12px rgba(102, 126, 234, 0.12);
}

:deep(.custom-input:focus) {
  border-color: #1890ff;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.15);
}

:deep(.input-icon) {
  color: #888;
  font-size: 16px;
}

:deep(.login-button) {
  border-radius: 11px;
  height: 47px;
  font-size: 16px;
  font-weight: 500;
  background: #1890ff;
  border: none;
  transition: all 0.3s ease;
}

:deep(.login-button:hover) {
  transform: translateY(-1.5px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.25);
}

:deep(.login-button:active) {
  transform: translateY(0);
}

.register-footer {
  text-align: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.no-account {
  color: #777;
  font-size: 14px;
}

.register-link {
  color: #1890ff;
  text-decoration: none;
  font-weight: 500;
  margin-left: 5px;
  transition: all 0.3s ease;
}

.register-link:hover {
  color: #1890ff;
  text-decoration: underline;
}

.no-account {
  color: #777;
  font-size: 14px;
}

.quick-actions {
  display: flex;              /* 启用 Flex 布局 */
  justify-content: space-between; /* 两端对齐：左边一个，右边一个 */
  align-items: center;        /* 垂直居中对齐 */
  margin-top: 10px;          /* 可选：与上方密码框增加一点间距 */
}

.register-link {
  color: #1890ff;
  text-decoration: none;
  font-weight: 500;
  margin-left: 5px;
  transition: all 0.3s ease;
}

.register-link:hover {
  color: #1890ff;
  text-decoration: underline;
}

.forgot-password-link {
  color: #777;
  font-size: 14px;
  text-decoration: none;
  transition: all 0.3s ease;
}

.forgot-password-link:hover {
  color: #1890ff;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-card {
    padding: 30px 20px;
    margin: 10px;
  }

  .slogan {
    font-size: 13px;
  }

  /* 移动端减少上移距离 */
  .login-wrapper {
    transform: translateY(-20px);
  }
}
</style>
