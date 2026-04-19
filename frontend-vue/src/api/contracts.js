import request from '@/utils/request'

export function getMyContracts(params) {
  return request({
    url: '/api/lease/page',
    method: 'get',
    params
  })
}

export function signContract(id, userId, userType, signatureData) {
  return request({
    url: `/api/lease/sign/${id}`,
    method: 'put',
    params: { userId, userType, signatureData }
  })
}

export function terminateContract(id, reason) {
  return request({
    url: `/api/lease/terminate/${id}`,
    method: 'put',
    params: { reason }
  })
}

export function getContractById(id) {
  return request({
    url: `/api/lease/${id}`,
    method: 'get'
  })
}

export function createContract(data) {
  return request({
    url: '/api/lease',
    method: 'post',
    data
  })
}