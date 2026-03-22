import client from './client';

import type { WeightUnit } from '@/types/domain';

interface UserInfo {
  id: number;
  email: string;
  nickname: string;
  weightUnit: WeightUnit;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  user: UserInfo;
}

export async function register(data: {
  email: string;
  password: string;
  nickname: string;
  weightUnit: WeightUnit;
}): Promise<AuthResponse> {
  const res = await client.post<AuthResponse>('/auth/register', data);
  return res.data;
}

export async function login(data: {
  email: string;
  password: string;
}): Promise<AuthResponse> {
  const res = await client.post<AuthResponse>('/auth/login', data);
  return res.data;
}

export async function refresh(refreshToken: string): Promise<AuthResponse> {
  const res = await client.post<AuthResponse>('/auth/refresh', {
    refreshToken,
  });
  return res.data;
}

export async function logout(): Promise<void> {
  await client.post('/auth/logout');
}
