<template>
  <div class="config-manage-page">
    <a-card title="系统配置管理" :bordered="false">
      <a-tabs v-model:activeKey="activeTab">

        <!-- AI 模型配置面板 -->
        <a-tab-pane key="ai" tab="AI 模型密钥配置">
          <a-space direction="vertical" style="width: 100%" size="large">

            <a-alert message="请选择需要配置的模型类型，修改后点击保存即可生效。" type="info" show-icon />

            <!-- 模型类型选择 -->
            <div>
              <span style="margin-right: 16px; font-weight: bold;">配置项：</span>
              <a-radio-group v-model:value="currentAiKey" button-style="solid" @change="loadAiConfig">
                <a-radio-button value="streaming_chat_model">普通流式对话 (Streaming)</a-radio-button>
                <a-radio-button value="reasoning_streaming_chat_model">推理流式对话 (Reasoning)</a-radio-button>
                <a-radio-button value="routing_chat_model">智能路由对话 (Routing)</a-radio-button>
              </a-radio-group>
            </div>

            <a-divider />

            <!-- AI 配置表单 -->
            <a-spin :spinning="aiLoading">
              <a-form v-if="aiForm" :model="aiForm" layout="vertical" @finish="handleAiSubmit">
                <a-row :gutter="24">
                  <a-col :span="12">
                    <a-form-item label="基础路径baseUrl" name="baseUrl">
                      <a-input v-model:value="aiForm.baseUrl" placeholder="例如: https://api.deepseek.com" />
                    </a-form-item>
                  </a-col>
                  <a-col :span="12">
                    <a-form-item label="API密钥" name="apiKey">
                      <a-input-password v-model:value="aiForm.apiKey" placeholder="请输入 API Key" />
                    </a-form-item>
                  </a-col>
                </a-row>

                <a-form-item label="模型名称" name="modelName">
                  <a-input v-model:value="aiForm.modelName" placeholder="请输入模型名称" />
                </a-form-item>

                <a-form-item label="MaxToken" name="maxToken">
                  <a-input v-model:value="aiForm.maxTokens" placeholder="MaxToken" />
                </a-form-item>

                <a-form-item label="temperature" name="temperature">
                  <a-input v-model:value="aiForm.temperature" placeholder="temperature" />
                </a-form-item>

                <!-- 隐藏字段，确保更新时带上Key -->
                <a-form-item v-show="false" name="configKey">
                  <a-input v-model:value="aiForm.configKey" />
                </a-form-item>

                <a-form-item>
                  <a-button type="primary" html-type="submit" :loading="aiSubmitting">保存 AI 配置</a-button>
                </a-form-item>
              </a-form>
              <a-empty v-else description="暂无配置信息" />
            </a-spin>
          </a-space>
        </a-tab-pane>

        <!-- 邮件配置面板 -->
        <a-tab-pane key="email" tab="邮件服务配置">
          <a-spin :spinning="emailLoading">
            <a-form v-if="emailForm" :model="emailForm" layout="vertical" @finish="handleEmailSubmit" style="max-width: 800px">

              <a-row :gutter="24">
                <a-col :span="12">
                  <a-form-item label="邮件服务器 (Host)" name="host" :rules="[{ required: true, message: '请输入Host' }]">
                    <a-input v-model:value="emailForm.host" placeholder="smtp.example.com" />
                  </a-form-item>
                </a-col>
                <a-col :span="12">
                   <a-form-item label="端口 (Port)" name="port" :rules="[{ required: true, message: '请输入端口' }]">
                    <a-input-number v-model:value="emailForm.port" style="width: 100%" placeholder="465" />
                  </a-form-item>
                </a-col>
              </a-row>

              <a-form-item label="发件人邮箱 (FromEmail)" name="fromEmail" :rules="[{ required: true, message: '请输入邮箱账号' }]">
                <a-input v-model:value="emailForm.fromEmail" placeholder="admin@example.com" />
              </a-form-item>

              <a-form-item label="授权码/密码 (Pass)" name="pass" :rules="[{ required: true, message: '请输入密码' }]">
                <a-input-password v-model:value="emailForm.pass" placeholder="请输入邮箱授权码或密码" />
              </a-form-item>

              <a-form-item label="发件人名称" name="user">
                 <a-input v-model:value="emailForm.user" placeholder="显示在邮件中的发送者名称" />
              </a-form-item>

              <a-form-item label="是否启用ssl（0关闭 1启用）" name="sslEnable">
                <a-input v-model:value="emailForm.sslEnable" placeholder="是否启用ssl" />
              </a-form-item>

              <a-form-item>
                <a-button type="primary" html-type="submit" :loading="emailSubmitting">保存邮件配置</a-button>
              </a-form-item>
            </a-form>
          </a-spin>
        </a-tab-pane>

      </a-tabs>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { message } from 'ant-design-vue';
