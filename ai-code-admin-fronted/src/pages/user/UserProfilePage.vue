<template>
  <div class="profile-container">
    <a-row :gutter="24">
      <a-col :xs="24" :sm="24" :md="8">
        <a-card :bordered="false" class="left-profile-card" :bodyStyle="{ padding: 0 }">
          <div class="profile-banner">
            <img src="https://ermao-1325310617.cos.ap-chengdu.myqcloud.com/AI/avatar/acec5153-d5f2-41f4-a76e-8c531584ac32.jpg" alt="banner" />
          </div>

          <div class="profile-info-wrapper">
            <div class="avatar-container">
              <a-upload
                name="file"
                :show-upload-list="false"
                :customRequest="handleUploadAvatar"
                :before-upload="beforeUpload"
                class="avatar-uploader"
              >
                <div class="avatar-wrapper">
                  <img v-if="imageUrl" :src="imageUrl" class="avatar-img" alt="avatar" />
                  <div v-else class="avatar-placeholder">
                    <user-outlined style="font-size: 40px; color: #ccc;" />
                  </div>
                  <div class="avatar-mask">
                    <camera-outlined style="font-size: 24px; color: #fff;" />
                    <span style="font-size: 12px; color: #fff; margin-top: 4px">更换头像</span>
                  </div>
                </div>
              </a-upload>
            </div>

            <h2 class="display-name">{{ loginUserStore.loginUser.userAccount}}</h2>
            <div class="display-desc">{{ loginUserStore.loginUser.userProfile || '暂无个人介绍' }}</div>

            <div class="info-list">
              <div class="info-item">
                <div class="info-label"><user-outlined /> 账号</div>
                <div class="info-value">{{ loginUserStore.loginUser.userAccount}}</div>
              </div>
              <div class="info-item">
                <div class="info-label"><SmileOutlined /> 性别</div>
                <div class="info-value">
                  <span>
                  {{ loginUserStore.loginUser.userGender === 1 ? '♂️' : loginUserStore.loginUser.userGender === 0 ? '♀️' : '㊙️' }}</span>
                </div>
              </div>
              <div class="info-item">
                <div class="info-label"><mail-outlined /> 邮箱</div>
                <div class="info-value">{{ loginUserStore.loginUser.userEmail || '未设置' }}</div>
              </div>
              <div class="info-item">
                <div class="info-label"><calendar-outlined /> 创建日期</div>
                <div class="info-value">{{ formatDate(loginUserStore.loginUser.createTime) }}</div>
              </div>
            </div>
          </div>
        </a-card>
      </a-col>

      <a-col :xs="24" :sm="24" :md="16">
        <a-card :bordered="false" title="基本设置" class="right-setting-card">
          <a-form layout="vertical" :model="formState" ref="basicFormRef">
            <a-row :gutter="24">
              <a-col :span="12">
                <a-form-item label="账号" name="userAccount" :rules="[{ required: true, message: '请输入账号' }]">
                  <a-input v-model:value="formState.userAccount" placeholder="请输入账号" :disabled="!isEditing" class="custom-input" />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="性别" name="userGender">
                  <a-select v-model:value="formState.userGender" placeholder="请选择性别" :disabled="!isEditing" class="custom-input">
                    <a-select-option :value="1">男</a-select-option>
                    <a-select-option :value="0">女</a-select-option>
                    <a-select-option :value="2">保密</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
            </a-row>

            <a-row :gutter="24">
              <a-col :span="12">
                <a-form-item label="邮箱" name="userEmail">
                  <a-input v-model:value="formState.userEmail" placeholder="请输入邮箱" :disabled="!isEditing" class="custom-input" />
                </a-form-item>
              </a-col>
            </a-row>

            <a-form-item label="个人介绍" name="userProfile">
              <a-textarea
                v-model:value="formState.userProfile"
                :rows="4"
                placeholder="请输入个人介绍"
                :disabled="!isEditing"
                class="custom-input"
              />
            </a-form-item>

            <div class="form-footer">
              <a-button type="primary" @click="handleBasicEditAction" :loading="submitLoading" class="action-button">
                {{ isEditing ? '保存' : '编辑' }}
              </a-button>
            </div>
          </a-form>
        </a-card>

        <a-card :bordered="false" title="更改密码" class="right-setting-card" style="margin-top: 20px">
          <a-form layout="vertical" :model="passwordFormState">
            <a-form-item label="原密码" name="oldPassword" :rules="[{ required: isPwdEditing, message: '请输入原密码' }]">
              <a-input-password
                v-model:value="passwordFormState.oldPassword"
                placeholder="请输入原密码"
                :disabled="!isPwdEditing"
                class="custom-input"
              />
            </a-form-item>
            <a-form-item label="新密码" name="newPassword" :rules="[{ required: isPwdEditing, message: '请输入新密码' }]">
              <a-input-password
                v-model:value="passwordFormState.newPassword"
                placeholder="请输入新密码"
                :disabled="!isPwdEditing"
                class="custom-input"
              />
            </a-form-item>
            <a-form-item label="确认密码" name="checkPassword" :rules="[{ required: isPwdEditing, message: '请输入确认密码' }]">
              <a-input-password
                v-model:value="passwordFormState.checkPassword"
                placeholder="请输入确认密码"
                :disabled="!isPwdEditing"
                class="custom-input"
              />
            </a-form-item>
            <div class="form-footer">
              <a-button type="primary" @click="handlePasswordEditAction" :loading="passwordLoading" class="action-button">
                {{ isPwdEditing ? '提交' : '编辑' }}
              </a-button>
            </div>
          </a-form>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, watchEffect } from "vue";
