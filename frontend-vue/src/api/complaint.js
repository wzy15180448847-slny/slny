import request from '@/utils/request'

export function getPendingComplaints(params) {
  return request({
    url: '/complaints/pending',
    method: 'get',
    params
  })
}

export function getProcessingComplaints(params) {
  return request({
    url: '/complaints/processing',
    method: 'get',
    params
  })
}

export function getComplaintDetail(id) {
  return request({
    url: `/complaints/${id}`,
    method: 'get'
  })
}

export function processComplaint(id, status, processResult) {
  return request({
    url: `/complaints/process/${id}`,
    method: 'post',
    params: { status, processResult }
  })
}

export function getAdminComplaints(params) {
  return request({
    url: '/admin/complaints',
    method: 'get',
    params
  })
}

export function arbitrateComplaint(id, data) {
  return request({
    url: `/admin/complaints/${id}/arbitrate`,
    method: 'put',
    data
  })
}