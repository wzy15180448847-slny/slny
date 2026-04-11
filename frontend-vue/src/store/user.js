import { defineStore } from 'pinia'
import { login, logout, getUserInfo } from '@/api/auth'
import { getToken, setToken, removeToken } from '@/utils/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken() || '',
    userInfo: null,
    roles: [],
    permissions: []
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    username: (state) => state.userInfo?.username || '',
    nickname: (state) => state.userInfo?.nickname || '',
    userType: (state) => state.userInfo?.userType || '',
    avatar: (state) => state.userInfo?.avatar || ''
  },

  actions: {
    async login(loginForm) {
      try {
        const { data } = await login(loginForm)
        this.token = data.token
        setToken(data.token)
        await this.getUserInfo()
        return Promise.resolve(data)
      } catch (error) {
        return Promise.reject(error)
      }
    },

    async getUserInfo() {
      try {
        const { data } = await getUserInfo()
        this.userInfo = data
        this.roles = data.roles || []
        this.permissions = data.permissions || []
        return Promise.resolve(data)
      } catch (error) {
        return Promise.reject(error)
      }
    },

    async logout() {
      try {
        await logout()
      } finally {
        this.resetState()
      }
    },

    resetState() {
      this.token = ''
      this.userInfo = null
      this.roles = []
      this.permissions = []
      removeToken()
    },

    initializeUser() {
      if (this.token) {
        this.getUserInfo().catch(() => {
          this.resetState()
        })
      }
    }
  },

  persist: {
    key: 'user-store',
    storage: localStorage,
    paths: ['token']
  }
})