import { useLoginUserStore } from "@/stores/loginUser.ts";
import { message } from "ant-design-vue";
import {
  UserOutlined, MailOutlined, CalendarOutlined,
  CameraOutlined,
  SmileOutlined
} from "@ant-design/icons-vue";
import { updateUser, uploadAvatar, updateUserPassword } from "@/api/userController.ts";
import dayjs from "dayjs";

const loginUserStore = useLoginUserStore();
const submitLoading = ref(false);
const passwordLoading = ref(false);
const imageUrl = ref("");

// 编辑状态控制
const isEditing = ref(false);
const isPwdEditing = ref(false);

const formState = reactive({
  id: undefined as number | undefined,
  userAccount: '',
  userGender: undefined as number | undefined,
  userAvatar: '',
  userProfile: '',
  userEmail: '',
  userPhone: '',
});

const passwordFormState = reactive({
  oldPassword: '',
  newPassword: '',
  checkPassword: '',
});

// 监听 Store 数据变化并回填到表单
watchEffect(() => {
  if (loginUserStore.loginUser) {
    const user = loginUserStore.loginUser;
    formState.id = user.id;
    formState.userAccount = user.userAccount || '';
    formState.userGender = user.userGender;
    formState.userAvatar = user.userAvatar || '';
    formState.userProfile = user.userProfile || '';
    formState.userEmail = user.userEmail || '';
    imageUrl.value = user.userAvatar || "";
  }
});

/**
 * 头像上传逻辑
 * 注意：此处使用自定义 Request 覆盖默认 XHR 行为
 */
const handleUploadAvatar = async ({ file, onSuccess, onError }: any) => {
  try {
    // 必须使用 FormData 来上传文件
    const formData = new FormData();
    formData.append('file', file);

    // 调用 API
    // 注意：虽然 API 定义写着 'application/json'，但传递 FormData 时，
    // 浏览器通常会自动设置 Content-Type 为 multipart/form-data，
    // 如果你的 request 封装强制覆盖了 header，可能需要在此处显式传递 header 覆盖回去。
    // 假设你的 request 库足够智能：
    const res = await uploadAvatar(formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });

    if (res.data.code === 0) {
      // 后端返回的是图片 URL
      const newAvatarUrl = res.data.data as string;
      imageUrl.value = newAvatarUrl;
      formState.userAvatar = newAvatarUrl;

      // 顺便更新一下用户的头像信息，无需点击保存
      await updateUser({ ...formState, userAvatar: newAvatarUrl });
      await loginUserStore.fetchLoginUser();

      message.success("头像上传成功");
      onSuccess(res.data);
    } else {
      message.error("上传失败：" + res.data.message);
      onError(new Error(res.data.message));
    }
  } catch (error) {
    message.error("上传出错");
    onError(error);
  }
};

const beforeUpload = (file: File) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png';
  if (!isJpgOrPng) {
    message.error('只能上传 JPG/PNG 文件!');
  }
  const isLt4M = file.size / 1024 / 1024 < 4;
  if (!isLt4M) {
    message.error('头像必须小于 2MB!');
  }
  return isJpgOrPng && isLt4M;
};

/**
 * 基本信息按钮逻辑：
 * 1. isEditing = false (显示"编辑") -> 点击变为 true (解锁表单)
 * 2. isEditing = true (显示"保存") -> 点击提交表单 -> 成功后变为 false
 */
const handleBasicEditAction = async () => {
  if (!isEditing.value) {
    isEditing.value = true;
    return;
  }

  // 提交逻辑
  submitLoading.value = true;
  try {
    const res = await updateUser(formState);
    if (res.data.code === 0) {
      message.success("保存成功");
      await loginUserStore.fetchLoginUser();
      isEditing.value = false; // 锁定表单
    } else {
      message.error(res.data.message);
    }
  } catch (error) {
    message.error("系统错误");
  } finally {
    submitLoading.value = false;
  }
};

