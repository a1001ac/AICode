<template>
  <div id="userManagePage">
    <!-- 搜索表单 -->
    <div class="search-section">
      <a-card :bordered="false" class="search-card">
        <a-form layout="vertical" :model="searchParams" @finish="doSearch">
          <a-row :gutter="24">
            <a-col :xl="6" :md="12" :sm="24">
              <a-form-item label="账号">
                <a-input
                  v-model:value="searchParams.userAccount"
                  placeholder="请输入账号"
                  allow-clear
                >
                  <template #prefix>
                    <UserOutlined />
                  </template>
                </a-input>
              </a-form-item>
            </a-col>
            <a-col :xl="6" :md="12" :sm="24">
              <a-form-item label="性别">
                <a-select
                  v-model:value="searchParams.userGender"
                  placeholder="请选择用户性别"
                  allow-clear
                >
                  <a-select-option value="">全部</a-select-option>
                  <a-select-option value="1">男</a-select-option>
                  <a-select-option value="0">女</a-select-option>
                  <a-select-option value="2">保密</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :xl="6" :md="12" :sm="24">
              <a-form-item label="邮箱">
                <a-input
                  v-model:value="searchParams.userEmail"
                  placeholder="请输入用户邮箱"
                  allow-clear
                >
                  <template #prefix>
                    <MailOutlined />
                  </template>
                </a-input>
              </a-form-item>
            </a-col>
            <a-col :xl="6" :md="12" :sm="24">
              <a-form-item label="用户角色">
                <a-select
                  v-model:value="searchParams.userRole"
                  placeholder="请选择用户角色"
                  allow-clear
                >
                  <a-select-option value="">全部角色</a-select-option>
                  <a-select-option value="admin">管理员</a-select-option>
                  <a-select-option value="user">普通用户</a-select-option>
                </a-select>
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
            <span>用户管理列表</span>
            <a-button type="primary" @click="showAddModal = true">
              <template #icon><PlusOutlined /></template>
              添加用户
            </a-button>
          </div>
        </template>

        <a-table
          :columns="columns"
          :data-source="data"
          :pagination="pagination"
          @change="doTableChange"
          :scroll="{ x: 1500, y: 'calc(100vh - 450px)' }"
          :loading="loading"
          row-key="id"
          class="user-table"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.dataIndex === 'id'">
              <span class="user-id">#{{ record.id }}</span>
            </template>
            <template v-else-if="column.dataIndex === 'userAvatar'">
              <div class="avatar-cell">
                <a-avatar
                  v-if="record.userAvatar"
                  :src="record.userAvatar"
                  :size="40"
                  shape="square"
                />
                <a-avatar v-else :size="40" shape="square" style="background-color: #87d068">
                  {{ record.userName?.substring(0, 1) || 'U' }}
                </a-avatar>
              </div>
            </template>
            <template v-else-if="column.dataIndex === 'userAccount'">
              <a-input
                v-if="editingKey === record.id"
                v-model:value="editableData[record.id].userAccount"
                style="margin: -5px 0"
              />
              <span v-else class="user-account">{{ record.userAccount }}</span>
            </template>
            <template v-else-if="column.dataIndex === 'userGender'">
              <a-select
                v-if="editingKey === record.id"
                v-model:value="editableData[record.id].userGender"
                style="width: 100%"
              >
                <a-select-option :value="1">♂️</a-select-option>
                <a-select-option :value="0">♀️</a-select-option>
                <a-select-option :value="2">㊙️</a-select-option>
              </a-select>
              <a-tooltip
                v-else
                :title="record.userGender === 1 ? '男' : record.userGender === 0 ? '女' : '保密'"
                ><span>
                  {{ record.userGender === 1 ? '♂️' : record.userGender === 0 ? '♀️' : '㊙️' }}</span
                >
              </a-tooltip>
            </template>
            <template v-else-if="column.dataIndex === 'userProfile'">
              <a-input
                v-if="editingKey === record.id"
                v-model:value="editableData[record.id].userProfile"
                style="margin: -5px 0"
              />
              <a-tooltip v-else :title="record.userProfile">
                <div class="profile-text">
                  {{ record.userProfile || '-' }}
                </div>
              </a-tooltip>
            </template>
            <template v-else-if="column.dataIndex === 'userRole'">
              <a-select
                v-if="editingKey === record.id"
                v-model:value="editableData[record.id].userRole"
                style="width: 100%"
              >
                <a-select-option value="user">普通用户</a-select-option>
                <a-select-option value="admin">管理员</a-select-option>
              </a-select>
              <a-tag v-else-if="record.userRole === 'admin'" color="green">管理员</a-tag>
              <a-tag v-else color="blue">普通用户</a-tag>
            </template>
            <template v-else-if="column.dataIndex === 'userEmail'">
              <a-input
                v-if="editingKey === record.id"
                v-model:value="editableData[record.id].userEmail"
                style="margin: -5px 0"
              />
              <span v-else>{{ record.userEmail }}</span>
            </template>
            <template v-else-if="column.dataIndex === 'loginIP'">
              <span class="login-ip">{{ record.loginIp }}</span>
            </template>
            <template v-else-if="column.dataIndex === 'loginAddress'">
              <span class="login-ip">{{ record.loginAddress }}</span>
            </template>
            <template v-else-if="column.dataIndex === 'registerIP'">
              <span class="login-ip">{{ record.registerIp }}</span>
            </template>
            <template v-else-if="column.dataIndex === 'registerAddress'">
              <span class="login-ip">{{ record.registerAddress }}</span>
            </template>
            <template v-else-if="column.dataIndex === 'updateTime'">
              <span class="create-time">
                {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
              </span>
            </template>
            <template v-else-if="column.dataIndex === 'createTime'">
              <span class="create-time">
                {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
              </span>
            </template>
            <template v-else-if="column.key === 'action'">
              <div class="action-buttons">
                <span v-if="editingKey === record.id">
                  <a-button type="link" size="small" @click="doSave(record.id)">保存</a-button>
                  <a-popconfirm title="确定取消吗?" ok-text="确定" cancel-text="取消" @confirm="doCancel(record.id)">
                    <a-button type="link" size="small">取消</a-button>
                  </a-popconfirm>
                </span>
                <span v-else>
                  <a-button
                    type="link"
                    size="small"
                    :disabled="editingKey !== ''"
                    @click="doEdit(record.id)"
                    >编辑</a-button
                  >
                  <template v-if="record.id === loginUser.id">
                    <a-tooltip title="不能删除自己">
                      <a-button type="link" size="small" danger disabled>
                        <DeleteOutlined /> 删除
                      </a-button>
                    </a-tooltip>
                  </template>
                  <template v-else>
                    <a-popconfirm
                      title="确定要删除这个用户吗？"
                      @confirm="doDelete(record)"
                      ok-text="确定"
                      cancel-text="取消"
                    >
                      <a-button type="link" size="small" danger> <DeleteOutlined /> 删除 </a-button>
                    </a-popconfirm>
                  </template>
                </span>
              </div>
            </template>
          </template>
        </a-table>
      </a-card>
    </div>
    <UserAddModal v-model:open="showAddModal" @success="fetchData" />
  </div>
</template>

<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import {
  SearchOutlined,
  UserOutlined,
  MailOutlined,
  RedoOutlined,
  DeleteOutlined,
  PlusOutlined,
} from '@ant-design/icons-vue'
import {
  deleteUserById,
  listUserVoByPage,
  updateUser,
  updateUserRole,
} from '@/api/userController.ts'
import dayjs from 'dayjs'
import { useLoginUserStore } from '@/stores/loginUser.ts'
// @ts-ignore
import { cloneDeep } from 'lodash-es'
import UserAddModal from '@/components/UserAddModal.vue'

const userStore = useLoginUserStore()
const loginUser = userStore.loginUser

const showAddModal = ref(false)

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: 100,
    fixed: 'left',
    align: 'center',
  },
  {
    title: '头像',
    dataIndex: 'userAvatar',
    width: 80,
    align: 'center',
  },
  {
    title: '账号',
    dataIndex: 'userAccount',
    width: 150,
    align: 'center',
  },
  {
    title: '性别',
    dataIndex: 'userGender',
    width: 80,
    align: 'center',
  },
  {
    title: '用户简介',
    dataIndex: 'userProfile',
    width: 250,
    align: 'center',
  },
  {
    title: '用户角色',
    dataIndex: 'userRole',
    width: 120,
    align: 'center',
  },
  {
    title: '邮箱',
    dataIndex: 'userEmail',
    width: 200,
    align: 'center',
  },
  {
    title: '最后登录时间',
    dataIndex: 'updateTime',
    width: 200,
    align: 'center',
  },
  {
    title: '最后登录IP',
    dataIndex: 'loginIP',
    width: 150,
    align: 'center',
  },
  {
    title: '最后登录地点',
    dataIndex: 'loginAddress',
    width: 150,
    align: 'center',
  },
  {
    title: '注册IP',
    dataIndex: 'registerIP',
    width: 150,
    align: 'center',
  },
  {
    title: '注册地点',
    dataIndex: 'registerAddress',
    width: 150,
    align: 'center',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    width: 180,
    align: 'center',
  },
  {
    title: '操作',
    key: 'action',
    width: 100,
    fixed: 'right',
    align: 'center',
  },
]

