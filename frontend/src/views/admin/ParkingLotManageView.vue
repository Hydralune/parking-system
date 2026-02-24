<template>
  <div class="dashboard">
    <aside class="sidebar">
      <div class="sidebar-logo">
        <div class="logo-icon"><svg width="20" height="20" viewBox="0 0 24 24" fill="none"><rect x="2" y="3" width="20" height="18" rx="3" fill="white" fill-opacity="0.3" stroke="white" stroke-width="1.5"/><path d="M7 8h5.5a2.5 2.5 0 010 5H7V8z" fill="white"/><path d="M7 13h3v4H7v-4z" fill="white" opacity="0.7"/></svg></div>
        <span>智停管理</span>
      </div>
      <nav class="sidebar-nav">
        <router-link to="/admin" class="sidebar-item"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/></svg>概览</router-link>
        <router-link to="/admin/parking-lots" class="sidebar-item active"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>停车场管理</router-link>
        <router-link to="/admin/reservations" class="sidebar-item"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>预约管理</router-link>
        <router-link to="/admin/members" class="sidebar-item"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/></svg>会员管理</router-link>
      </nav>
      <div class="sidebar-footer"><router-link to="/" class="sidebar-item"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/></svg>返回前台</router-link></div>
    </aside>

    <main class="main">
      <div class="main-header">
        <div><h1 class="main-title">停车场管理</h1><p class="main-subtitle">管理所有停车场信息</p></div>
        <button class="btn-add" @click="showAddDialog">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
          添加停车场
        </button>
      </div>

      <div class="table-card">
        <el-table :data="parkingLots" v-loading="loading" class="modern-table">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="name" label="名称" min-width="160" />
          <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip />
          <el-table-column label="车位" width="120">
            <template #default="{ row }">
              <div class="spot-info"><span class="spot-avail">{{ row.availableSpots }}</span><span class="spot-sep">/</span><span class="spot-total">{{ row.totalSpots }}</span></div>
            </template>
          </el-table-column>
          <el-table-column label="时价" width="100">
            <template #default="{ row }"><span class="price-tag">¥{{ row.hourlyRate }}/h</span></template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }"><span class="status-badge" :class="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '营业中' : '已关闭' }}</span></template>
          </el-table-column>
          <el-table-column label="操作" width="160" fixed="right">
            <template #default="{ row }">
              <div class="action-btns">
                <button class="tbl-btn edit" @click="showEditDialog(row)">编辑</button>
                <button class="tbl-btn delete" @click="deleteParkingLot(row.id)">删除</button>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <div class="table-footer">
          <el-pagination :current-page="pagination.current" :page-size="pagination.size" :page-sizes="[10,20,50]" :total="pagination.total" layout="total, sizes, prev, pager, next" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
        </div>
      </div>
    </main>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px" :before-close="handleClose">
      <el-form :model="lotForm" :rules="lotRules" ref="lotFormRef" label-width="100px" class="dialog-form">
        <el-form-item label="名称" prop="name"><el-input v-model="lotForm.name" placeholder="停车场名称" /></el-form-item>
        <el-form-item label="地址" prop="address"><el-input v-model="lotForm.address" placeholder="详细地址" /></el-form-item>
        <el-form-item label="总车位" prop="totalSpots"><el-input-number v-model="lotForm.totalSpots" :min="1" /></el-form-item>
        <el-form-item label="时价(元)" prop="hourlyRate"><el-input-number v-model="lotForm.hourlyRate" :min="0" :step="0.5" :precision="2" /></el-form-item>
        <el-form-item label="开放时间"><el-time-picker v-model="lotForm.openingTime" format="HH:mm:ss" value-format="HH:mm:ss" placeholder="开放时间" /></el-form-item>
        <el-form-item label="关闭时间"><el-time-picker v-model="lotForm.closingTime" format="HH:mm:ss" value-format="HH:mm:ss" placeholder="关闭时间" /></el-form-item>
        <el-form-item label="联系电话"><el-input v-model="lotForm.phone" placeholder="联系电话" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="lotForm.description" type="textarea" :rows="3" placeholder="停车场描述" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="lotForm.status" :active-value="1" :inactive-value="0" active-text="营业中" inactive-text="已关闭" /></el-form-item>
      </el-form>
      <template #footer>
        <button class="dialog-btn cancel" @click="handleClose">取消</button>
        <button class="dialog-btn confirm" @click="submitForm" :disabled="submitLoading">{{ submitLoading ? '保存中...' : '保存' }}</button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getParkingLots, createParkingLot, updateParkingLot as apiUpdate, deleteParkingLot as apiDelete } from '@/api/admin'

