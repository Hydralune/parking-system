<template>
  <div class="page-layout">
    <HeaderNav />
    <div class="page-content">
      <div class="profile-wrap">
        <!-- Sidebar -->
        <div class="profile-sidebar">
          <div class="avatar-section">
            <div class="avatar-lg">{{ userStore.username?.charAt(0)?.toUpperCase() }}</div>
            <div class="avatar-name">{{ userStore.username }}</div>
            <div class="avatar-role">
              <span class="role-badge" :class="userStore.userType === 1 ? 'admin' : 'user'">
                {{ userStore.userType === 1 ? '管理员' : '普通用户' }}
              </span>
            </div>
          </div>
          <nav class="profile-nav">
            <button class="profile-nav-item active">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="8" r="4"/><path d="M4 20c0-4 3.6-7 8-7s8 3 8 7"/></svg>
              个人信息
            </button>
            <router-link to="/reservations" class="profile-nav-item">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
              我的预约
            </router-link>
            <router-link v-if="userStore.isAdmin" to="/admin" class="profile-nav-item">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/></svg>
              管理后台
            </router-link>
          </nav>
        </div>

        <!-- Main form -->
        <div class="profile-main">
          <div class="profile-card">
            <h2 class="profile-card-title">个人信息</h2>
            <p class="profile-card-desc">更新您的账户信息</p>

            <el-form :model="profileForm" :rules="profileRules" ref="profileFormRef" label-position="top" class="profile-form">
              <div class="form-row">
                <el-form-item label="用户名" prop="username">
                  <el-input v-model="profileForm.username" disabled size="large" />
                  <div class="field-hint">用户名不可修改</div>
                </el-form-item>
                <el-form-item label="手机号" prop="phone">
                  <el-input v-model="profileForm.phone" size="large" placeholder="请输入手机号" />
                </el-form-item>
              </div>
              <el-form-item label="账户类型">
                <div class="type-display">
                  <span class="role-badge" :class="userStore.userType === 1 ? 'admin' : 'user'">
                    {{ userStore.userType === 1 ? '管理员' : '普通用户' }}
                  </span>
                </div>
              </el-form-item>
              <div class="form-actions">
                <button class="btn-save" @click.prevent="handleUpdateProfile" :disabled="loading">
                  {{ loading ? '保存中...' : '保存更改' }}
                </button>
              </div>
            </el-form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import HeaderNav from '@/components/HeaderNav.vue'

export default {
  name: 'ProfileView',
  components: { HeaderNav },
  setup() {
    const userStore = useUserStore()
    const profileFormRef = ref(null)
    const loading = ref(false)
    const profileForm = reactive({ username: userStore.username, phone: '' })
    const profileRules = {
      phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }, { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }]
    }

    const handleUpdateProfile = async () => {
      if (!profileFormRef.value) return
      await profileFormRef.value.validate(async (valid) => {
        if (!valid) return
        loading.value = true
        try {
          await new Promise(r => setTimeout(r, 600))
          ElMessage.success('信息更新成功')
        } catch (e) {
          ElMessage.error('更新失败：' + e.message)
        } finally { loading.value = false }
      })
    }

    onMounted(() => { profileForm.username = userStore.username; profileForm.phone = '' })
    return { userStore, profileFormRef, profileForm, profileRules, loading, handleUpdateProfile }
  }
}
</script>

<style scoped>
.page-layout { min-height: 100vh; display: flex; flex-direction: column; background: var(--bg); }
.page-content { flex: 1; max-width: 1100px; margin: 0 auto; width: 100%; padding: 40px 24px 60px; }

.profile-wrap { display: flex; gap: 28px; align-items: flex-start; }

.profile-sidebar { width: 240px; flex-shrink: 0; }
.avatar-section { background: white; border-radius: var(--radius-lg); border: 1px solid var(--border); padding: 28px 20px; text-align: center; margin-bottom: 12px; }
.avatar-lg { width: 72px; height: 72px; border-radius: 50%; background: linear-gradient(135deg, var(--primary), var(--accent)); color: white; font-size: 28px; font-weight: 800; display: flex; align-items: center; justify-content: center; margin: 0 auto 12px; box-shadow: 0 8px 24px rgba(37,99,235,0.3); }
.avatar-name { font-size: 16px; font-weight: 700; color: var(--text-primary); margin-bottom: 8px; }
.role-badge { display: inline-block; padding: 3px 12px; border-radius: 100px; font-size: 12px; font-weight: 600; }
.role-badge.admin { background: #fee2e2; color: #dc2626; }
.role-badge.user { background: #dcfce7; color: #16a34a; }

.profile-nav { background: white; border-radius: var(--radius); border: 1px solid var(--border); padding: 8px; display: flex; flex-direction: column; gap: 2px; }
.profile-nav-item { display: flex; align-items: center; gap: 10px; padding: 10px 12px; border-radius: 8px; font-size: 14px; font-weight: 500; color: var(--text-secondary); text-decoration: none; cursor: pointer; border: none; background: none; width: 100%; text-align: left; transition: var(--transition); }
.profile-nav-item:hover { color: var(--primary); background: var(--primary-bg); }
.profile-nav-item.active { color: var(--primary); background: var(--primary-bg); font-weight: 600; }

.profile-main { flex: 1; min-width: 0; }
.profile-card { background: white; border-radius: var(--radius-lg); border: 1px solid var(--border); padding: 32px; }
.profile-card-title { font-size: 1.3rem; font-weight: 800; color: var(--text-primary); margin-bottom: 4px; }
.profile-card-desc { font-size: 14px; color: var(--text-muted); margin-bottom: 32px; }

.profile-form :deep(.el-form-item__label) { font-size: 13px; font-weight: 600; color: var(--text-secondary); margin-bottom: 6px; }
.profile-form :deep(.el-input__wrapper) { border-radius: 10px !important; box-shadow: 0 0 0 1px var(--border) !important; }
.profile-form :deep(.el-input__wrapper:hover) { box-shadow: 0 0 0 1px var(--primary-light) !important; }
.profile-form :deep(.el-input__wrapper.is-focus) { box-shadow: 0 0 0 2px rgba(37,99,235,0.2) !important; }
.form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
.field-hint { font-size: 12px; color: var(--text-muted); margin-top: 4px; }
.type-display { padding: 10px 0; }
.form-actions { margin-top: 8px; }
.btn-save { padding: 12px 32px; border-radius: 10px; background: linear-gradient(135deg, var(--primary), var(--accent)); color: white; font-size: 14px; font-weight: 700; border: none; cursor: pointer; box-shadow: 0 4px 12px rgba(37,99,235,0.3); transition: var(--transition); }
.btn-save:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 8px 20px rgba(37,99,235,0.4); }
.btn-save:disabled { opacity: 0.6; cursor: not-allowed; }

@media (max-width: 768px) {
  .profile-wrap { flex-direction: column; }
  .profile-sidebar { width: 100%; }
  .form-row { grid-template-columns: 1fr; }
}
</style>
