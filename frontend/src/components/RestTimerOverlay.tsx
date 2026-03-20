import { useRestTimer } from '@/hooks/useTimer';
import { useSessionStore } from '@/store/sessionStore';
import { Button } from '@/components/ui/button';
import { formatDuration } from '@/utils/format';

export function RestTimerOverlay() {
  const restTimer = useRestTimer();
  const addRestTime = useSessionStore((s) => s.addRestTime);
  const stopRestTimer = useSessionStore((s) => s.stopRestTimer);

  if (!restTimer.isRunning) return null;

  const progress =
    restTimer.totalSeconds > 0
      ? ((restTimer.totalSeconds - restTimer.remainingSeconds) / restTimer.totalSeconds) * 100
      : 0;

  const circumference = 2 * Math.PI * 54;
  const strokeDashoffset = circumference - (progress / 100) * circumference;

  return (
    <div className="fixed inset-0 z-50 flex flex-col items-center justify-center bg-black/90">
      <p className="mb-6 text-lg font-medium text-white/70">휴식 시간</p>

      <div className="relative mb-8">
        <svg width="140" height="140" className="-rotate-90">
          <circle
            cx="70"
            cy="70"
            r="54"
            fill="none"
            stroke="rgba(255,255,255,0.1)"
            strokeWidth="8"
          />
          <circle
            cx="70"
            cy="70"
            r="54"
            fill="none"
            stroke="#4F46E5"
            strokeWidth="8"
            strokeLinecap="round"
            strokeDasharray={circumference}
            strokeDashoffset={strokeDashoffset}
            className="transition-all duration-1000"
          />
        </svg>
        <div className="absolute inset-0 flex items-center justify-center">
          <span className="text-3xl font-bold text-white tabular-nums">
            {formatDuration(restTimer.remainingSeconds)}
          </span>
        </div>
      </div>

      <div className="flex gap-4">
        <Button
          variant="outline"
          className="border-white/20 bg-transparent text-white hover:bg-white/10"
          onClick={() => addRestTime(30)}
        >
          +30s
        </Button>
        <Button
          variant="default"
          onClick={stopRestTimer}
        >
          건너뛰기
        </Button>
      </div>
    </div>
  );
}
