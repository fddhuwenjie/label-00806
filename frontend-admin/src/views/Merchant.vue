<template>
  <div class="page-card">
    <div class="card-header">
      <span class="title">商家管理</span>
      <el-button type="primary" :icon="Plus" @click="openDialog()">新增商家</el-button>
    </div>

    <div class="search-bar">
      <el-input v-model="query.keyword" placeholder="商家名称" clearable style="width: 200px" @keyup.enter="loadData" />
      <el-select v-model="query.status" placeholder="状态" clearable style="width: 120px">
        <el-option label="营业中" :value="1" />
        <el-option label="休息中" :value="0" />
      </el-select>
      <el-button type="primary" :loading="loading" @click="loadData">搜索</el-button>
      <el-button @click="resetQuery">重置</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column type="selection" width="50" />
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="商家名称" min-width="150" show-overflow-tooltip />
      <el-table-column prop="phone" label="联系电话" width="130" />
      <el-table-column prop="minPrice" label="起送价" width="100">
        <template #default="{ row }">¥{{ row.minPrice?.toFixed(2) || '0.00' }}</template>
      </el-table-column>
      <el-table-column prop="deliveryFee" label="配送费" width="100">
        <template #default="{ row }">¥{{ row.deliveryFee?.toFixed(2) || '0.00' }}</template>
      </el-table-column>
      <el-table-column prop="monthlySales" label="月销量" width="100" sortable />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-switch
            v-model="row.status"
            :active-value="1"
            :inactive-value="0"
            :loading="row.statusLoading"
            @change="toggleStatus(row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openCategoryDialog(row)">分类</el-button>
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
          <el-popconfirm title="确定删除该商家？删除后不可恢复" confirm-button-text="删除" cancel-button-text="取消" confirm-button-type="danger" :icon="WarningFilled" icon-color="#FAAD14" :width="220" @confirm="handleDelete(row.id)">
            <template #reference>
              <el-button link type="danger" :loading="row.deleteLoading">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
      <template #empty>
        <el-empty description="暂无商家数据">
          <el-button type="primary" @click="openDialog()">新增商家</el-button>
        </el-empty>
      </template>
    </el-table>

    <div class="pagination">
      <el-pagination v-model:current-page="query.page" v-model:page-size="query.pageSize"
        :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper" @change="loadData" />
    </div>

    <!-- 商家表单弹窗 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑商家' : '新增商家'" width="500px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" :disabled="submitting">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入商家名称" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="Logo">
          <el-upload
            class="logo-uploader"
            :show-file-list="false"
            :http-request="handleLogoUpload"
            accept="image/*"
          >
            <el-image v-if="form.logo" :src="form.logo" class="uploaded-logo" fit="cover" />
            <div v-else class="logo-placeholder">
              <el-icon><Plus /></el-icon>
              <span>上传Logo</span>
            </div>
          </el-upload>
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" maxlength="11" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入商家地址" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="起送价" prop="minPrice">
          <el-input-number v-model="form.minPrice" :min="0" :max="9999" :precision="2" placeholder="0.00" />
          <span class="form-tip">元</span>
        </el-form-item>
        <el-form-item label="配送费" prop="deliveryFee">
          <el-input-number v-model="form.deliveryFee" :min="0" :max="99" :precision="2" placeholder="0.00" />
          <span class="form-tip">元</span>
        </el-form-item>
        <el-form-item label="配送时间" prop="deliveryTime">
          <el-input v-model="form.deliveryTime" placeholder="如：30-45分钟" />
        </el-form-item>
        <el-form-item label="商家描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入商家描述" maxlength="500" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false" :disabled="submitting">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          {{ submitting ? '提交中...' : '确定' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 分类管理弹窗 -->
    <el-dialog v-model="categoryDialogVisible" title="分类管理" width="500px" class="category-dialog">
      <div class="category-header">
        <div class="merchant-info">
          <span class="label">当前商家</span>
          <span class="name">{{ currentMerchantName }}</span>
        </div>
        <el-button type="primary" size="small" :icon="Plus" @click="openCategoryForm()">新增分类</el-button>
      </div>
      
      <div v-loading="categoryLoading" class="category-list-wrapper">
        <div v-if="categories.length === 0" class="category-empty">
          <el-empty description="暂无分类，点击上方按钮添加" :image-size="80" />
        </div>
        <div v-else class="category-list">
          <div v-for="(item, index) in categories" :key="item.id" class="category-item">
            <div class="category-info">
              <span class="category-index">{{ index + 1 }}</span>
              <span class="category-name">{{ item.name }}</span>
              <el-tag size="small" type="info" class="category-sort">排序: {{ item.sortOrder }}</el-tag>
            </div>
            <div class="category-actions">
              <el-button link type="primary" size="small" @click="openCategoryForm(item)">编辑</el-button>
              <el-popconfirm title="确定删除该分类？" confirm-button-text="删除" cancel-button-text="取消" confirm-button-type="danger" :icon="WarningFilled" icon-color="#FAAD14" :width="200" @confirm="handleDeleteCategory(item.id)">
                <template #reference>
                  <el-button link type="danger" size="small">删除</el-button>
                </template>
              </el-popconfirm>
            </div>
          </div>
        </div>
      </div>

      <!-- 分类表单 -->
      <el-dialog v-model="categoryFormVisible" :title="categoryForm.id ? '编辑分类' : '新增分类'" width="400px" append-to-body>
        <el-form ref="categoryFormRef" :model="categoryForm" :rules="categoryRules" label-width="80px">
          <el-form-item label="名称" prop="name">
            <el-input v-model="categoryForm.name" placeholder="请输入分类名称" maxlength="20" />
          </el-form-item>
          <el-form-item label="排序" prop="sortOrder">
            <el-input-number v-model="categoryForm.sortOrder" :min="0" :max="999" />
            <span class="form-tip">数值越小越靠前</span>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="categoryFormVisible = false">取消</el-button>
          <el-button type="primary" :loading="categorySubmitting" @click="handleCategorySubmit">确定</el-button>
        </template>
      </el-dialog>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElNotification } from 'element-plus'
