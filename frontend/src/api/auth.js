import request from '@/utils/request'

// 用户登录
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

// 用户注册
export function register(data) {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

// 检查用户名是否可用
export function checkUsername(username) {
  return request({
    url: `/auth/check-username/${username}`,
    method: 'get'
  })
}

// 检查手机号是否可用
export function checkPhone(phone) {
  return request({
    url: `/auth/check-phone/${phone}`,
    method: 'get'
  })
}