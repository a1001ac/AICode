<template>
  <div id="userForgotPasswordPage">
    <!-- 科技感网格背景 -->
    <div class="grid-background"></div>

    <div class="login-wrapper">
      <div class="login-card">
        <div class="login-header">
          <div class="logo">
            <div class="logo-icon">🔐</div>
            <h1 class="logo-text">找回密码</h1>
          </div>
          <p class="slogan">请输入注册邮箱以获取验证码</p>
        </div>

        <a-form :model="formState" name="forgotPasswordForm" autocomplete="off" @finish="handleSubmit">
          <a-form-item
            name="email"
            :rules="[
              { required: true, message: '请输入邮箱' },
              { type: 'email', message: '请输入有效的邮箱地址' }
            ]"
          >
            <a-input
              v-model:value="formState.email"
              placeholder="请输入邮箱"
              size="large"
              class="custom-input"
            >
              <template #prefix>
                <MailOutlined class="input-icon" />
              </template>
            </a-input>
          </a-form-item>

          <a-form-item>
            <a-button type="primary" html-type="submit" size="large" block class="login-button" :loading="loading">
              获取验证码
            </a-button>
          </a-form-item>
        </a-form>

        <div class="login-footer">
          <RouterLink to="/user/login" class="back-link">返回登录</RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { sendResetPasswordCode } from '@/api/userController.ts'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { MailOutlined } from '@ant-design/icons-vue'

const formState = reactive({
  email: '',
})

const loading = ref(false)
const router = useRouter()

const handleSubmit = async (values: any) => {
  loading.value = true
  try {
    const res = await sendResetPasswordCode({ email: values.email })
    if (res.data.code === 0) {
      message.success('验证码已发送，请查收')
      router.push({
        path: '/reset_password',
        query: { email: values.email },
      })
    } else {
      message.error('发送失败，' + res.data.message)
    }
  } catch (error) {
    message.error('请求失败，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
#userForgotPasswordPage {
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
  background-size: 90px 90px, 90px 90px, 18px 18px, 18px 18px;
  pointer-events: none;
  animation: gridFloat 20s ease-in-out infinite;
}

@keyframes gridFloat {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(-1.5px, 1.5px); }
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
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.08), 0 2px 8px rgba(0, 0, 0, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.3);
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
}

:deep(.custom-input) {
  border-radius: 11px;
  border: 1px solid #e8ecef;
  background: rgba(255, 255, 255, 0.85);
  transition: all 0.3s ease;
}

:deep(.custom-input:hover), :deep(.custom-input:focus) {
  border-color: #096dd9;
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
  background: linear-gradient(135deg, #1890ff  0%, #096dd9 100%);
  border: none;
  transition: all 0.3s ease;
}

:deep(.login-button:hover) {
  transform: translateY(-1.5px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.25);
}

.login-footer {
  text-align: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.back-link {
  color: #666;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s;
}

.back-link:hover {
  color: #667eea;
}

@media (max-width: 480px) {
  .login-card { padding: 30px 20px; margin: 10px; }
  .login-wrapper { transform: translateY(-20px); }
}
</style>