// 编辑相关
const editingKey = ref<number | string>('')
const editableData = reactive<Record<string, API.UserVO>>({})

const doEdit = (id: number) => {
  editableData[id] = cloneDeep(data.value.filter((item) => id === item.id)[0])
  editingKey.value = id
}

const doSave = async (id: number) => {
  const updatedUser = editableData[id]
  try {
    // 1. 先更新基本信息（包括性别）
    const userRes = await updateUser({
      id: updatedUser.id,
      userAccount: updatedUser.userAccount,
      // 强制确保是数字
      userGender: Number(updatedUser.userGender),
      userProfile: updatedUser.userProfile,
      userEmail: updatedUser.userEmail,
    })

    if (userRes.data.code !== 0) {
      message.error('用户信息更新失败: ' + userRes.data.message)
      return // 如果第一个失败了，就没必要走第二个了
    }

    // 2. 基本信息成功后，再更新角色
    const roleRes = await updateUserRole({
      id: updatedUser.id,
      userRole: updatedUser.userRole,
    })

    if (roleRes.data.code === 0) {
      message.success('更新成功')
      editingKey.value = ''
      await fetchData() // 重新加载数据
    } else {
      message.error('用户角色更新失败: ' + roleRes.data.message)
    }
  } catch (e) {
    console.error(e)
    message.error('更新操作失败')
  }
}

