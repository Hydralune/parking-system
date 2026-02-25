import request from '@/utils/request'

// 获取停车场列表
export function getParkingLots(params) {
  return request({
    url: '/parking-lots',
    method: 'get',
    params
  })
}

// 根据ID获取停车场详情
export function getParkingLotById(id) {
  return request({
    url: `/parking-lots/${id}`,
    method: 'get'
  })
}

// 获取停车场可用车位数量
export function getAvailableSpotsCount(id) {
  return request({
    url: `/parking-lots/${id}/available-spots`,
    method: 'get'
  })
}

// 获取停车场可用车位列表（支持区域筛选）
export function getAvailableSpots(id, zone) {
  return request({
    url: `/parking-lots/${id}/spots`,
    method: 'get',
    params: zone ? { zone } : {}
  })
}

// 获取停车场区域列表
export function getParkingZones(id) {
  return request({
    url: `/parking-lots/${id}/zones`,
    method: 'get'
  })
}