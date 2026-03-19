import axios from 'axios';
import { useAuthStore } from '@/store/authStore';

const client = axios.create({
  baseURL: '/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
});

client.interceptors.request.use((config) => {
  const token = useAuthStore.getState().accessToken;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

client.interceptors.response.use(
  (response) => response,
  async (error) => {
    const original = error.config;
    if (error.response?.status === 401 && !original._retry) {
      original._retry = true;
      const refreshToken = useAuthStore.getState().refreshToken;
      if (refreshToken) {
        try {
          const res = await axios.post('/api/v1/auth/refresh', {
            refreshToken,
          });
          const { accessToken, refreshToken: newRefresh, user } = res.data;
          useAuthStore.getState().setAuth({
            accessToken,
            refreshToken: newRefresh,
            user,
          });
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
