import axios from 'axios';
import { useAuthStore } from '@/store/authStore';

export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  error?: {
    code: string;
    message: string;
    details?: { field: string; message: string }[];
  };
}

const client = axios.create({
  baseURL: '/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
});

client.interceptors.request.use((config) => {
  // Don't send auth token for auth endpoints (register, login, refresh)
  const isAuthEndpoint = config.url?.startsWith('/auth/');
  if (!isAuthEndpoint) {
    const token = useAuthStore.getState().accessToken;
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
  }
  return config;
});

client.interceptors.response.use(
  (response) => {
    // Unwrap ApiResponse wrapper: { success, data, error } → data
    if (response.data && typeof response.data === 'object' && 'success' in response.data) {
      response.data = response.data.data;
    }
    return response;
  },
  async (error) => {
    const original = error.config;
    if ((error.response?.status === 401 || error.response?.status === 403) && !original._retry) {
      original._retry = true;
      const refreshToken = useAuthStore.getState().refreshToken;
      if (refreshToken) {
        try {
          const res = await axios.post('/api/v1/auth/refresh', {
            refreshToken,
          });
          const { accessToken, refreshToken: newRefresh } = res.data.data!;
          const existingUser = useAuthStore.getState().user;
          if (existingUser) {
            useAuthStore.getState().setAuth({
              accessToken,
              refreshToken: newRefresh,
              user: existingUser,
            });
          }
          original.headers.Authorization = `Bearer ${accessToken}`;
          return client(original);
        } catch {
          useAuthStore.getState().clearAuth();
          window.location.href = '/auth';
        }
      } else {
        useAuthStore.getState().clearAuth();
        window.location.href = '/auth';
      }
    }
    return Promise.reject(error);
  },
);

export default client;
