import { useQuery } from '@tanstack/react-query';
import { Drawer } from 'vaul';
import { getPreviousSession } from '@/api/exercises';
import { useAuthStore } from '@/store/authStore';
import { formatWeight, convertWeight } from '@/utils/weight';
import { formatDate } from '@/utils/format';

interface OverloadInfoSheetProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  exerciseId: number;
}

export function OverloadInfoSheet({ open, onOpenChange, exerciseId }: OverloadInfoSheetProps) {
  const user = useAuthStore((s) => s.user);
  const unit = user?.weightUnit ?? 'kg';

  const { data: previousData } = useQuery({
    queryKey: ['previousSession', exerciseId],
    queryFn: () => getPreviousSession(exerciseId),
    enabled: open,
  });

  return (
    <Drawer.Root open={open} onOpenChange={onOpenChange}>
      <Drawer.Portal>
        <Drawer.Overlay className="fixed inset-0 z-50 bg-black/40" />
        <Drawer.Content className="fixed bottom-0 left-0 right-0 z-50 mt-24 flex h-[60vh] flex-col rounded-t-2xl bg-background">
          <div className="mx-auto mt-4 h-1.5 w-12 rounded-full bg-muted-foreground/20" />
          <Drawer.Title className="px-6 pt-4 text-lg font-semibold">
            이전 기록
          </Drawer.Title>
          <Drawer.Description className="px-6 text-sm text-muted-foreground">
            {previousData?.sessionDate
              ? `최근 세션: ${formatDate(previousData.sessionDate)}`
              : '이전 기록이 없습니다'}
          </Drawer.Description>
          <div className="flex-1 overflow-y-auto px-6 py-4">
            {previousData?.sets && previousData.sets.length > 0 ? (
              <>
                <div className="mb-4 rounded-xl bg-accent p-3">
                  <p className="text-xs text-muted-foreground">이전 총 볼륨</p>
                  <p className="text-lg font-bold text-primary">
                    {formatWeight(previousData.sets.reduce((sum, s) => sum + s.weight * s.reps, 0), unit)}
                  </p>
                </div>

                <div className="space-y-2">
                  {previousData.sets.map((set) => (
                    <div
                      key={set.setNumber}
                      className="flex items-center justify-between rounded-lg border p-3 text-sm"
                    >
                      <span className="text-muted-foreground">
                        {set.setNumber}세트
                      </span>
                      <span className="font-medium">
                        {convertWeight(set.weight, unit)} {unit} x {set.reps}회
                      </span>
                    </div>
                  ))}
                </div>

                <div className="mt-4 rounded-xl bg-primary/5 p-3">
                  <p className="text-xs font-medium text-primary">목표 제안</p>
                  <p className="mt-1 text-sm text-muted-foreground">
                    이전보다 무게를 2.5{unit} 올리거나 1회 더 수행해보세요.
                  </p>
                </div>
              </>
            ) : (
              <div className="flex h-32 items-center justify-center">
                <p className="text-muted-foreground">이전 기록이 없습니다</p>
              </div>
            )}
          </div>
        </Drawer.Content>
      </Drawer.Portal>
    </Drawer.Root>
  );
}
