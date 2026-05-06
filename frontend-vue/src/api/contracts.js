import request from '@/utils/request'

export function getMyContracts(params) {
  return request({
    url: '/lease/my',
    method: 'get',
    params
  })
}

export function signContract(id, data) {
  return request({
    url: `/lease/sign/${id}`,
    method: 'put',
    data: data
  })
}

export function terminateContract(id, reason) {
  return request({
    url: `/lease/terminate/${id}`,
    method: 'put',
    params: { reason }
  })
}

export function getContractById(id) {
  return request({
    url: `/lease/${id}`,
    method: 'get'
  })
}

export function createContract(data) {
  return request({
    url: '/lease',
    method: 'post',
    data
  })
}

export function exportContract(id) {
  return request({
    url: `/lease/export/${id}`,
    method: 'get',
    responseType: 'blob'
  })
}