export default {
  name: 'ParkingLotManageView',
  setup() {
    const parkingLots = ref([])
    const pagination = reactive({ current: 1, size: 10, total: 0 })
    const loading = ref(false)
    const submitLoading = ref(false)
    const dialogVisible = ref(false)
    const isEdit = ref(false)
    const lotFormRef = ref(null)
    const lotForm = reactive({ id: null, name: '', address: '', totalSpots: 100, availableSpots: 100, hourlyRate: 5.0, openingTime: '08:00:00', closingTime: '22:00:00', phone: '', description: '', status: 1, longitude: 0, latitude: 0 })
    const lotRules = { name: [{ required: true, message: '请输入名称', trigger: 'blur' }], address: [{ required: true, message: '请输入地址', trigger: 'blur' }] }
    const dialogTitle = computed(() => isEdit.value ? '编辑停车场' : '添加停车场')

    const fetchParkingLots = async () => {
      loading.value = true
      try {
        const res = await getParkingLots({ current: pagination.current, size: pagination.size })
        if (res.success) { parkingLots.value = res.data.records || []; pagination.total = res.data.total || 0 }
        else ElMessage.error(res.message || '获取失败')
      } catch (e) { ElMessage.error('获取失败：' + e.message) } finally { loading.value = false }
    }

    const resetForm = () => Object.assign(lotForm, { id: null, name: '', address: '', totalSpots: 100, availableSpots: 100, hourlyRate: 5.0, openingTime: '08:00:00', closingTime: '22:00:00', phone: '', description: '', status: 1, longitude: 0, latitude: 0 })
    const showAddDialog = () => { isEdit.value = false; resetForm(); dialogVisible.value = true }
    const showEditDialog = (row) => { isEdit.value = true; Object.assign(lotForm, row); dialogVisible.value = true }
    const handleClose = () => { dialogVisible.value = false; resetForm() }

    const submitForm = async () => {
      if (!lotFormRef.value) return
      await lotFormRef.value.validate(async (valid) => {
        if (!valid) return
        submitLoading.value = true
        try {
          const res = isEdit.value ? await apiUpdate(lotForm.id, lotForm) : await createParkingLot(lotForm)
          if (res.success) { ElMessage.success(isEdit.value ? '更新成功' : '添加成功'); dialogVisible.value = false; resetForm(); fetchParkingLots() }
          else ElMessage.error(res.message || '操作失败')
        } catch (e) { ElMessage.error('操作失败：' + e.message) } finally { submitLoading.value = false }
      })
    }

    const deleteParkingLot = async (id) => {
      try {
        await ElMessageBox.confirm('确定要删除这个停车场吗？', '确认删除', { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' })
        const res = await apiDelete(id)
        if (res.success) { ElMessage.success('删除成功'); fetchParkingLots() }
        else ElMessage.error(res.message || '删除失败')
      } catch (e) { if (e !== 'cancel') ElMessage.error('删除失败：' + e.message) }
    }

    const handleSizeChange = (s) => { pagination.size = s; fetchParkingLots() }
    const handleCurrentChange = (c) => { pagination.current = c; fetchParkingLots() }
    fetchParkingLots()
    return { parkingLots, pagination, loading, submitLoading, dialogVisible, isEdit, lotFormRef, lotForm, lotRules, dialogTitle, showAddDialog, showEditDialog, handleClose, submitForm, deleteParkingLot, handleSizeChange, handleCurrentChange }
  }
}
</script>

<style scoped>
/* styles loaded globally via main.js */
</style>
