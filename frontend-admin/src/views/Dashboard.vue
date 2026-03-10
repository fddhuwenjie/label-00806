<template>
  <div>
    <div class="stat-cards">
      <div class="stat-card primary">
        <div class="stat-icon">📦</div>
        <div class="stat-title">今日订单</div>
        <div class="stat-value">{{ stats.todayOrders || 0 }}</div>
        <div class="stat-footer">实时更新</div>
      </div>
      <div class="stat-card success">
        <div class="stat-icon">💰</div>
        <div class="stat-title">今日营业额</div>
        <div class="stat-value">¥{{ stats.todayAmount || '0.00' }}</div>
        <div class="stat-footer">实时更新</div>
      </div>
      <div class="stat-card warning">
        <div class="stat-icon">📋</div>
        <div class="stat-title">总订单数</div>
        <div class="stat-value">{{ stats.totalOrders || 0 }}</div>
        <div class="stat-footer">累计统计</div>
      </div>
      <div class="stat-card info">
        <div class="stat-icon">💵</div>
        <div class="stat-title">总营业额</div>
        <div class="stat-value">¥{{ stats.totalAmount || '0.00' }}</div>
        <div class="stat-footer">累计统计</div>
      </div>
      <div class="stat-card primary">
        <div class="stat-icon">👥</div>
        <div class="stat-title">用户总数</div>
        <div class="stat-value">{{ stats.totalUsers || 0 }}</div>
        <div class="stat-footer">注册用户</div>
      </div>
      <div class="stat-card success">
        <div class="stat-icon">🏪</div>
        <div class="stat-title">商家总数</div>
        <div class="stat-value">{{ stats.totalMerchants || 0 }}</div>
        <div class="stat-footer">入驻商家</div>
      </div>
    </div>

    <div class="page-card">
      <div class="card-header">
        <span class="title">快捷操作</span>
      </div>
      <div class="quick-actions">
        <div class="action-item" @click="$router.push('/order')">
          <div class="action-icon" style="background: #FFF3ED; color: #FF6B35;">📦</div>
          <span>处理订单</span>
        </div>
        <div class="action-item" @click="$router.push('/merchant')">
          <div class="action-icon" style="background: #F0FFF0; color: #52C41A;">🏪</div>
          <span>商家管理</span>
        </div>
        <div class="action-item" @click="$router.push('/dish')">
          <div class="action-icon" style="background: #E6F7FF; color: #1890FF;">🍜</div>
          <span>菜品管理</span>
        </div>
        <div class="action-item" @click="$router.push('/user')">
          <div class="action-icon" style="background: #FFFBE6; color: #FAAD14;">👥</div>
          <span>用户管理</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onActivated } from 'vue'
import { getStatistics } from '@/api/dashboard'

const stats = ref({})

const fetchStatistics = async () => {
  try {
    stats.value = await getStatistics()
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

onMounted(() => {
  fetchStatistics()
})

onActivated(() => {
  fetchStatistics()
})
</script>

<style scoped>
.quick-actions {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 24px 16px;
  border-radius: 12px;
  border: 1px solid #E5E7EB;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
  color: #374151;
  font-weight: 500;
}

.action-item:hover {
  border-color: #FF6B35;
  background: #FFF9F5;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 53, 0.1);
}

.action-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
}
</style>
