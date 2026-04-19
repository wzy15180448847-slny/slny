import request from '@/utils/request'

export function createAppointment(data) {
  return request({
    url: '/api/appointment',
    method: 'post',
    data
  })
}

export function getMyAppointments(params) {
  return request({
    url: '/api/appointment/page',
    method: 'get',
    params
  })
}

export function cancelAppointment(id, reason) {
  return request({
    url: `/api/appointment/cancel/${id}`,
    method: 'put',
    params: { reason }
  })
}

export function getAppointmentById(id) {
  return request({
    url: `/api/appointment/${id}`,
    method: 'get'
  })
}

export function confirmAppointment(id) {
  return request({
    url: `/api/appointment/confirm/${id}`,
    method: 'put'
  })
}

export function completeAppointment(id) {
  return request({
    url: `/api/appointment/complete/${id}`,
    method: 'put'
  })
}

export function rejectAppointment(id, reason) {
  return request({
    url: `/api/appointment/reject/${id}`,
    method: 'put',
    params: { reason }
  })
}