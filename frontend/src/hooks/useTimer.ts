import { useEffect, useRef } from 'react';
import { useSessionStore } from '@/store/sessionStore';

export function useSessionTimer(isActive: boolean) {
  const incrementElapsed = useSessionStore((s) => s.incrementElapsed);
  const intervalRef = useRef<ReturnType<typeof setInterval> | null>(null);

  useEffect(() => {
    if (isActive) {
      intervalRef.current = setInterval(() => {
        incrementElapsed();
      }, 1000);
    }
    return () => {
      if (intervalRef.current) {
        clearInterval(intervalRef.current);
      }
    };
  }, [isActive, incrementElapsed]);
}

export function useRestTimer() {
  const restTimer = useSessionStore((s) => s.restTimer);
  const tickRestTimer = useSessionStore((s) => s.tickRestTimer);
  const intervalRef = useRef<ReturnType<typeof setInterval> | null>(null);

  useEffect(() => {
    if (restTimer.isRunning) {
      intervalRef.current = setInterval(() => {
        tickRestTimer();
      }, 1000);
    }
    return () => {
      if (intervalRef.current) {
        clearInterval(intervalRef.current);
      }
    };
  }, [restTimer.isRunning, tickRestTimer]);

  return restTimer;
}
