import { createStore } from 'vuex'
import apiClient from './api'

export const store = createStore({
  state: {
    auth: {
      isLoggedIn: false,
      user: null,
      token: null
    },
    routes: [],
    auditHistory: [],
    healthStatus: {},
    loading: false,
    error: null,
    notification: null
  },

  mutations: {
    setIsLoggedIn(state, isLoggedIn) {
      state.auth.isLoggedIn = isLoggedIn
    },
    setUser(state, user) {
      state.auth.user = user
    },
    setToken(state, token) {
      state.auth.token = token
      if (token) {
        localStorage.setItem('authToken', token)
      } else {
        localStorage.removeItem('authToken')
      }
    },
    setRoutes(state, routes) {
      state.routes = routes
    },
    addRoute(state, route) {
      state.routes.push(route)
    },
    updateRoute(state, updatedRoute) {
      const index = state.routes.findIndex(r => r.id === updatedRoute.id)
      if (index !== -1) {
        state.routes[index] = updatedRoute
      }
    },
    deleteRoute(state, routeId) {
      state.routes = state.routes.filter(r => r.id !== routeId)
    },
    setAuditHistory(state, audits) {
      state.auditHistory = audits
    },
    setHealthStatus(state, health) {
      state.healthStatus = health
    },
    setLoading(state, loading) {
      state.loading = loading
    },
    setError(state, error) {
      state.error = error
    },
    setNotification(state, notification) {
      state.notification = notification
    },
    clearNotification(state) {
      state.notification = null
    }
  },

  actions: {
    async login({ commit }, { token, user }) {
      commit('setToken', token)
      commit('setUser', user)
      commit('setIsLoggedIn', true)
    },

    logout({ commit }) {
      commit('setToken', null)
      commit('setUser', null)
      commit('setIsLoggedIn', false)
    },

    async fetchRoutes({ commit }) {
      commit('setLoading', true)
      try {
        const response = await apiClient.get('/api/admin/routes')
        commit('setRoutes', response.data)
        commit('setError', null)
      } catch (error) {
        commit('setError', error.response?.data?.message || 'Failed to fetch routes')
      } finally {
        commit('setLoading', false)
      }
    },

    async createRoute({ commit }, routeData) {
      commit('setLoading', true)
      try {
        const response = await apiClient.post('/api/admin/routes', routeData)
        commit('addRoute', response.data)
        commit('setNotification', { type: 'success', message: 'Route created successfully' })
        return response.data
      } catch (error) {
        const message = error.response?.data?.message || 'Failed to create route'
        commit('setError', message)
        commit('setNotification', { type: 'error', message })
        throw error
      } finally {
        commit('setLoading', false)
      }
    },

    async updateRoute({ commit }, { id, routeData }) {
      commit('setLoading', true)
      try {
        const response = await apiClient.put(`/api/admin/routes/${id}`, routeData)
        commit('updateRoute', response.data)
        commit('setNotification', { type: 'success', message: 'Route updated successfully' })
        return response.data
      } catch (error) {
        const message = error.response?.data?.message || 'Failed to update route'
        commit('setError', message)
        commit('setNotification', { type: 'error', message })
        throw error
      } finally {
        commit('setLoading', false)
      }
    },

    async deleteRoute({ commit }, id) {
      commit('setLoading', true)
      try {
        await apiClient.delete(`/api/admin/routes/${id}`)
        commit('deleteRoute', id)
        commit('setNotification', { type: 'success', message: 'Route deleted successfully' })
      } catch (error) {
        const message = error.response?.data?.message || 'Failed to delete route'
        commit('setError', message)
        commit('setNotification', { type: 'error', message })
        throw error
      } finally {
        commit('setLoading', false)
      }
    },

    async toggleRouteStatus({ commit }, { id, enable }) {
      commit('setLoading', true)
      try {
        const endpoint = enable ? 'enable' : 'disable'
        const response = await apiClient.post(`/api/admin/routes/${id}/${endpoint}`)
        commit('updateRoute', response.data)
        const message = enable ? 'Route enabled' : 'Route disabled'
        commit('setNotification', { type: 'success', message })
        return response.data
      } catch (error) {
        const message = error.response?.data?.message || 'Failed to toggle route status'
        commit('setError', message)
        commit('setNotification', { type: 'error', message })
        throw error
      } finally {
        commit('setLoading', false)
      }
    },

    async fetchAuditHistory({ commit }, routeId) {
      commit('setLoading', true)
      try {
        const response = await apiClient.get(`/api/admin/audit/routes/${routeId}`)
        commit('setAuditHistory', response.data)
        commit('setError', null)
      } catch (error) {
        commit('setError', error.response?.data?.message || 'Failed to fetch audit history')
      } finally {
        commit('setLoading', false)
      }
    },

    async fetchHealthStatus({ commit }) {
      commit('setLoading', true)
      try {
        const response = await apiClient.get('/api/admin/routes/health/status')
        const health = {}
        response.data.forEach(route => {
          health[route.id] = route.health
        })
        commit('setHealthStatus', health)
        commit('setError', null)
      } catch (error) {
        commit('setError', error.response?.data?.message || 'Failed to fetch health status')
      } finally {
        commit('setLoading', false)
      }
    }
  },

  getters: {
    isLoggedIn: state => state.auth.isLoggedIn,
    currentUser: state => state.auth.user,
    routes: state => state.routes,
    auditHistory: state => state.auditHistory,
    healthStatus: state => state.healthStatus,
    isLoading: state => state.loading,
    error: state => state.error,
    notification: state => state.notification
  }
})

export default store

