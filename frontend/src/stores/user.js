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
        const data = response.data  // LoginResponse 对象
        if (data && data.token) {
          this.token = data.token
          this.userId = data.userId
          this.username = data.username
          this.userType = data.userType
          this.isAuthenticated = true
          localStorage.setItem('token', data.token)
          return { success: true, message: '登录成功' }
        } else {
          return { success: false, message: data?.message || '用户名或密码错误' }
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