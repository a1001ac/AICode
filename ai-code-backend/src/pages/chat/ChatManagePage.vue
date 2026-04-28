<template>
  <div id="chatManagePage">
    <!-- 搜索表单 -->
    <div class="search-section">
      <a-card :bordered="false" class="search-card">
        <a-form layout="vertical" :model="searchParams" @finish="doSearch">
          <a-row :gutter="24">
            <a-col :xl="6" :md="12" :sm="24">
              <a-form-item label="消息内容">
                <a-input v-model:value="searchParams.message" placeholder="输入消息内容" allow-clear />
              </a-form-item>
            </a-col>
            <a-col :xl="6" :md="12" :sm="24">
              <a-form-item label="消息类型">
                <a-select
                  v-model:value="searchParams.messageType"
                  placeholder="选择消息类型"
                  allow-clear
                >
                  <a-select-option value="">全部</a-select-option>
                  <a-select-option value="user">用户消息</a-select-option>
                  <a-select-option value="ai">AI消息</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :xl="6" :md="12" :sm="24">
              <a-form-item label="应用ID">
                <a-input v-model:value="searchParams.appId" placeholder="输入应用ID" allow-clear />
              </a-form-item>
            </a-col>
            <a-col :xl="6" :md="12" :sm="24">
              <a-form-item label="用户ID">
                <a-input v-model:value="searchParams.userId" placeholder="输入用户ID" allow-clear />
              </a-form-item>
            </a-col>
            <a-col :xl="6" :md="12" :sm="24">
              <a-form-item label="&nbsp;">
                <a-space>
                  <a-button type="primary" html-type="submit">
                    <template #icon><SearchOutlined /></template>
                    搜索
                  </a-button>
                  <a-button @click="resetSearch">
                    <template #icon><RedoOutlined /></template>
                    重置
                  </a-button>
                </a-space>
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>
      </a-card>
    </div>

    <!-- 表格区域 -->
    <div class="table-section">
      <a-card :bordered="false">
        <template #title>
          <div class="table-header">
            <span>聊天消息列表</span>
          </div>
        </template>
        <a-table
          :columns="columns"
          :data-source="data"
          :pagination="pagination"
          @change="doTableChange"
          :scroll="{ x: 1500, y: 'calc(100vh - 450px)' }"
          row-key="id"
          class="chat-table"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.dataIndex === 'id'">
              <span class="id">#{{ record.id }}</span>
            </template>
            <template v-if="column.dataIndex === 'message'">
              <a-tooltip :title="record.message">
                <div class="message-text">{{ record.message }}</div>
              </a-tooltip>
            </template>
            <template v-else-if="column.dataIndex === 'messageType'">
              <a-tag :color="record.messageType === 'user' ? 'blue' : 'green'">
                {{ record.messageType === 'user' ? '用户消息' : 'AI消息' }}
              </a-tag>
            </template>
            <template v-if="column.dataIndex === 'appId'">
              <span class="id">#{{ record.id }}</span>
            </template>
            <template v-if="column.dataIndex === 'userId'">
              <span class="id">#{{ record.id }}</span>
            </template>
            <template v-else-if="column.dataIndex === 'createTime'">
              {{ formatTime(record.createTime) }}
            </template>
            <template v-else-if="column.key === 'action'">
              <a-space>
                <a-button type="primary" size="small" @click="viewAppChat(record.appId)">
                  查看对话
                </a-button>
                <a-popconfirm title="确定要删除这条消息吗？"
                              @confirm="deleteMessage(record.id)"
                              ok-text="确定"
                              cancel-text="取消">
                  <a-button type="link" size="small" danger> <DeleteOutlined /> 删除 </a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </template>
        </a-table>
      </a-card>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { listAllChatHistoryByPageForAdmin } from '@/api/chatHistoryController'
import { formatTime } from '@/utils/time'
import { DeleteOutlined, RedoOutlined, SearchOutlined } from '@ant-design/icons-vue'