import { getAiModelConfig, updateAiModelConfig } from '@/api/aiModelConfigController';
import { getEmailConfig, updateEmailConfig } from '@/api/emailConfigController';

const activeTab = ref('ai');

// --- AI 配置逻辑 ---
const currentAiKey = ref('streaming_chat_model');
const aiLoading = ref(false);
const aiSubmitting = ref(false);
const aiForm = ref<API.AiModelConfig>({} as API.AiModelConfig);

const loadAiConfig = async () => {
  aiLoading.value = true;
  try {
    const res = await getAiModelConfig({ configKey: currentAiKey.value });
    if (res.data.code === 0 && res.data.data) {
      aiForm.value = res.data.data;
      // 确保表单里的key与选中的一致
      aiForm.value.configKey = currentAiKey.value;
    } else {
      message.error(res.data.message || '加载AI配置失败');
    }
  } catch (e: any) {
    message.error('加载AI配置出错：' + e.message);
  } finally {
    aiLoading.value = false;
  }
};

const handleAiSubmit = async (values: API.AiModelConfig) => {
  aiSubmitting.value = true;
  try {
    // values 中包含了表单数据，合并原始数据以防丢失ID等信息
    const updateData = { ...aiForm.value, ...values, configKey: currentAiKey.value };
    const res = await updateAiModelConfig(updateData);
    if (res.data.code === 0) {
      message.success('AI配置更新成功');
      await loadAiConfig(); // 刷新数据
    } else {
      message.error(res.data.message || '更新失败');
    }
  } catch (e: any) {
    message.error('更新出错：' + e.message);
  } finally {
    aiSubmitting.value = false;
  }
};

// --- 邮件 配置逻辑 ---
const emailLoading = ref(false);
const emailSubmitting = ref(false);
const emailForm = ref<API.EmailConfigUpdateRequest>({} as API.EmailConfigUpdateRequest);

const loadEmailConfig = async () => {
  emailLoading.value = true;
  try {
    const res = await getEmailConfig();
    if (res.data.code === 0 && res.data.data) {
      emailForm.value = res.data.data;
    } else {
      message.error(res.data.message || '加载邮件配置失败');
    }
  } catch (e: any) {
    message.error('加载邮件配置出错：' + e.message);
  } finally {
    emailLoading.value = false;
  }
};

const handleEmailSubmit = async (values: API.EmailConfigUpdateRequest) => {
  emailSubmitting.value = true;
  try {
    const res = await updateEmailConfig(values);
    if (res.data.code === 0) {
      message.success('邮件配置更新成功');
    } else {
      message.error(res.data.message || '更新失败');
    }
  } catch (e: any) {
    message.error('更新出错：' + e.message);
  } finally {
    emailSubmitting.value = false;
  }
};

// 初始化
onMounted(() => {
  loadAiConfig();
  loadEmailConfig();
});

</script>

<style scoped>
.config-manage-page {
  padding: 24px;
}

@media (max-width: 768px) {
  .config-manage-page {
    padding: 12px;
  }
}
</style>

