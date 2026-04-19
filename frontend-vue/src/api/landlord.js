import request from '@/utils/request'

export function getMyHouses(params) {
  return request({
    url: '/houses/my',
    method: 'get',
    params
  })
}

export function updateHouseStatus(id, status) {
  return request({
    url: `/houses/${id}/status`,
    method: 'put',
    data: { status }
  })
}

export function getLandlordAppointments(params) {
  return request({
    url: '/appointments/landlord',
    method: 'get',
    params
  })
}

export function confirmAppointment(id) {
  return request({
    url: `/appointments/${id}/confirm`,
    method: 'post'
  })
}

export function rejectAppointment(id, data) {
  return request({
    url: `/appointments/${id}/reject`,
    method: 'post',
    data
  })
}

export function completeAppointment(id) {
  return request({
    url: `/appointments/${id}/complete`,
    method: 'post'
  })
}

export function createContract(data) {
  return request({
    url: '/contracts',
    method: 'post',
    data
  })
}

export function getLandlordContracts() {
  return request({
    url: '/contracts/landlord',
    method: 'get'
  })
}

export function sendContract(id) {
  return request({
    url: `/contracts/${id}/send`,
    method: 'post'
  })
}

export function exportContract(id) {
  return request({
    url: `/contracts/${id}/export`,
    method: 'get',
    responseType: 'blob'
  })
}

export function generateBill(id) {
  return request({
    url: `/contracts/${id}/bill`,
    method: 'post'
  })
}