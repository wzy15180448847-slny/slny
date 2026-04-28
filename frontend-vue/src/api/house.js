import request from '@/utils/request'

export function getHouseList(params) {
  return request({
    url: '/houses',
    method: 'get',
    params
  })
}

export function getAllHouses() {
  return request({
    url: '/houses',
    method: 'get'
  })
}

export function getHouseDetail(id) {
  return request({
    url: `/houses/${id}`,
    method: 'get',
    params: {
      _t: Date.now()
    }
  })
}

export function publishHouse(data) {
  return request({
    url: '/houses',
    method: 'post',
    data
  })
}

export function updateHouse(id, data) {
  return request({
    url: `/houses/${id}`,
    method: 'put',
    data
  })
}

export function deleteHouse(id) {
  return request({
    url: `/houses/${id}`,
    method: 'delete'
  })
}

export function searchHouses(params) {
  return request({
    url: '/houses/search',
    method: 'get',
    params
  })
}

export function getFavorites(params) {
  return request({
    url: '/houses/favorites/list',
    method: 'get',
    params
  })
}

export function addFavorite(houseId) {
  return request({
    url: `/houses/${houseId}/favorite`,
    method: 'post'
  })
}

export function removeFavorite(houseId) {
  return request({
    url: `/houses/${houseId}/favorite`,
    method: 'delete'
  })
}

export function isFavorited(houseId) {
  return request({
    url: `/houses/${houseId}/favorite`,
    method: 'get'
  })
}

export function uploadHouseImages(houseId, data) {
  return request({
    url: `/houses/${houseId}/images`,
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function getPendingAuditList(params) {
  return request({
    url: '/houses/pending-audit',
    method: 'get',
    params
  })
}

export function getApprovedHouses(params) {
  return request({
    url: '/houses/approved',
    method: 'get',
    params
  })
}

export function getRejectedHouses(params) {
  return request({
    url: '/houses/rejected',
    method: 'get',
    params
  })
}

export function auditHouse(id, data) {
  return request({
    url: `/houses/${id}/audit`,
    method: 'post',
    params: data
  })
}

export function onlineHouse(id) {
  return request({
    url: `/houses/${id}/online`,
    method: 'post'
  })
}

export function offlineHouse(id) {
  return request({
    url: `/houses/${id}/offline`,
    method: 'post'
  })
}

export function getRecommendHouses(limit) {
  return request({
    url: '/houses/recommend',
    method: 'get',
    params: { limit }
  })
}

export function getLatestHouses(limit) {
  return request({
    url: '/houses/latest',
    method: 'get',
    params: { limit }
  })
}