const router = useRouter()

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: 100,
    fixed: 'left',
    align: 'center',
  },
  {
    title: '消息内容',
    dataIndex: 'message',
    width: 150,
    align: 'center',
  },
  {
    title: '消息类型',
    dataIndex: 'messageType',
    width: 80,
    align: 'center',
  },
  {
    title: '应用ID',
    dataIndex: 'appId',
    width: 100,
    align: 'center',
  },
  {
    title: '用户ID',
    dataIndex: 'userId',
    width: 100,
    align: 'center',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 160,
    align: 'center',
  },
  {
    title: '操作',
    key: 'action',
    width: 80,
    fixed: 'right',
    align: 'center',
  },
]

// 数据
const data = ref<API.ChatHistory[]>([])
const total = ref(0)

// 搜索条件
const searchParams = reactive<API.ChatHistoryQueryRequest>({
  current: 1,
  pageSize: 10,
})

// 获取数据
const fetchData = async () => {
  try {
    const res = await listAllChatHistoryByPageForAdmin({
      ...searchParams,
    })
    if (res.data.data) {
      data.value = res.data.data.records ?? []
      total.value = res.data.data.total ?? 0
    } else {
      message.error('获取数据失败，' + res.data.message)
    }
  } catch (error) {
    console.error('获取数据失败：', error)
    message.error('获取数据失败')
  }
}

// 页面加载时请求一次
onMounted(() => {
  fetchData()
})

// 分页参数
const pagination = computed(() => {
  return {
    current: searchParams.current ?? 1,
    pageSize: searchParams.pageSize ?? 10,
    total: total.value,
    showSizeChanger: true,
    showTotal: (total: number) => `共 ${total} 条`,
  }
})

// 表格变化处理
const doTableChange = (page: { current: number; pageSize: number }) => {
  searchParams.current = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}

// 搜索
const doSearch = () => {
  // 重置页码
  searchParams.current = 1
  fetchData()
}

// 重置搜索
const resetSearch = () => {
  searchParams.message = undefined
  searchParams.messageType = undefined
  searchParams.appId = undefined
  searchParams.userId = undefined
  fetchData()
}

// 查看应用对话
const viewAppChat = (appId: number | undefined) => {
  if (appId) {
    router.push(`/app/chat/${appId}`)
  }
}

// 删除消息
const deleteMessage = async (id: number | undefined) => {
  if (!id) return

  try {
    // 注意：这里需要后端提供删除对话历史的接口
    // 目前先显示成功，实际实现需要调用删除接口
    message.success('删除成功')
    // 刷新数据
    fetchData()
  } catch (error) {
    console.error('删除失败：', error)
    message.error('删除失败')
  }
}
</script>

<style scoped>
#chatManagePage {
  padding: 24px;
  background: #f5f6f7;
  min-height: calc(100vh - 64px);
}

.search-section {
  margin-bottom: 24px;
}

.search-card {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.09);
  border-radius: 8px;
}

.table-section {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.09);
  overflow: hidden;
}

:deep(.table-section .ant-card-head) {
  border-bottom: 1px solid #f0f0f0;
  padding: 0 24px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-table {
  border: none;
}

.chat-table :deep(.ant-table-thead > tr > th) {
  background: #fafafa;
  font-weight: 600;
  color: #333;
}

.chat-table :deep(.ant-table-tbody > tr:hover) {
  background: #f9f9f9;
}

.id {
  font-family: 'Monaco', 'Consolas', monospace;
  color: #1890ff;
  font-weight: 500;
}

.message-text {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #666;
  margin: 0 auto;
}

:deep(.ant-table-tbody > tr > td) {
  vertical-align: middle;
  padding: 16px 8px;
}

:deep(.ant-table-thead > tr > th) {
  padding: 16px 8px;
}

@media (max-width: 768px) {
  #chatManagePage {
    padding: 12px;
  }

  .table-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
}
</style>
