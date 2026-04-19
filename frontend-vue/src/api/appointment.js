import request from '@/utils/request'

export function createAppointment(data) {
  return request({
    url: '/appointments',
    method: 'post',
    data
  })
}

export function getMyAppointments(params) {
  return request({
    url: '/appointments/my',
    method: 'get',
    params
  })
}

export function cancelAppointment(id) {
  return request({
    url: `/appointments/${id}/cancel`,
    method: 'post'
  })
}

export function getAppointmentById(id) {
  return request({
    url: `/appointments/${id}`,
    method: 'get'
  })
}