import request from '@/utils/request'

export const qualificationApi = {
  getPendingList: (params) => {
    return request({
      url: '/agent-qualification/pending',
      method: 'get',
      params
    })
  },
  
  auditQualification: (id, auditStatus, auditRemark) => {
    return request({
      url: `/agent-qualification/audit/${id}`,
      method: 'post',
      params: {
        auditStatus,
        auditRemark
      }
    })
  }
}