import { Plus, WarningFilled } from '@element-plus/icons-vue'
import { getMerchantPage, saveMerchant, updateMerchant, updateMerchantStatus, deleteMerchant } from '@/api/merchant'
import { getCategoryList, saveCategory, updateCategory, deleteCategory } from '@/api/category'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref()

const query = reactive({ page: 1, pageSize: 10, keyword: '', status: null })
const form = reactive({ 
  id: null, 
  name: '', 
  logo: '',
  phone: '', 
  address: '', 
  minPrice: 0, 
  deliveryFee: 0,
  deliveryTime: '',
  description: ''
})

const rules = {
  name: [
    { required: true, message: '请输入商家名称', trigger: 'blur' },
    { min: 2, max: 50, message: '名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^(1[3-9]\d{9}|0\d{2,3}-?\d{7,8})$/, message: '请输入正确的手机号或座机号', trigger: 'blur' }
  ],
  minPrice: [{ required: true, message: '请输入起送价', trigger: 'blur' }],
  deliveryFee: [{ required: true, message: '请输入配送费', trigger: 'blur' }]
}

// 分类相关
const categoryDialogVisible = ref(false)
const categoryFormVisible = ref(false)
const categoryLoading = ref(false)
const categorySubmitting = ref(false)
const categories = ref([])
const currentMerchantId = ref(null)
const currentMerchantName = ref('')
const categoryFormRef = ref()
const categoryForm = reactive({ id: null, merchantId: null, name: '', sortOrder: 0 })
const categoryRules = { 
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { max: 20, message: '分类名称不能超过20个字符', trigger: 'blur' }
  ] 
}

// 重置查询条件
function resetQuery() {
  query.keyword = ''
  query.status = null
  query.page = 1
  loadData()
}

async function loadData() {
  loading.value = true
  try {
    const data = await getMerchantPage(query)
    tableData.value = (data.records || []).map(item => ({
      ...item,
      statusLoading: false,
      deleteLoading: false
    }))
    total.value = data.total || 0
  } catch (error) {
    ElMessage.error('加载数据失败，请重试')
  } finally {
    loading.value = false
  }
}

function openDialog(row) {
  // 移除按钮焦点
  document.activeElement?.blur()
  if (row) {
    Object.assign(form, {
      id: row.id,
      name: row.name,
      logo: row.logo || '',
      phone: row.phone,
      address: row.address,
      minPrice: row.minPrice,
      deliveryFee: row.deliveryFee,
      deliveryTime: row.deliveryTime || '',
      description: row.description || ''
    })
  } else {
    Object.assign(form, { 
      id: null, 
      name: '', 
      logo: '',
      phone: '', 
      address: '', 
      minPrice: 0, 
      deliveryFee: 0,
      deliveryTime: '',
      description: ''
    })
  }
  dialogVisible.value = true
}

