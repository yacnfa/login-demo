import request from './request'

// 获取用户列表：GET /users?keyword=
export function getUserList(params) {
  return request({
    url: '/users',
    method: 'get',
    params
  })
}

// 新增用户：POST /users
export function addUser(data) {
  return request({
    url: '/users',
    method: 'post',
    data
  })
}

// 编辑用户：PUT /users/{id}
export function editUser(data) {
  return request({
    url: `/users/${data.id}`,
    method: 'put',
    data
  })
}

// 删除用户：DELETE /users/{id}
export function delUser(id) {
  return request({
    url: `/users/${id}`,
    method: 'delete'
  })
}

// 批量删除：POST /users/batch-delete  body: { ids: [...] }
export function batchDeleteUsers(ids) {
  return request({
    url: '/users/batch-delete',
    method: 'post',
    data: { ids }
  })
}
