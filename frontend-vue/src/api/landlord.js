import request from '@/utils/request'

export function getMyHouses(params) {
  return request({
    url: '/houses/my',
    method: 'get',
    params
  })
}

export function onlineHouse(id) {
  return request({
    url: `/houses/${id}/online`,
    method: 'post'
  })
}

export function offlineHouse(id) {
  return request({
    url: `/houses/${id}/offline`,
    method: 'post'
  })
}

export function getLandlordAppointments(params) {
  return request({
    url: '/appointment/landlord',
    method: 'get',
    params
  })
}

export function confirmAppointment(id) {
  return request({
    url: `/appointment/confirm/${id}`,
    method: 'put'
  })
}

export function rejectAppointment(id, reason) {
  return request({
    url: `/appointment/reject/${id}`,
    method: 'put',
    params: { reason }
  })
}

export function completeAppointment(id) {
  return request({
    url: `/appointment/complete/${id}`,
    method: 'put'
  })
}

export function createContract(data) {
  return request({
    url: '/lease',
    method: 'post',
    data
  })
}

export function getLandlordContracts(params) {
  return request({
    url: '/lease/page',
    method: 'get',
    params
  })
}

export function sendContract(id) {
  return request({
    url: `/lease/send/${id}`,
    method: 'post'
  })
}

export function exportContract(id) {
  return request({
    url: `/lease/export/${id}`,
    method: 'get',
    responseType: 'blob'
  })
}

export function generateBill(id) {
  return request({
    url: `/lease/bill/${id}`,
    method: 'post'
  })
}

export function getLandlordReminders() {
  return request({
    url: '/rent-reminder/landlord',
    method: 'get'
  })
}

export function getLandlordReminderSummary() {
  return request({
    url: '/rent-reminder/summary',
    method: 'get'
  })
}

export function sendReminder(id) {
  return request({
    url: `/rent-reminder/${id}/send`,
    method: 'post'
  })
}

export function sendCollection(id) {
  return request({
    url: `/rent-reminder/${id}/collection`,
    method: 'post'
  })
}

export function getLandlordRepairs(params) {
  return request({
    url: '/repairs/landlord',
    method: 'get',
    params
  })
}

export function acceptRepair(id) {
  return request({
    url: `/repairs/${id}/accept`,
    method: 'put'
  })
}

export function completeRepair(id) {
  return request({
    url: `/repairs/${id}/complete`,
    method: 'put'
  })
}

export function publishHouse(data) {
  return request({
    url: '/houses',
    method: 'post',
    data
  })
}