// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 此处后端没有提供注释 GET /email/get */
export async function getEmailConfig(options?: { [key: string]: any }) {
  return request<API.BaseResponseEmailConfig>('/email/get', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /email/update */
export async function updateEmailConfig(
  body: API.EmailConfigUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>('/email/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
