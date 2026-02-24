import request from '@/utils/request'

// 获取用户预约列表
export function getUserReservations(params) {
  return request({
    url: '/reservations',
    method: 'get',
    params
  })
}

// 创建预约
export function createReservation(data) {
  return request({
    url: '/reservations',
    method: 'post',
    data
  })
}

// 根据ID获取预约详情
export function getReservationById(id) {
  return request({
    url: `/reservations/${id}`,
    method: 'get'
  })
}

// 取消预约
export function cancelReservation(id) {
  return request({
    url: `/reservations/${id}/cancel`,
    method: 'put'
  })
}