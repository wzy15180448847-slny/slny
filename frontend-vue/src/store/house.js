import { defineStore } from 'pinia'
import { getHouseList, getHouseDetail, searchHouses, publishHouse, uploadHouseImages } from '@/api/house'

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
    },

    async publishHouse(formData, fileList) {
      const rentTypeMapping = {
        'ENTIRE': 1,
        'SHARED': 2,
        'SINGLE_ROOM': 3
      }

      const houseData = {
        title: formData.title,
        rentPrice: Number(formData.price),
        rentWay: rentTypeMapping[formData.rentType] || 1,
        houseType: formData.houseType,
        area: Number(formData.area),
        floor: Number(formData.floor),
        totalFloor: Number(formData.totalFloors),
        orientation: formData.orientation,
        city: formData.city,
        district: formData.district,
        community: formData.community,
        address: formData.address,
        facilities: JSON.stringify(formData.amenities),
        description: formData.description,
        contactName: formData.contactName,
        contactPhone: formData.contactPhone
      }

      const response = await publishHouse(houseData)
      const houseId = response.data.id

      if (fileList && fileList.length > 0) {
        const formData = new FormData()
        fileList.forEach((file, index) => {
          formData.append('files', file.raw, file.name)
        })
        await uploadHouseImages(houseId, formData)
      }

      return response
    }
  }
})
