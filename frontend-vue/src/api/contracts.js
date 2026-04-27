import request from '@/utils/request'

export function getMyContracts(params) {
  return request({
    url: '/lease/page',
    method: 'get',
    params
  })
}

export function signContract(id, userId, userType, signatureData) {
  return request({
    url: `/lease/sign/${id}`,
    method: 'put',
    params: { userId, userType, signatureData }
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