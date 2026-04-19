import request from '@/utils/request'

export function getWallet() {
  return request({
    url: '/wallet',
    method: 'get'
  })
}

export function getTransactions() {
  return request({
    url: '/wallet/transactions',
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

export function getPaymentRecords() {
  return request({
    url: '/payments',
    method: 'get'
  })
}