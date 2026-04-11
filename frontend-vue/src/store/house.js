import { defineStore } from 'pinia'
import { getHouseList, getHouseDetail, searchHouses } from '@/api/house'

export const useHouseStore = defineStore('house', {
  state: () => ({
    houseList: [],
    currentHouse: null,
    searchParams: {
      city: '',
      district: '',
      houseType: '',
      minPrice: null,
      maxPrice: null,
      minArea: null,
      maxArea: null,
      rentWay: '',
      keyword: ''
    },
    pagination: {
      current: 1,
      size: 12,
      total: 0
    },
    loading: false
  }),

  getters: {
    hasMore: (state) => state.pagination.current * state.pagination.size < state.pagination.total
  },

  actions: {
    async fetchHouseList(params = {}) {
      this.loading = true
      try {
        const { data } = await getHouseList({
          current: this.pagination.current,
          size: this.pagination.size,
          ...this.searchParams,
          ...params
        })
        this.houseList = data.records
        this.pagination.total = data.total
        return Promise.resolve(data)
      } catch (error) {
        return Promise.reject(error)
      } finally {
        this.loading = false
      }
    },

    async fetchHouseDetail(id) {
      this.loading = true
      try {
        const { data } = await getHouseDetail(id)
        this.currentHouse = data
        return Promise.resolve(data)
      } catch (error) {
        return Promise.reject(error)
      } finally {
        this.loading = false
      }
    },

    async searchHouses(params) {
      this.loading = true
      this.searchParams = { ...this.searchParams, ...params }
      this.pagination.current = 1
      try {
        const { data } = await searchHouses({
          current: this.pagination.current,
          size: this.pagination.size,
          ...this.searchParams
        })
        this.houseList = data.records
        this.pagination.total = data.total
        return Promise.resolve(data)
      } catch (error) {
        return Promise.reject(error)
      } finally {
        this.loading = false
      }
    },

    updateSearchParams(params) {
      this.searchParams = { ...this.searchParams, ...params }
    },

    resetSearchParams() {
      this.searchParams = {
        city: '',
        district: '',
        houseType: '',
        minPrice: null,
        maxPrice: null,
        minArea: null,
        maxArea: null,
        rentWay: '',
        keyword: ''
      }
      this.pagination.current = 1
    },

    setPage(page) {
      this.pagination.current = page
    }
  }
})
