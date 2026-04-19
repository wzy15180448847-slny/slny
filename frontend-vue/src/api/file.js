import request from '@/utils/request'

export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/files/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function deleteFile(fileName) {
  return request({
    url: '/files/delete',
    method: 'delete',
    params: { fileName }
  })
}

export function getFileUrl(fileName) {
  return request({
    url: '/files/url',
    method: 'get',
    params: { fileName }
  })
}