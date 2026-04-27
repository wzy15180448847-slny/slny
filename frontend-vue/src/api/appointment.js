import request from '@/utils/request'

export function createAppointment(data) {
  return request({
    url: '/appointment',
    method: 'post',
    data
  })
}

export function getMyAppointments(params) {
  return request({
    url: '/appointment/page',
    method: 'get',
    params
  })
}

export function cancelAppointment(id, reason) {
  return request({
    url: `/appointment/cancel/${id}`,
    method: 'put',
    params: { reason }
  })
}

export function getAppointmentById(id) {
  return request({
    url: `/appointment/${id}`,
    method: 'get'
  })
}

export function confirmAppointment(id) {
  return request({
    url: `/appointment/confirm/${id}`,
    method: 'put'
  })
}

export function completeAppointment(id) {
  return request({
    url: `/appointment/complete/${id}`,
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