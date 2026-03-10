<template>
  <div class="page-card">
    <div class="card-header">
      <span class="title">菜品管理</span>
      <el-button type="primary" @click="openDialog()">新增菜品</el-button>
    </div>

    <div class="search-bar">
      <el-select v-model="query.merchantId" placeholder="选择商家" clearable style="width: 200px" @change="onMerchantChange">
        <el-option v-for="m in merchants" :key="m.id" :label="m.name" :value="m.id" />
      </el-select>
      <el-select v-model="query.categoryId" :placeholder="query.merchantId ? '选择分类' : '请先选择商家'" :disabled="!query.merchantId" clearable style="width: 150px">
        <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
      </el-select>
      <el-input v-model="query.keyword" placeholder="菜品名称" clearable style="width: 200px" />
      <el-select v-model="query.status" placeholder="状态" clearable style="width: 120px">
        <el-option label="上架" :value="1" />
        <el-option label="下架" :value="0" />
      </el-select>
      <el-button type="primary" @click="loadData">搜索</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="图片" width="80">
        <template #default="{ row }">
          <el-image v-if="row.image" :src="row.image" style="width: 50px; height: 50px; border-radius: 4px;" fit="cover" />
          <div v-else class="image-placeholder">
            <span>🍽️</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="菜品名称" />
      <el-table-column prop="price" label="价格" width="100">
        <template #default="{ row }">¥{{ row.price }}</template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="80" />
      <el-table-column prop="sales" label="销量" width="80" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
          <el-button link :type="row.status === 1 ? 'warning' : 'success'" @click="toggleStatus(row)">
            {{ row.status === 1 ? '下架' : '上架' }}
          </el-button>
          <el-popconfirm title="确定删除该菜品？" confirm-button-text="删除" cancel-button-text="取消" confirm-button-type="danger" :icon="WarningFilled" icon-color="#FAAD14" :width="200" @confirm="handleDelete(row.id)">
            <template #reference>
              <el-button link type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination v-model:current-page="query.page" v-model:page-size="query.pageSize"
        :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" @change="loadData" />
    </div>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑菜品' : '新增菜品'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="商家" prop="merchantId">
          <el-select v-model="form.merchantId" placeholder="选择商家" style="width: 100%" @change="onFormMerchantChange">
            <el-option v-for="m in merchants" :key="m.id" :label="m.name" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="选择分类" style="width: 100%">
            <el-option v-for="c in formCategories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="图片">
          <el-upload
            class="image-uploader"
            :show-file-list="false"
            :http-request="handleUpload"
            accept="image/*"
          >
            <el-image v-if="form.image" :src="form.image" class="uploaded-image" fit="cover" />
            <div v-else class="upload-placeholder">
              <el-icon><Plus /></el-icon>
              <span>点击上传</span>
            </div>
          </el-upload>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="form.stock" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, WarningFilled } from '@element-plus/icons-vue'
import { getDishPage, saveDish, updateDish, updateDishStatus, deleteDish } from '@/api/dish'
import { getMerchantPage } from '@/api/merchant'
import { getCategoryList } from '@/api/category'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref()
const merchants = ref([])
const categories = ref([])
const formCategories = ref([])

const query = reactive({ page: 1, pageSize: 10, merchantId: null, categoryId: null, keyword: '', status: null })
const form = reactive({ id: null, merchantId: null, categoryId: null, name: '', image: '', price: null, stock: 999, description: '' })
const rules = {
  merchantId: [{ required: true, message: '请选择商家', trigger: 'change' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  name: [{ required: true, message: '请输入菜品名称', trigger: 'blur' }],
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '价格必须大于0', trigger: 'blur' }
  ]
}

onMounted(async () => {
  const data = await getMerchantPage({ page: 1, pageSize: 100 })
  merchants.value = data.records
  loadData()
})

async function loadData() {
  loading.value = true
  try {
    const data = await getDishPage(query)
    tableData.value = data.records
    total.value = data.total
  } finally {
    loading.value = false
  }
}

async function onMerchantChange() {
  query.categoryId = null
  if (query.merchantId) {
    categories.value = await getCategoryList(query.merchantId)
  } else {
    categories.value = []
  }
  loadData()
}

async function onFormMerchantChange() {
  form.categoryId = null
  if (form.merchantId) {
    formCategories.value = await getCategoryList(form.merchantId)
  } else {
    formCategories.value = []
  }
}

// 图片上传处理 - 转为 base64
function handleUpload(options) {
  const file = options.file
  const reader = new FileReader()
  reader.onload = (e) => {
    form.image = e.target.result
  }
  reader.readAsDataURL(file)
}

async function openDialog(row) {
  // 移除按钮焦点
  document.activeElement?.blur()
  if (row) {
    Object.assign(form, { ...row, image: row.image || '' })
    if (row.merchantId) {
      formCategories.value = await getCategoryList(row.merchantId)
    }
  } else {
    Object.assign(form, { id: null, merchantId: null, categoryId: null, name: '', image: '', price: null, stock: 999, description: '' })
    formCategories.value = []
  }
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (form.id) {
      await updateDish(form)
    } else {
      await saveDish(form)
    }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadData()
  } finally {
    submitting.value = false
  }
}

async function toggleStatus(row) {
  // 移除按钮焦点
  document.activeElement?.blur()
  await updateDishStatus({ id: row.id, status: row.status === 1 ? 0 : 1 })
  ElMessage.success('操作成功')
  loadData()
}

async function handleDelete(id) {
  await deleteDish(id)
  ElMessage.success('删除成功')
  loadData()
}
</script>

<style scoped>
.image-uploader {
  width: 120px;
  height: 120px;
}
.image-uploader :deep(.el-upload) {
  width: 120px;
  height: 120px;
  border: 2px dashed #E5E7EB;
  border-radius: 12px;
  cursor: pointer;
  overflow: hidden;
  transition: border-color 0.2s;
}
.image-uploader :deep(.el-upload:hover) {
  border-color: #FF6B35;
}
.uploaded-image {
  width: 120px;
  height: 120px;
}
.upload-placeholder {
  width: 120px;
  height: 120px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #9CA3AF;
  gap: 6px;
}
.upload-placeholder .el-icon {
  font-size: 28px;
  color: #D1D5DB;
}
.image-placeholder {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  background: linear-gradient(135deg, #FFF3ED 0%, #FFE4D6 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}
</style>
