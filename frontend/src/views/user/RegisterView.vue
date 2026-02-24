<template>
  <div class="auth-page">
    <div class="auth-left">
      <div class="auth-left-content">
        <router-link to="/" class="auth-logo">
          <div class="logo-icon-wrap">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none">
              <rect x="2" y="3" width="20" height="18" rx="3" fill="white" fill-opacity="0.3" stroke="white" stroke-width="1.5"/>
              <path d="M7 8h5.5a2.5 2.5 0 010 5H7V8z" fill="white"/>
              <path d="M7 13h3v4H7v-4z" fill="white" opacity="0.7"/>
            </svg>
          </div>
          <span>智停系统</span>
        </router-link>
        <h2>加入我们</h2>
        <p>注册账户，开启智能停车新体验</p>
        <div class="auth-features">
          <div class="auth-feature" v-for="f in features" :key="f">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M20 6L9 17l-5-5"/></svg>
            {{ f }}
          </div>
        </div>
      </div>
    </div>

    <div class="auth-right">
      <div class="auth-form-wrap">
        <div class="auth-form-header">
          <h1>创建账户</h1>
          <p>已有账号？<router-link to="/login">立即登录</router-link></p>
        </div>

        <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef" class="auth-form">
          <el-form-item prop="username">
            <label class="form-label">用户名</label>
            <el-input v-model="registerForm.username" placeholder="3-20个字符" size="large" prefix-icon="User" />
          </el-form-item>
          <el-form-item prop="phone">
            <label class="form-label">手机号</label>
            <el-input v-model="registerForm.phone" placeholder="请输入手机号" size="large" prefix-icon="Phone" />
          </el-form-item>
          <el-form-item prop="password">
            <label class="form-label">密码</label>
            <el-input v-model="registerForm.password" type="password" placeholder="至少6位" size="large" prefix-icon="Lock" show-password />
          </el-form-item>
          <el-form-item prop="confirmPassword">
            <label class="form-label">确认密码</label>
            <el-input v-model="registerForm.confirmPassword" type="password" placeholder="再次输入密码" size="large" prefix-icon="Lock" show-password @keyup.enter="handleRegister" />
          </el-form-item>
          <el-form-item>
            <button class="btn-submit" :class="{ loading }" @click.prevent="handleRegister" :disabled="loading">
              <span v-if="!loading">注册</span>
              <span v-else class="loading-dots">注册中<i></i><i></i><i></i></span>
            </button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

export default {
  name: 'RegisterView',
  setup() {
    const router = useRouter()
    const userStore = useUserStore()
    const registerFormRef = ref(null)
    const loading = ref(false)

    const registerForm = reactive({ username: '', phone: '', password: '', confirmPassword: '' })

    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== registerForm.password) callback(new Error('两次密码不一致'))
      else callback()
    }

    const registerRules = {
      username: [{ required: true, message: '请输入用户名', trigger: 'blur' }, { min: 3, max: 20, message: '3-20个字符', trigger: 'blur' }],
      phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }, { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }],
      password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '至少6位', trigger: 'blur' }],
      confirmPassword: [{ required: true, message: '请确认密码', trigger: 'blur' }, { validator: validateConfirmPassword, trigger: 'blur' }]
    }

    const features = ['免费注册，快速开始', '实时车位查询', '在线预约停车', '会员积分奖励']

    const handleRegister = async () => {
      if (!registerFormRef.value) return
      await registerFormRef.value.validate(async (valid) => {
        if (!valid) return
        loading.value = true
        try {
          const result = await userStore.register({ username: registerForm.username, phone: registerForm.phone, password: registerForm.password })
          if (result.success) {
            ElMessage.success(result.message)
            router.push('/login')
          } else {
            ElMessage.error(result.message)
          }
        } catch (e) {
          ElMessage.error('注册失败：' + e.message)
        } finally {
          loading.value = false
        }
      })
    }

    return { registerFormRef, registerForm, registerRules, loading, features, handleRegister }
  }
}
</script>

