import request from '@/utils/request'

export function getHouseList(params) {
  return request({
    url: '/houses',
    method: 'get',
    params
  })
}

export function getHouseDetail(id) {
  return request({
    url: `/houses/${id}`,
    method: 'get'
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

export function getMyHouses(params) {
  return request({
    url: '/houses/my',
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

export function getFavorites(params) {
  return request({
    url: '/houses/favorites',
    method: 'get',
    params
  })
}

export function incrementViewCount(id) {
  return request({
    url: `/houses/${id}/view`,
    method: 'post'
  })
}
