import { defineStore } from 'pinia'
import { login, register } from '@/api/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    user: null,
    userId: null,
    username: '',
    userType: null, // 0-普通用户, 1-管理员
    isAuthenticated: false
  }),

  getters: {
    isLogin: (state) => !!state.token,
    isAdmin: (state) => state.userType === 1
  },

  actions: {
    async login(credentials) {
      try {
        const response = await login(credentials)
        if (response.data && response.data.success) {
          const { token, userId, username, userType } = response.data
          
          // 保存到状态
          this.token = token
          this.userId = userId
          this.username = username
          this.userType = userType
          this.isAuthenticated = true
          
          // 保存到本地存储
          localStorage.setItem('token', token)
          
          return { success: true, message: response.data.message }
        } else {
          return { success: false, message: (response.data && response.data.message) || '用户名或密码错误' }
        }
      } catch (error) {
        return { success: false, message: error.message || '登录失败' }
      }
    },

    async register(userData) {
      try {
        const response = await register(userData)
        if (response.success) {
          return { success: true, message: response.message, data: response.data }
        } else {
          return { success: false, message: response.message }
        }
      } catch (error) {
        return { success: false, message: error.message || '注册失败' }
      }
    },

    logout() {
      this.token = ''
      this.user = null
      this.userId = null
      this.username = ''
      this.userType = null
      this.isAuthenticated = false
      
      localStorage.removeItem('token')
    },

    initializeStore() {
      const token = localStorage.getItem('token')
      if (token) {
        this.token = token
        this.isAuthenticated = true
        // 注意：这里可能需要验证token的有效性
      }
    }
  }
})