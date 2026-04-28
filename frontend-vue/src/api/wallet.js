import request from '@/utils/request'

export function getWallet() {
  return request({
    url: '/wallet/me',
    method: 'get'
  })
}

export function getTransactions() {
  return request({
    url: '/wallet/me/transactions',
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

export function withdraw(data) {
  return request({
    url: '/wallet/withdraw',
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
    url: `/payment/payments`,
    method: 'get',
    params: { userId }
  })
}