async function handleSubmit() {
  try {
    await formRef.value.validate()
  } catch {
    ElMessage.warning('请检查表单填写是否正确')
    return
  }
  
  submitting.value = true
  try {
    if (form.id) {
      await updateMerchant(form)
      ElNotification.success({ title: '成功', message: '商家信息已更新' })
    } else {
      await saveMerchant(form)
      ElNotification.success({ title: '成功', message: '商家添加成功' })
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    // 错误已在 request 拦截器中处理
  } finally {
    submitting.value = false
  }
}

async function toggleStatus(row) {
  const newStatus = row.status
  const oldStatus = newStatus === 1 ? 0 : 1
  row.statusLoading = true
  
  try {
    await updateMerchantStatus({ id: row.id, status: newStatus })
    ElMessage.success(newStatus === 1 ? '商家已开启营业' : '商家已设为休息')
  } catch (error) {
    // 恢复原状态
    row.status = oldStatus
  } finally {
    row.statusLoading = false
  }
}

async function handleDelete(id) {
  const row = tableData.value.find(item => item.id === id)
  if (row) row.deleteLoading = true
  
  try {
    await deleteMerchant(id)
    ElNotification.success({ title: '成功', message: '商家已删除' })
    // 如果当前页只有一条数据且不是第一页，则跳转到上一页
    if (tableData.value.length === 1 && query.page > 1) {
      query.page--
    }
    loadData()
  } finally {
    if (row) row.deleteLoading = false
  }
}

// 分类管理
async function openCategoryDialog(row) {
  // 移除按钮焦点
  document.activeElement?.blur()
  currentMerchantId.value = row.id
  currentMerchantName.value = row.name
  categoryDialogVisible.value = true
  await loadCategories()
}

async function loadCategories() {
  categoryLoading.value = true
  try {
    categories.value = await getCategoryList(currentMerchantId.value) || []
  } catch (error) {
    ElMessage.error('加载分类失败')
    categories.value = []
  } finally {
    categoryLoading.value = false
  }
}

function openCategoryForm(row) {
  // 移除按钮焦点
  document.activeElement?.blur()
  if (row) {
    Object.assign(categoryForm, row)
  } else {
    Object.assign(categoryForm, { id: null, merchantId: currentMerchantId.value, name: '', sortOrder: 0 })
  }
  categoryFormVisible.value = true
}

async function handleCategorySubmit() {
  try {
    await categoryFormRef.value.validate()
  } catch {
    return
  }
  
  categorySubmitting.value = true
  categoryForm.merchantId = currentMerchantId.value
  
  try {
    if (categoryForm.id) {
      await updateCategory(categoryForm)
    } else {
      await saveCategory(categoryForm)
    }
    ElMessage.success('操作成功')
    categoryFormVisible.value = false
    loadCategories()
  } finally {
    categorySubmitting.value = false
  }
}

async function handleDeleteCategory(id) {
  try {
    await deleteCategory(id)
    ElMessage.success('分类已删除')
    loadCategories()
  } catch (error) {
    // 错误已处理
  }
}

// Logo上传处理 - 转为 base64
function handleLogoUpload(options) {
  const file = options.file
  const reader = new FileReader()
  reader.onload = (e) => {
    form.logo = e.target.result
  }
  reader.readAsDataURL(file)
}

loadData()
</script>

<style scoped>
.logo-uploader {
  width: 100px;
  height: 100px;
}
.logo-uploader :deep(.el-upload) {
  width: 100px;
  height: 100px;
  border: 2px dashed #E5E7EB;
  border-radius: 12px;
  cursor: pointer;
  overflow: hidden;
  transition: border-color 0.2s;
}
.logo-uploader :deep(.el-upload:hover) {
  border-color: #FF6B35;
}
.uploaded-logo {
  width: 100px;
  height: 100px;
}
.logo-placeholder {
  width: 100px;
  height: 100px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #9CA3AF;
  font-size: 12px;
  gap: 4px;
}
.logo-placeholder .el-icon {
  font-size: 24px;
  color: #D1D5DB;
}

/* 分类管理弹窗样式 */
.category-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #F0F0F0;
}

.merchant-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.merchant-info .label {
  font-size: 13px;
  color: #909399;
}

.merchant-info .name {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.category-list-wrapper {
  min-height: 200px;
}

.category-empty {
  padding: 40px 0;
}

.category-list {
  background: #FAFAFA;
  border-radius: 8px;
  overflow: hidden;
}

.category-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  background: #fff;
  border-bottom: 1px solid #F0F0F0;
  transition: background 0.2s;
}

.category-item:hover {
  background: #FAFBFC;
}

.category-item:last-child {
  border-bottom: none;
}

.category-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.category-index {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #FF6B35 0%, #FF8F5A 100%);
  color: #fff;
  font-size: 12px;
  font-weight: 600;
  border-radius: 6px;
}

.category-name {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.category-sort {
  font-size: 12px;
}

.category-actions {
  display: flex;
  gap: 8px;
}
</style>
