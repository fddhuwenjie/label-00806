<template>
  <div class="page-card">
    <div class="card-header">
      <span class="title">订单管理</span>
    </div>

    <div class="search-bar">
      <el-select v-model="query.status" placeholder="订单状态" clearable style="width: 150px" @change="loadData">
        <el-option label="待支付" :value="0" />
        <el-option label="待接单" :value="1" />
        <el-option label="配送中" :value="2" />
        <el-option label="已完成" :value="3" />
        <el-option label="已取消" :value="4" />
      </el-select>
      <el-button type="primary" @click="loadData">搜索</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="orderNo" label="订单号" width="200" show-overflow-tooltip />
      <el-table-column prop="merchantName" label="商家" />
      <el-table-column prop="actualAmount" label="实付金额" width="100">
        <template #default="{ row }">¥{{ row.actualAmount }}</template>
      </el-table-column>
      <el-table-column prop="statusText" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">{{ row.statusText }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="下单时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="showDetail(row)">详情</el-button>
          <el-button v-if="row.status === 1" link type="success" @click="handleAccept(row)">接单配送</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination v-model:current-page="query.page" v-model:page-size="query.pageSize"
        :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" @change="loadData" />
    </div>

    <el-dialog v-model="detailVisible" title="订单详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单号">{{ detail.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detail.statusText }}</el-descriptions-item>
        <el-descriptions-item label="商家">{{ detail.merchantName }}</el-descriptions-item>
        <el-descriptions-item label="商品金额">¥{{ detail.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="配送费">¥{{ detail.deliveryFee }}</el-descriptions-item>
        <el-descriptions-item label="实付金额">¥{{ detail.actualAmount }}</el-descriptions-item>
        <el-descriptions-item label="下单时间" :span="2">{{ detail.createTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detail.remark || '无' }}</el-descriptions-item>
      </el-descriptions>

      <div class="order-items-section">
        <div class="section-title">商品列表</div>
        <div class="order-items-list">
          <div v-for="item in detail.items" :key="item.id" class="order-item">
            <div class="item-info">
              <span class="item-name">{{ item.dishName }}</span>
              <span class="item-quantity">x{{ item.quantity }}</span>
            </div>
            <div class="item-price">¥{{ item.dishPrice }}</div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { getOrderPage, getOrder, updateOrderStatus } from '@/api/order'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const detailVisible = ref(false)
const detail = ref({})

const query = reactive({ page: 1, pageSize: 10, status: null })

function getStatusType(status) {
  const types = { 0: 'info', 1: 'warning', 2: 'primary', 3: 'success', 4: 'danger' }
  return types[status] || 'info'
}

function formatTime(timeStr) {
  if (!timeStr) return '-'
  if (typeof timeStr === 'string' && timeStr.includes('T')) {
    return timeStr.replace('T', ' ').substring(0, 19)
  }
  return timeStr
}

async function loadData() {
  loading.value = true
  try {
    const data = await getOrderPage(query)
    tableData.value = data.records.map(item => ({
      ...item,
      createTime: formatTime(item.createTime)
    }))
    total.value = data.total
  } finally {
    loading.value = false
  }
}

async function showDetail(row) {
  document.activeElement?.blur()
  const data = await getOrder(row.id)
  detail.value = {
    ...data,
    createTime: formatTime(data.createTime)
  }
  detailVisible.value = true
}

async function handleAccept(row) {
  document.activeElement?.blur()
  await updateOrderStatus({ id: row.id, status: 2 })
  ElMessage.success('已接单，开始配送')
  loadData()
}

loadData()
</script>

<style scoped>
.order-items-section {
  margin-top: 20px;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
  padding-left: 8px;
  border-left: 3px solid #FF6B35;
}

.order-items-list {
  background: #FAFAFA;
  border-radius: 8px;
  padding: 4px 0;
}

.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #F0F0F0;
}

.order-item:last-child {
  border-bottom: none;
}

.item-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.item-name {
  font-size: 14px;
  color: #303133;
}

.item-quantity {
  font-size: 13px;
  color: #909399;
  background: #EBEEF5;
  padding: 2px 8px;
  border-radius: 4px;
}

.item-price {
  font-size: 14px;
  font-weight: 500;
  color: #FF6B35;
}
</style>
