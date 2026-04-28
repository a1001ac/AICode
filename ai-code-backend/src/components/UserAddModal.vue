<template>
  <a-modal
    :open="open"
    title="添加用户"
    cancel-text="取消"
    ok-text="添加"
    :confirm-loading="confirmLoading"
    @ok="handleSubmit"
    @cancel="closeModal"
    :afterClose="resetForm"
  >
    <a-form
      ref="formRef"
      :model="formState"
      :rules="rules"
      :label-col="{ span: 6 }"
      :wrapper-col="{ span: 16 }"
    >
      <a-form-item label="账号" name="userAccount">
        <a-input v-model:value="formState.userAccount" placeholder="请输入账号" />
      </a-form-item>
      <a-form-item label="密码" name="userPassword">
        <a-input-password v-model:value="formState.userPassword" placeholder="请输入密码" />
      </a-form-item>
      <a-form-item label="用户角色" name="userRole">
        <a-select v-model:value="formState.userRole" placeholder="请选择用户角色">
          <a-select-option value="user">普通用户</a-select-option>
          <a-select-option value="admin">管理员</a-select-option>
        </a-select>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue';
import { message, type FormInstance } from 'ant-design-vue';
import { addUser } from '@/api/userController';
// 假设你有一个 types 目录或全局定义，如果没有，请保留你原本的 API.UserAddRequest
// import type { API } from '@/services/ant-design-pro/typings';

// 1. 定义 Props，使用 'open' 对应官方文档 (Antd v3+)
// 如果你使用的是老版本 (Antd v2)，请改回 'visible'
const props = defineProps<{
  open: boolean;
}>();

const emit = defineEmits(['update:open', 'success']);

// 2. 表单引用和加载状态
const formRef = ref<FormInstance>();
const confirmLoading = ref<boolean>(false);

const formState = reactive({
  userAccount: '',
  userPassword: '',
  userRole: 'user',
});

// 3. 定义表单校验规则 (替代手动的 if check)
const rules = {
  userAccount: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  userPassword: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '密码不能少于6位', trigger: 'blur' }],
  userRole: [{ required: true, message: '请选择用户角色', trigger: 'change' }],
};

// 关闭弹窗
const closeModal = () => {
  emit('update:open', false);
};

// 重置表单 (在弹窗完全关闭后触发，体验更好)
const resetForm = () => {
  formRef.value?.resetFields();
};

// 提交逻辑
const handleSubmit = async () => {
  try {
    // 1. 先校验表单
    await formRef.value?.validate();

    // 2. 开启加载状态
    confirmLoading.value = true;

    // 3. 发送请求
    const res = await addUser(formState);

    if (res.data.code === 0) {
      message.success('添加用户成功');
      emit('success'); // 通知父组件刷新列表
      closeModal();    // 关闭弹窗
      // 注意：这里不需要手动重置 formState，因为加了 afterClose 钩子会自动重置
    } else {
      message.error('添加失败：' + res.data.message);
    }
  } catch (error) {
    // 捕获校验失败或API错误
    console.error(error);
    // 如果不是校验错误（校验错误不需要弹窗提示，表单会自动红字提示）
    if (!(error as any).errorFields) {
      message.error('添加失败，请稍后重试');
    }
  } finally {
    // 4. 无论成功失败，关闭 loading
    confirmLoading.value = false;
  }
};
</script>
