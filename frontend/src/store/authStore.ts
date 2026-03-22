import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { WeightUnit } from '@/types/domain';

export interface AuthUser {
  id: number;
  email: string;
  nickname: string;
  weightUnit: WeightUnit;
}

interface AuthState {
  accessToken: string | null;
  refreshToken: string | null;
  user: AuthUser | null;
  setAuth: (data: { accessToken: string; refreshToken: string; user: AuthUser }) => void;
  setUser: (user: AuthUser) => void;
  clearAuth: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      accessToken: null,
      refreshToken: null,
      user: null,
      setAuth: (data) =>
        set({
          accessToken: data.accessToken,
          refreshToken: data.refreshToken,
          user: data.user,
        }),
      setUser: (user) => set({ user }),
      clearAuth: () =>
        set({ accessToken: null, refreshToken: null, user: null }),
    }),
    {
      name: 'auth-storage',
    },
  ),
);