const handlePasswordEditAction = async () => {
  if (!isPwdEditing.value) {
    isPwdEditing.value = true;
    return;
  }
  if (!passwordFormState.oldPassword) {
    message.warning("请输入原密码");
    return;
  }
  passwordLoading.value = true;
  try {
    const res = await updateUserPassword(passwordFormState);
    if (res.data.code === 0) {
      message.success("密码修改成功");
      isPwdEditing.value = false;
      // 清空密码框
      passwordFormState.oldPassword = '';
      passwordFormState.newPassword = '';
      passwordFormState.checkPassword = '';
    } else {
      message.error(res.data.message);
    }
  } finally {
    passwordLoading.value = false;
  }
};

// 辅助函数
const formatDate = (d?: string) => d ? dayjs(d).format('YYYY-MM-DD HH:mm:ss') : '未知';
</script>

<style scoped>
.profile-container {
  padding: 24px;
  background-color: #f0f2f5;
  min-height: 100vh;
}

/* 左侧卡片样式 */
.left-profile-card {
  border-radius: 8px;
  overflow: visible; /* 确保卡片不会隐藏溢出的头像 */
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.03);
  background: #fff;
}

.profile-banner {
  height: 180px;
  width: 100%;
  overflow: hidden;
  border-radius: 8px 8px 0 0; /* banner顶部圆角 */
}

.profile-banner img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.profile-info-wrapper {
  padding: 0 24px 24px;
  background: #fff;
  position: relative;
  border-radius: 0 0 8px 8px;
  /* 增加顶部内边距，为绝对定位的头像留出空间，防止用户名被遮挡 */
  padding-top: 60px;
  text-align: center;
}

/* 头像容器绝对定位 */
.avatar-container {
  position: absolute;
  /* 计算方式：头像总高度约为108px(100px高+8px边框)，向上偏移一半高度 */
  top: -54px;
  left: 50%;
  /* 水平居中 */
  transform: translateX(-50%);
  z-index: 10;
}

.avatar-uploader {
  display: inline-block;
  cursor: pointer;
}

.avatar-wrapper {
  position: relative;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  border: 2px solid #fff; /* 头像白边 */
  box-shadow: 0 5px 10px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  background: #ffffff;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
}

/* 鼠标悬停显示遮罩 */
.avatar-mask {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.avatar-wrapper:hover .avatar-mask {
  opacity: 1;
}

/* 文本样式 */
.display-name {
  text-align: center;
  margin-bottom: 4px;
  font-weight: 600;
  color: #262626;
  font-size: 20px;
}
.display-desc {
  text-align: center;
  color: #8c8c8c;
  margin-bottom: 24px;
  font-size: 14px;
}

/* 信息列表样式 */
.info-list {
  border-top: 1px solid #f0f0f0;
  padding-top: 24px;
}
.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  font-size: 14px;
  line-height: 1.5;
}
.info-label {
  color: #595959;
}
.info-label .anticon {
  margin-right: 8px;
}
.info-value {
  color: #262626;
  font-weight: 500;
}

/* 右侧表单样式 */
.right-setting-card {
  border-radius: 8px;
  box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.03);
  background: #fff;
}

.form-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
  border-top: 1px solid #f0f0f0;
  padding-top: 16px;
}

/* Ant Design 样式微调 */
:deep(.ant-card-head) {
  border-bottom: 1px solid #f0f0f0;
  padding: 0 24px;
  min-height: 56px;
}
:deep(.ant-card-head-title) {
  font-weight: 500;
  font-size: 16px;
}

/* 登录页样式 */
:deep(.custom-input) {
  border-radius: 11px;
  border: 1px solid #e8ecef;
  background: #fafafa;
  transition: all 0.3s ease;
}

:deep(.custom-input:hover) {
  border-color: #1890ff;
  box-shadow: 0 3px 12px rgba(102, 126, 234, 0.12);
}

:deep(.custom-input:focus),
:deep(.ant-select-focused .ant-select-selector) {
  border-color: #1890ff !important;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.15) !important;
}

:deep(.ant-select-selector) {
  border-radius: 11px !important;
  border: 1px solid #e8ecef !important;
  background: #fafafa !important;
  transition: all 0.3s ease;
}

:deep(.action-button) {
  border-radius: 11px;
  font-weight: 500;
  background: #1890ff;
  border: none;
  transition: all 0.3s ease;
  padding: 0 24px;
}

:deep(.action-button:hover) {
  transform: translateY(-1.5px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.25);
}

:deep(.action-button:active) {
  transform: translateY(0);
}
</style>
