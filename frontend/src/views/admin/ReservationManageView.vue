<template>
  <div class="dashboard">
    <aside class="sidebar">
      <div class="sidebar-logo">
        <div class="logo-icon"><svg width="20" height="20" viewBox="0 0 24 24" fill="none"><rect x="2" y="3" width="20" height="18" rx="3" fill="white" fill-opacity="0.3" stroke="white" stroke-width="1.5"/><path d="M7 8h5.5a2.5 2.5 0 010 5H7V8z" fill="white"/><path d="M7 13h3v4H7v-4z" fill="white" opacity="0.7"/></svg></div>
        <span>智停管理</span>
      </div>
      <nav class="sidebar-nav">
        <router-link to="/admin" class="sidebar-item"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/></svg>概览</router-link>
        <router-link to="/admin/parking-lots" class="sidebar-item"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>停车场管理</router-link>
        <router-link to="/admin/reservations" class="sidebar-item active"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>预约管理</router-link>
        <router-link to="/admin/members" class="sidebar-item"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/></svg>会员管理</router-link>
      </nav>
      <div class="sidebar-footer"><router-link to="/" class="sidebar-item"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/></svg>返回前台</router-link></div>
    </aside>

    <main class="main">
      <div class="main-header">
        <div><h1 class="main-title">预约管理</h1><p class="main-subtitle">查看和管理所有预约记录</p></div>
      </div>

      <div class="table-card">
        <el-table :data="reservations" v-loading="loading" class="modern-table">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="userId" label="用户ID" width="90" />
          <el-table-column prop="parkingLotName" label="停车场" min-width="160" show-overflow-tooltip />
          <el-table-column prop="spotNumber" label="车位号" width="100" />
          <el-table-column prop="startTime" label="开始时间" width="160" />
          <el-table-column prop="endTime" label="结束时间" width="160" />
          <el-table-column label="费用" width="100">
            <template #default="{ row }"><span class="price-tag">¥{{ row.totalFee || '-' }}</span></template>
          </el-table-column>
          <el-table-column label="状态" width="110">
            <template #default="{ row }">
              <span class="status-badge" :class="getStatusClass(row.status)">{{ getStatusText(row.status) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <div class="action-btns">
                <button class="tbl-btn edit" @click="showEditDialog(row)">修改状态</button>
                <button class="tbl-btn delete" @click="deleteReservation(row.id)">删除</button>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <div class="table-footer">
          <el-pagination :current-page="pagination.current" :page-size="pagination.size" :page-sizes="[10,20,50]" :total="pagination.total" layout="total, sizes, prev, pager, next" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
        </div>
      </div>
    </main>

    <el-dialog title="修改预约状态" v-model="dialogVisible" width="400px">
      <el-form :model="editForm" label-width="80px" class="dialog-form">
        <el-form-item label="预约ID"><el-input :value="editForm.id" disabled /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editForm.status" placeholder="选择状态">
            <el-option label="待确认" :value="0" />
            <el-option label="已确认" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已取消" :value="3" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <button class="dialog-btn cancel" @click="dialogVisible = false">取消</button>
        <button class="dialog-btn confirm" @click="submitEdit" :disabled="submitLoading">{{ submitLoading ? '保存中...' : '保存' }}</button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminReservations as getReservations, updateReservationStatus as updateReservation, deleteReservation as apiDelete } from '@/api/admin'

export default {
  name: 'ReservationManageView',
  setup() {
    const reservations = ref([])
    const pagination = reactive({ current: 1, size: 10, total: 0 })
    const loading = ref(false)
    const submitLoading = ref(false)
    const dialogVisible = ref(false)
    const editForm = reactive({ id: null, status: 0 })

    const getStatusText = (s) => ({ 0: '待确认', 1: '已确认', 2: '已完成', 3: '已取消' }[s] || '未知')
    const getStatusClass = (s) => ({ 0: 'warning', 1: 'info', 2: 'success', 3: 'danger' }[s] || 'info')

    const fetchReservations = async () => {
      loading.value = true
      try {
        const res = await getReservations({ current: pagination.current, size: pagination.size })
        if (res.success) { reservations.value = res.data.records || []; pagination.total = res.data.total || 0 }
        else ElMessage.error(res.message || '获取失败')
      } catch (e) { ElMessage.error('获取失败：' + e.message) } finally { loading.value = false }
    }

    const showEditDialog = (row) => { Object.assign(editForm, { id: row.id, status: row.status }); dialogVisible.value = true }

    const submitEdit = async () => {
      submitLoading.value = true
      try {
        const res = await updateReservation(editForm.id, { status: editForm.status })
        if (res.success) { ElMessage.success('更新成功'); dialogVisible.value = false; fetchReservations() }
        else ElMessage.error(res.message || '更新失败')
      } catch (e) { ElMessage.error('更新失败：' + e.message) } finally { submitLoading.value = false }
    }

    const deleteReservation = async (id) => {
      try {
        await ElMessageBox.confirm('确定要删除这条预约记录吗？', '确认删除', { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' })
        const res = await apiDelete(id)
        if (res.success) { ElMessage.success('删除成功'); fetchReservations() }
        else ElMessage.error(res.message || '删除失败')
      } catch (e) { if (e !== 'cancel') ElMessage.error('删除失败：' + e.message) }
    }

    const handleSizeChange = (s) => { pagination.size = s; fetchReservations() }
    const handleCurrentChange = (c) => { pagination.current = c; fetchReservations() }
    fetchReservations()
    return { reservations, pagination, loading, submitLoading, dialogVisible, editForm, getStatusText, getStatusClass, showEditDialog, submitEdit, deleteReservation, handleSizeChange, handleCurrentChange }
  }
}
</script>

<style scoped>
/* styles loaded globally via main.js */
</style>
