import request from '@/utils/request'

export function getMyContracts() {
  return request({
    url: '/contracts/my',
    method: 'get'
  })
}

export function signContract(id, data) {
  return request({
    url: `/contracts/${id}/sign`,
    method: 'post',
    data
  })
}

export function terminateContract(id, data) {
  return request({
    url: `/contracts/${id}/terminate`,
    method: 'post',
    data
  })
}

export function getContractById(id) {
  return request({
    url: `/contracts/${id}`,
    method: 'get'
  })
}