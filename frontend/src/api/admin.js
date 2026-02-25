import request from '@/utils/request'

// 获取后台统计数据
export function getAdminStats() {
  return request({
    url: '/admin/stats',
    method: 'get'
  })
}

// 获取停车场列表（管理员）
export function getAdminParkingLots(params) {
  return request({
    url: '/parking-lots',
    method: 'get',
    params
  })
}

// 创建停车场
export function createParkingLot(data) {
  return request({
    url: '/parking-lots',
    method: 'post',
    data
  })
}

// 更新停车场
export function updateParkingLot(id, data) {
  return request({
    url: `/parking-lots/${id}`,
    method: 'put',
    data
  })
}

// 删除停车场
export function deleteParkingLot(id) {
  return request({
    url: `/parking-lots/${id}`,
    method: 'delete'
  })
}

// 获取预约列表（管理员）
export function getAdminReservations(params) {
  return request({
    url: '/reservations/all',
    method: 'get',
    params
  })
}

// 更新预约状态
export function updateReservationStatus(id, data) {
  return request({
    url: `/reservations/${id}`,
    method: 'put',
    data
  })
}

// 获取会员列表
export function getMembers(params) {
  return request({
    url: '/members',
    method: 'get',
    params
  })
}

// 创建会员
export function createMember(data) {
  return request({
    url: '/members',
    method: 'post',
    data
  })
}

// 更新会员
export function updateMember(id, data) {
  return request({
    url: `/members/${id}`,
    method: 'put',
    data
  })
}

// 删除会员
export function deleteMember(id) {
  return request({
    url: `/members/${id}`,
    method: 'delete'
  })
}

// 管理员为指定用户创建会员
export function createMemberByAdmin(data) {
  return request({
    url: '/members/admin',
    method: 'post',
    data
  })
}

// 搜索用户（按用户名/手机号）
export function searchUser(keyword) {
  return request({
    url: '/members/search-user',
    method: 'get',
    params: { keyword }
  })
}
export function deleteReservation(id) {
  return request({
    url: `/reservations/${id}`,
    method: "delete"
  })
}