<style scoped>
.auth-page { display: flex; min-height: 100vh; }
.auth-left {
  flex: 1; background: linear-gradient(135deg, #7c3aed 0%, var(--primary) 50%, var(--accent) 100%);
  display: flex; align-items: center; justify-content: center; padding: 60px 48px;
  position: relative; overflow: hidden;
}
.auth-left::before { content: ''; position: absolute; width: 500px; height: 500px; background: rgba(255,255,255,0.05); border-radius: 50%; top: -200px; right: -150px; }
.auth-left::after { content: ''; position: absolute; width: 300px; height: 300px; background: rgba(255,255,255,0.05); border-radius: 50%; bottom: -100px; left: -80px; }
.auth-left-content { position: relative; z-index: 1; max-width: 360px; }
.auth-logo { display: flex; align-items: center; gap: 10px; text-decoration: none; margin-bottom: 48px; }
.logo-icon-wrap { width: 40px; height: 40px; background: rgba(255,255,255,0.2); border-radius: 12px; display: flex; align-items: center; justify-content: center; }
.auth-logo span { font-size: 20px; font-weight: 700; color: white; }
.auth-left-content h2 { font-size: 2rem; font-weight: 800; color: white; margin-bottom: 12px; }
.auth-left-content p { font-size: 15px; color: rgba(255,255,255,0.75); margin-bottom: 36px; line-height: 1.7; }
.auth-features { display: flex; flex-direction: column; gap: 14px; }
.auth-feature { display: flex; align-items: center; gap: 10px; font-size: 14px; color: rgba(255,255,255,0.9); }
.auth-feature svg { flex-shrink: 0; color: #86efac; }
.auth-right { flex: 1; display: flex; align-items: center; justify-content: center; padding: 60px 48px; background: white; }
.auth-form-wrap { width: 100%; max-width: 400px; }
.auth-form-header { margin-bottom: 32px; }
.auth-form-header h1 { font-size: 1.8rem; font-weight: 800; color: var(--text-primary); margin-bottom: 8px; }
.auth-form-header p { font-size: 14px; color: var(--text-secondary); }
.auth-form-header a { color: var(--primary); font-weight: 600; text-decoration: none; }
.auth-form-header a:hover { text-decoration: underline; }
.auth-form :deep(.el-form-item) { margin-bottom: 16px; }
.auth-form :deep(.el-form-item__content) { flex-direction: column; align-items: flex-start; }
.form-label { font-size: 13px; font-weight: 600; color: var(--text-secondary); margin-bottom: 6px; display: block; }
.auth-form :deep(.el-input__wrapper) { border-radius: 10px !important; box-shadow: 0 0 0 1px var(--border) !important; transition: var(--transition); }
.auth-form :deep(.el-input__wrapper:hover) { box-shadow: 0 0 0 1px var(--primary-light) !important; }
.auth-form :deep(.el-input__wrapper.is-focus) { box-shadow: 0 0 0 2px rgba(37,99,235,0.25) !important; }
.btn-submit { width: 100%; padding: 14px; border-radius: 10px; background: linear-gradient(135deg, #7c3aed, var(--primary)); color: white; font-size: 15px; font-weight: 700; border: none; cursor: pointer; box-shadow: 0 6px 20px rgba(124,58,237,0.35); transition: var(--transition); margin-top: 4px; }
.btn-submit:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 10px 28px rgba(124,58,237,0.45); }
.btn-submit:disabled { opacity: 0.7; cursor: not-allowed; }
.loading-dots i { display: inline-block; width: 4px; height: 4px; border-radius: 50%; background: white; margin-left: 3px; animation: pulse 1s infinite; }
.loading-dots i:nth-child(2) { animation-delay: 0.2s; }
.loading-dots i:nth-child(3) { animation-delay: 0.4s; }
@media (max-width: 768px) { .auth-left { display: none; } .auth-right { padding: 40px 24px; } }
</style>
