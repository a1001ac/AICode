// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 此处后端没有提供注释 GET /ai/get */
export async function getAiModelConfig(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getAiModelConfigParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseAiModelConfig>('/ai/get', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 此处后端没有提供注释 POST /ai/update */
export async function updateAiModelConfig(
  body: API.AiModelConfig,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>('/ai/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
