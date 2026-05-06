import request from '@/utils/request'

// 管理员仪表盘
export function getDashboardStats() {
  return request({
    url: '/admin/dashboard/stats',
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

// 管理员用户管理
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

export function updateCreditScore(id, score) {
  return request({
    url: `/admin/users/${id}/credit`,
    method: 'put',
    data: { score }
  })
}

// 管理员投诉仲裁
export function getComplaints(params) {
  return request({
    url: '/admin/complaints',
    method: 'get',
    params
  })
}

export function arbitrateComplaint(id, status, processResult) {
  return request({
    url: `/admin/complaints/${id}/arbitrate`,
    method: 'put',
    data: { status, processResult }
  })
}

// 合同管理
export function getAdminContracts(params) {
  return request({
    url: '/lease/admin',
    method: 'get',
    params
  })
}

export function exportContract(id) {
  return request({
    url: `/lease/export/${id}`,
    method: 'get',
    responseType: 'blob'
  })
}

export function searchLogs(params) {
  return request({
    url: '/admin/dashboard/logs/search',
    method: 'get',
    params
  })
}

export function getTodayLoginStats() {
  return request({
    url: '/admin/dashboard/logs/today-stats',
    method: 'get'
  })
}