const doCancel = (id: number) => {
  delete editableData[id]
  editingKey.value = ''
}

// 数据
const data = ref<API.UserVO[]>([])
const total = ref(0)
const loading = ref(false)

// 搜索条件
const searchParams = reactive<API.UserQueryRequest>({
  current: 1,
  pageSize: 10,
})

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    const res = await listUserVoByPage({
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
  } finally {
    loading.value = false
  }
}

// 重置搜索
const resetSearch = () => {
  searchParams.userAccount = undefined
  searchParams.userGender = undefined
  searchParams.userEmail = undefined
  searchParams.userRole = undefined
  searchParams.current = 1
  fetchData()
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
    showTotal: (total: number) => `共 ${total} 条记录`,
    showQuickJumper: true,
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

// 删除数据
const doDelete = async (record: API.UserVO) => {
  if (!record.id) return

  try {
    const res = await deleteUserById({ id: record.id })
    if (res.data.code === 0) {
      message.success('删除成功')
      // 刷新数据
      fetchData()
    } else {
      message.error('删除失败：' + res.data.message)
    }
  } catch (error) {
    console.error('删除失败：', error)
    message.error('删除失败')
  }
}
</script>

<style scoped>
#userManagePage {
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

.user-table {
  border: none;
}

.user-table :deep(.ant-table-thead > tr > th) {
  background: #fafafa;
  font-weight: 600;
  color: #333;
}

.user-table :deep(.ant-table-tbody > tr:hover) {
  background: #f9f9f9;
}

.user-id {
  font-family: 'Monaco', 'Consolas', monospace;
  color: #1890ff;
  font-weight: 500;
}

.avatar-cell {
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-account {
  font-weight: 500;
  color: #1a1a1a;
}

.user-name {
  font-weight: 500;
  color: #1a1a1a;
}

.profile-text {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #666;
  margin: 0 auto;
}

.create-time {
  color: #8c8c8c;
  font-size: 12px;
}

.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
  flex-wrap: nowrap;
}

:deep(.ant-table-tbody > tr > td) {
  vertical-align: middle;
  padding: 16px 8px;
}

:deep(.ant-table-thead > tr > th) {
  padding: 16px 8px;
}

@media (max-width: 768px) {
  #userManagePage {
    padding: 12px;
  }

  .table-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
}
</style>
