import request from '@/utils/request'

export function getWallet(userId) {
  return request({
    url: `/wallet/${userId}`,
    method: 'get'
  })
}

export function getTransactions(userId) {
  return request({
    url: `/wallet/${userId}/transactions`,
    method: 'get'
  })
}

export function recharge(data) {
  return request({
    url: '/wallet/recharge',
    method: 'post',
    data
  })
}

export function payRent(data) {
  return request({
    url: '/wallet/pay-rent',
    method: 'post',
    data
  })
}

export function getPaymentRecords(userId) {
  return request({
    url: `/payments`,
    method: 'get',
    params: { userId }
  })
}