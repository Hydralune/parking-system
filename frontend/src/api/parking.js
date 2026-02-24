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