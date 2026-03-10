<template>
  <div class="layout">
    <aside class="sidebar">
      <div class="logo">
        <div class="logo-icon">
          <svg viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 17.93c-3.95-.49-7-3.85-7-7.93 0-.62.08-1.21.21-1.79L9 15v1c0 1.1.9 2 2 2v1.93zm6.9-2.54c-.26-.81-1-1.39-1.9-1.39h-1v-3c0-.55-.45-1-1-1H8v-2h2c.55 0 1-.45 1-1V7h2c1.1 0 2-.9 2-2v-.41c2.93 1.19 5 4.06 5 7.41 0 2.08-.8 3.97-2.1 5.39z"/>
          </svg>
        </div>
        <span class="logo-text">美味外卖</span>
      </div>
      <el-menu :default-active="route.path" router background-color="transparent" text-color="#fff">
        <el-menu-item index="/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <span>数据概览</span>
        </el-menu-item>
        <el-menu-item index="/merchant">
          <el-icon><Shop /></el-icon>
          <span>商家管理</span>
        </el-menu-item>
        <el-menu-item index="/dish">
          <el-icon><Food /></el-icon>
          <span>菜品管理</span>
        </el-menu-item>
        <el-menu-item index="/order">
          <el-icon><List /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/user">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
      </el-menu>
      <div class="sidebar-footer">
        <span>美味外卖 © 2026</span>
      </div>
    </aside>
    <div class="main-container">
      <header class="header">
        <div class="header-left">
          <span class="page-title">{{ route.meta.title || '首页' }}</span>
        </div>
        <el-dropdown @command="handleCommand">
          <span class="user-dropdown">
            <el-avatar :size="32" class="user-avatar">
              {{ (userStore.userInfo?.realName || userStore.userInfo?.username || 'A').charAt(0).toUpperCase() }}
            </el-avatar>
            <span class="user-name">{{ userStore.userInfo?.realName || userStore.userInfo?.username }}</span>
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">
                <el-icon><SwitchButton /></el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </header>
      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { SwitchButton } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

onMounted(async () => {
  if (userStore.token && !userStore.userInfo) {
    try {
      await userStore.getInfo()
    } catch (e) {
      userStore.logout()
      router.push('/login')
    }
  }
})

function handleCommand(command) {
  if (command === 'logout') {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  }
}
</script>

<style scoped>
.sidebar-footer {
  padding: 16px;
  text-align: center;
  font-size: 11px;
  color: rgba(255,255,255,0.3);
  border-top: 1px solid rgba(255,255,255,0.06);
}

.header-left {
  flex: 1;
}

.page-title {
  font-size: 16px;
  font-weight: 600;
  color: #1F2937;
}

.user-dropdown {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 8px;
  border-radius: 8px;
  transition: background 0.2s;
}

.user-dropdown:hover {
  background: #F3F4F6;
}

.user-avatar {
  background: linear-gradient(135deg, #FF6B35 0%, #FF8F5E 100%);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
}

.user-name {
  font-size: 14px;
  color: #374151;
  font-weight: 500;
}
</style>
