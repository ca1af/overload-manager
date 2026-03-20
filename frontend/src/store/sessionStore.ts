import { create } from 'zustand';

interface RestTimerState {
  isRunning: boolean;
  totalSeconds: number;
  remainingSeconds: number;
}

interface SessionState {
  activeSessionId: number | null;
  elapsedSeconds: number;
  restTimer: RestTimerState;
  setActiveSession: (id: number | null) => void;
  setElapsedSeconds: (seconds: number) => void;
  incrementElapsed: () => void;
  startRestTimer: (seconds: number) => void;
  tickRestTimer: () => void;
  addRestTime: (seconds: number) => void;
  stopRestTimer: () => void;
}

export const useSessionStore = create<SessionState>()((set) => ({
  activeSessionId: null,
  elapsedSeconds: 0,
  restTimer: {
    isRunning: false,
    totalSeconds: 0,
    remainingSeconds: 0,
  },
  setActiveSession: (id) => set({ activeSessionId: id }),
  setElapsedSeconds: (seconds) => set({ elapsedSeconds: seconds }),
  incrementElapsed: () =>
    set((state) => ({ elapsedSeconds: state.elapsedSeconds + 1 })),
  startRestTimer: (seconds) =>
    set({
      restTimer: {
        isRunning: true,
        totalSeconds: seconds,
        remainingSeconds: seconds,
      },
    }),
  tickRestTimer: () =>
    set((state) => {
      const next = state.restTimer.remainingSeconds - 1;
      if (next <= 0) {
        return {
          restTimer: { isRunning: false, totalSeconds: 0, remainingSeconds: 0 },
        };
      }
      return {
        restTimer: { ...state.restTimer, remainingSeconds: next },
      };
    }),
  addRestTime: (seconds) =>
    set((state) => ({
      restTimer: {
        ...state.restTimer,
        totalSeconds: state.restTimer.totalSeconds + seconds,
        remainingSeconds: state.restTimer.remainingSeconds + seconds,
      },
    })),
  stopRestTimer: () =>
    set({
      restTimer: { isRunning: false, totalSeconds: 0, remainingSeconds: 0 },
    }),
}));
