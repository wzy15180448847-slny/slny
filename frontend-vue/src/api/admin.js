import request from '@/utils/request'

export function getDashboardStats() {
  return request({
    url: '/admin/dashboard/stats',
    method: 'get'
  })
}

export function getHouseStatusDistribution() {
  return request({
    url: '/admin/dashboard/house-status',
    method: 'get'
  })
}

export function getMonthlyRevenueTrend() {
  return request({
    url: '/admin/dashboard/revenue-trend',
    method: 'get'
  })
}

export function getChartData() {
  return request({
    url: '/admin/dashboard/charts',
    method: 'get'
  })
}

export function getRecentLogs() {
  return request({
    url: '/admin/dashboard/logs',
    method: 'get'
  })
}

export function getUsers(params) {
  return request({
    url: '/admin/users',
    method: 'get',
    params
  })
}

export function updateUserStatus(id, status) {
  return request({
    url: `/admin/users/${id}/status`,
    method: 'put',
    data: { status }
  })
}

export function deleteUser(id) {
  return request({
    url: `/admin/users/${id}`,
    method: 'delete'
  })
}

export function getComplaints(params) {
  return request({
    url: '/admin/complaints',
    method: 'get',
    params
  })
}

export function arbitrateComplaint(id, data) {
  return request({
    url: `/admin/complaints/${id}/arbitrate`,
    method: 'put',
    data
  })
}