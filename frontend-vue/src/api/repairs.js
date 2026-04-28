import request from '@/utils/request'

export function getMyRepairs(params) {
  return request({
    url: '/repairs/my',
    method: 'get',
    params
  })
}

export function createRepair(data) {
  return request({
    url: '/repairs',
    method: 'post',
    data
  })
}

export function cancelRepair(id) {
  return request({
    url: `/repairs/${id}/cancel`,
    method: 'put'
  })
}

export function evaluateRepair(id, data) {
  return request({
    url: `/repairs/${id}/evaluate`,
    method: 'put',
    data
  })
}
