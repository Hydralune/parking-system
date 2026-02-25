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
        <router-link to="/admin/reservations" class="sidebar-item"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>预约管理</router-link>
        <router-link to="/admin/members" class="sidebar-item active"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/></svg>会员管理</router-link>
      </nav>
      <div class="sidebar-footer"><router-link to="/" class="sidebar-item"><svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/></svg>返回前台</router-link></div>
    </aside>

    <main class="main">
      <div class="main-header">
        <div><h1 class="main-title">会员管理</h1><p class="main-subtitle">管理用户会员信息与等级</p></div>
        <button class="btn-add" @click="showAddDialog">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
          添加会员
        </button>
      </div>

      <div class="table-card">
        <el-table :data="members" v-loading="loading" class="modern-table">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="userId" label="用户ID" width="90" />
          <el-table-column prop="username" label="用户名" width="120" />
          <el-table-column label="等级" width="120">
            <template #default="{ row }">
              <span class="level-badge" :class="`level-${row.memberLevel}`">{{ getLevelText(row.memberLevel) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="points" label="积分" width="100" />
          <el-table-column label="折扣率" width="100">
            <template #default="{ row }"><span class="price-tag">{{ (row.discountRate * 100).toFixed(0) }}%</span></template>
          </el-table-column>
          <el-table-column prop="validFrom" label="有效期开始" width="140" />
          <el-table-column prop="validTo" label="有效期结束" width="140" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <span class="status-badge" :class="row.validTo && new Date(row.validTo) > new Date() ? 'success' : 'danger'">
                {{ row.validTo && new Date(row.validTo) > new Date() ? '有效' : '过期' }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="160" fixed="right">
            <template #default="{ row }">
              <div class="action-btns">
                <button class="tbl-btn edit" @click="showEditDialog(row)">编辑</button>
                <button class="tbl-btn delete" @click="deleteMember(row.id)">删除</button>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <div class="table-footer">
          <el-pagination :current-page="pagination.current" :page-size="pagination.size" :page-sizes="[10,20,50]" :total="pagination.total" layout="total, sizes, prev, pager, next" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
        </div>
      </div>
    </main>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="520px" :before-close="handleClose">
      <el-form :model="memberForm" :rules="memberRules" ref="memberFormRef" label-width="100px" class="dialog-form">
        <el-form-item label="用户" prop="userId">
          <el-select
            v-model="memberForm.userId"
            filterable
            remote
            :remote-method="handleUserSearch"
            :loading="userSearchLoading"
            placeholder="输入用户名或手机号搜索"
            style="width:100%"
            :disabled="isEdit"
          >
            <el-option v-for="u in userOptions" :key="u.id" :label="`${u.username}（${u.phone || '无手机号'}）`" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="会员等级" prop="memberLevel">
          <el-select v-model="memberForm.memberLevel" placeholder="选择等级">
            <el-option label="普通会员" :value="1" />
            <el-option label="银卡会员" :value="2" />
            <el-option label="金卡会员" :value="3" />
            <el-option label="钻石会员" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="积分"><el-input-number v-model="memberForm.points" :min="0" /></el-form-item>
        <el-form-item label="折扣率">
          <el-slider v-model="memberForm.discountRate" :min="0.1" :max="1" :step="0.05" :format-tooltip="v => `${(v*100).toFixed(0)}%`" />
          <div class="discount-display">{{ (memberForm.discountRate * 100).toFixed(0) }}%</div>
        </el-form-item>
        <el-form-item label="有效期开始"><el-date-picker v-model="memberForm.validFrom" type="date" placeholder="选择日期" format="YYYY-MM-DD" value-format="YYYY-MM-DD" /></el-form-item>
        <el-form-item label="有效期结束"><el-date-picker v-model="memberForm.validTo" type="date" placeholder="选择日期" format="YYYY-MM-DD" value-format="YYYY-MM-DD" /></el-form-item>
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
import { getMembers, createMemberByAdmin, updateMember as apiUpdate, deleteMember as apiDelete, searchUser } from '@/api/admin'

export default {
  name: 'MemberManageView',
  setup() {
    const members = ref([])
    const pagination = reactive({ current: 1, size: 10, total: 0 })
    const loading = ref(false)
    const submitLoading = ref(false)
    const dialogVisible = ref(false)
    const isEdit = ref(false)
    const memberFormRef = ref(null)
    const memberForm = reactive({ id: null, userId: null, memberLevel: 1, points: 0, discountRate: 1.0, validFrom: null, validTo: null })
    const memberRules = { userId: [{ required: true, message: '请选择用户', trigger: 'change' }], memberLevel: [{ required: true, message: '请选择等级', trigger: 'change' }] }
    const dialogTitle = computed(() => isEdit.value ? '编辑会员' : '添加会员')
    const userOptions = ref([])
    const userSearchLoading = ref(false)

    const handleUserSearch = async (keyword) => {
      if (!keyword) return
      userSearchLoading.value = true
      try {
        const res = await searchUser(keyword)
        if (res.success) userOptions.value = res.data
      } finally { userSearchLoading.value = false }
    }

    const getLevelText = (l) => ({ 1: '普通会员', 2: '银卡会员', 3: '金卡会员', 4: '钻石会员' }[l] || '未知')

    const fetchMembers = async () => {
      loading.value = true
      try {
        const res = await getMembers({ current: pagination.current, size: pagination.size })
        if (res.success) { members.value = res.data.records || []; pagination.total = res.data.total || 0 }
        else ElMessage.error(res.message || '获取失败')
      } catch (e) { ElMessage.error('获取失败：' + e.message) } finally { loading.value = false }
    }

    const resetForm = () => Object.assign(memberForm, { id: null, userId: null, memberLevel: 1, points: 0, discountRate: 1.0, validFrom: null, validTo: null })
    const showAddDialog = () => { isEdit.value = false; resetForm(); dialogVisible.value = true }
    const showEditDialog = (row) => { isEdit.value = true; Object.assign(memberForm, row); dialogVisible.value = true }
    const handleClose = () => { dialogVisible.value = false; resetForm() }

    const submitForm = async () => {
      if (!memberFormRef.value) return
      await memberFormRef.value.validate(async (valid) => {
        if (!valid) return
        submitLoading.value = true
        try {
          const res = isEdit.value ? await apiUpdate(memberForm.id, memberForm) : await createMemberByAdmin(memberForm)
          if (res.success) { ElMessage.success(isEdit.value ? '更新成功' : '添加成功'); dialogVisible.value = false; resetForm(); fetchMembers() }
          else ElMessage.error(res.message || '操作失败')
        } catch (e) { ElMessage.error('操作失败：' + e.message) } finally { submitLoading.value = false }
      })
    }

    const deleteMember = async (id) => {
      try {
        await ElMessageBox.confirm('确定要删除这个会员吗？', '确认删除', { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' })
        const res = await apiDelete(id)
        if (res.success) { ElMessage.success('删除成功'); fetchMembers() }
        else ElMessage.error(res.message || '删除失败')
      } catch (e) { if (e !== 'cancel') ElMessage.error('删除失败：' + e.message) }
    }

    const handleSizeChange = (s) => { pagination.size = s; fetchMembers() }
    const handleCurrentChange = (c) => { pagination.current = c; fetchMembers() }
    fetchMembers()
    return { members, pagination, loading, submitLoading, dialogVisible, isEdit, memberFormRef, memberForm, memberRules, dialogTitle, getLevelText, showAddDialog, showEditDialog, handleClose, submitForm, deleteMember, handleSizeChange, handleCurrentChange, userOptions, userSearchLoading, handleUserSearch }
  }
}
</script>

<style scoped>
.level-badge { display: inline-block; padding: 3px 10px; border-radius: 100px; font-size: 12px; font-weight: 600; }
.level-1 { background: #f1f5f9; color: #64748b; }
.level-2 { background: #dbeafe; color: #2563eb; }
.level-3 { background: #fef3c7; color: #d97706; }
.level-4 { background: #ede9fe; color: #7c3aed; }
.discount-display { margin-top: 8px; font-size: 15px; font-weight: 700; color: var(--primary); }
</style>
