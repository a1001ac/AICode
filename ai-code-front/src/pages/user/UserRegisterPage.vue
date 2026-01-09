<template>
  <div id="userRegisterPage">
    <!-- 科技感网格背景 -->
    <div class="grid-background"></div>

    <div class="login-wrapper">
      <div class="login-card">
        <div class="login-header">
          <div class="logo">
            <div class="logo-icon">🤖</div>
            <h1 class="logo-text">Code Flow</h1>
          </div>
          <p class="slogan">简化代码开发，完整输出应用</p>
        </div>

        <a-form :model="formState" name="registerForm" autocomplete="off" @finish="handleSubmit">
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
              { min: 6, message: '密码不能小于 6 位' },
            ]"
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

          <a-form-item
            name="checkPassword"
            :rules="[
              { required: true, message: '请确认密码' },
              { min: 6, message: '密码不能小于 6 位' },
              { validator: validateCheckPassword },
            ]"
          >
            <a-input-password
              v-model:value="formState.checkPassword"
              placeholder="请确认密码"
              size="large"
              class="custom-input"
            >
              <template #prefix>
                <LockOutlined class="input-icon" />
              </template>
            </a-input-password>
          </a-form-item>

          <a-form-item>
            <a-button type="primary" html-type="submit" size="large" block class="login-button">
              注册
            </a-button>
          </a-form-item>
        </a-form>

        <div class="login-footer">
          <span class="no-account">已有账号？</span>
          <RouterLink to="/user/login" class="register-link">立即登录</RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { userRegister } from '@/api/userController.ts'
import { message } from 'ant-design-vue'
import { reactive } from 'vue'
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'

const router = useRouter()

const formState = reactive<API.UserRegisterRequest>({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
})

/**
 * 验证确认密码
 * @param rule
 * @param value
 * @param callback
 */
const validateCheckPassword = (rule: unknown, value: string, callback: (error?: Error) => void) => {
  if (value && value !== formState.userPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

/**
 * 提交表单
 * @param values
 */
const handleSubmit = async (values: API.UserRegisterRequest) => {
  const res = await userRegister(values)
  // 注册成功，跳转到登录页面
  if (res.data.code === 0) {
    message.success('注册成功')
    router.push({
      path: '/user/login',
      replace: true,
    })
  } else {
    message.error('注册失败，' + res.data.message)
  }
}
</script>

<style scoped>
#userRegisterPage {
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
  font-size: 32px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.logo-text {
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, #444 0%, #666 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0;
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

.login-footer {
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

/* 响应式设计 */
@media (max-width: 480px) {
  .login-card {
    padding: 30px 20px;
    margin: 10px;
  }

  .logo-text {
    font-size: 24px;
  }

  .slogan {
    font-size: 13px;
  }

  .login-wrapper {
    transform: translateY(-20px);
  }
}
</style>
