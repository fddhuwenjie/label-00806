<template>
  <div class="page-card">
    <div class="card-header">
      <span class="title">用户管理</span>
    </div>

    <div class="search-bar">
      <el-input v-model="query.keyword" placeholder="昵称/手机号" clearable style="width: 200px" @keyup.enter="loadData" />
      <el-select v-model="query.status" placeholder="状态" clearable style="width: 120px">
        <el-option label="正常" :value="1" />
        <el-option label="禁用" :value="0" />
      </el-select>
      <el-button type="primary" @click="loadData">搜索</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="nickname" label="昵称">
        <template #default="{ row }">{{ row.nickname || '未设置' }}</template>
      </el-table-column>
      <el-table-column prop="phone" label="手机号">
        <template #default="{ row }">{{ row.phone || '未绑定' }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间" width="180">
        <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button link :type="row.status === 1 ? 'danger' : 'success'" @click="toggleStatus(row)">
            {{ row.status === 1 ? '禁用' : '启用' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination v-model:current-page="query.page" v-model:page-size="query.pageSize"
        :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" @change="loadData" />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'

function formatTime(time) {
  if (!time) return '-'
  return time.replace('T', ' ').substring(0, 19)
}
import { ElMessage } from 'element-plus'
import { getUserPage, updateUserStatus } from '@/api/user'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const query = reactive({ page: 1, pageSize: 10, keyword: '', status: null })

async function loadData() {
  loading.value = true
  try {
    const data = await getUserPage(query)
    tableData.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

async function toggleStatus(row) {
  // 移除按钮焦点
  document.activeElement?.blur()
  await updateUserStatus({ id: row.id, status: row.status === 1 ? 0 : 1 })
  ElMessage.success('操作成功')
  loadData()
}

loadData()
</script>
