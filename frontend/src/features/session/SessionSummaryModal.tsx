import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import type { WorkoutSession } from '@/types/domain';
import { formatDuration } from '@/utils/format';
import { useAuthStore } from '@/store/authStore';
import { formatWeight } from '@/utils/weight';

interface SessionSummaryModalProps {
  open: boolean;
  onClose: () => void;
  session: WorkoutSession | null;
}

export function SessionSummaryModal({ open, onClose, session }: SessionSummaryModalProps) {
  const user = useAuthStore((s) => s.user);
  const unit = user?.weightUnit ?? 'kg';

  if (!session) return null;

  return (
    <Dialog open={open} onOpenChange={onClose}>
      <DialogContent className="max-w-sm">
        <DialogHeader>
          <DialogTitle>운동 완료</DialogTitle>
          <DialogDescription>수고하셨습니다!</DialogDescription>
        </DialogHeader>

        <div className="grid grid-cols-2 gap-4 py-4">
          <div className="text-center">
            <p className="text-2xl font-bold text-primary">
              {session.durationSeconds ? formatDuration(session.durationSeconds) : '--'}
            </p>
            <p className="text-xs text-muted-foreground">운동 시간</p>
          </div>
          <div className="text-center">
            <p className="text-2xl font-bold text-primary">{session.exerciseCount}</p>
            <p className="text-xs text-muted-foreground">운동 수</p>
          </div>
          <div className="text-center">
            <p className="text-2xl font-bold text-primary">{session.totalSets}</p>
            <p className="text-xs text-muted-foreground">총 세트</p>
          </div>
          <div className="text-center">
            <p className="text-2xl font-bold text-primary">
              {formatWeight(session.totalVolumeKg, unit)}
            </p>
            <p className="text-xs text-muted-foreground">총 볼륨</p>
          </div>
        </div>

        <Button size="lg" className="w-full" onClick={onClose}>
          확인
        </Button>
      </DialogContent>
    </Dialog>
  );
}
