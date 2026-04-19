import request from '@/utils/request'

export function getDashboardStats() {
  return request({
    url: '/api/admin/dashboard/overview',
    method: 'get'
  })
}

export function getHouseStatusDistribution() {
  return request({
    url: '/api/admin/dashboard/house-status',
    method: 'get'
  })
}

export function getMonthlyRevenueTrend() {
  return request({
    url: '/api/admin/dashboard/revenue-trend',
    method: 'get'
  })
}

export function getUsers(params) {
  return request({
    url: '/api/admin/users',
    method: 'get',
    params
  })
}

export function updateUserStatus(id, status) {
  return request({
    url: `/api/admin/users/${id}/status`,
    method: 'put',
    data: { status }
  })
}

export function deleteUser(id) {
  return request({
    url: `/api/admin/users/${id}`,
    method: 'delete'
  })
}

export function getComplaints(params) {
  return request({
    url: '/api/admin/complaints',
    method: 'get',
    params
  })
}

export function arbitrateComplaint(id, data) {
  return request({
    url: `/api/admin/complaints/${id}/arbitrate`,
    method: 'put',
    data
  })
}
