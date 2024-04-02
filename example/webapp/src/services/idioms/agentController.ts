// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** 此处后端没有提供注释 POST /api/v1/chat */
export async function chat(body: API.ChatAgentRequest, options?: { [key: string]: any }) {
  return request<API.ChatAgentResponse>('/api/v1/chat', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
