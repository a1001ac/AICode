// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 此处后端没有提供注释 GET /view/total */
export async function getTotalViews(options?: { [key: string]: any }) {
  return request<API.BaseResponseLong>('/view/total', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 GET /view/trend */
export async function getViewTrend(options?: { [key: string]: any }) {
  return request<API.BaseResponseListView>('/view/trend', {
    method: 'GET',
    ...(options || {}),
  })
}
