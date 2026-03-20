import { useState } from 'react';
import { useParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { History } from 'lucide-react';
import { AppHeader } from '@/components/AppHeader';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { RestTimerOverlay } from '@/components/RestTimerOverlay';
import { SetTable } from '@/features/session/SetTable';
import { OverloadInfoSheet } from '@/features/session/OverloadInfoSheet';
import { getSession } from '@/api/sessions';
import { getPreviousSession } from '@/api/exercises';
import { useAuthStore } from '@/store/authStore';
import { convertWeight } from '@/utils/weight';
import { formatDate } from '@/utils/format';

export default function SetRecordPage() {
  const { sessionId, sessionExerciseId } = useParams<{
    sessionId: string;
    sessionExerciseId: string;
  }>();
  const sid = Number(sessionId);
  const seId = Number(sessionExerciseId);
  const user = useAuthStore((s) => s.user);
  const unit = user?.weightUnit ?? 'kg';

  const [sheetOpen, setSheetOpen] = useState(false);

  const { data: session } = useQuery({
    queryKey: ['session', sid],
    queryFn: () => getSession(sid),
  });

  const sessionExercise = session?.exercises?.find((se) => se.id === seId);
  const exerciseId = sessionExercise?.exercise.id;

  const { data: previousData } = useQuery({
    queryKey: ['previousSession', exerciseId],
    queryFn: () => getPreviousSession(exerciseId!),
    enabled: exerciseId !== undefined,
  });

  if (!sessionExercise) {
    return (
      <div className="flex min-h-dvh flex-col">
        <AppHeader title="세트 기록" showBack />
        <div className="flex flex-1 items-center justify-center">
          <p className="text-muted-foreground">불러오는 중...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="flex min-h-dvh flex-col">
      <AppHeader
        title={sessionExercise.exercise.nameKo}
        showBack
        rightAction={
          <Button variant="ghost" size="icon" onClick={() => setSheetOpen(true)}>
            <History className="h-5 w-5" />
          </Button>
        }
      />

      <main className="flex-1 px-4 py-4">
        {previousData && previousData.sessionId !== null && (
          <Card className="mb-4 border-primary/20 bg-accent">
            <CardContent className="p-3">
              <div className="flex items-center justify-between">
                <p className="text-xs text-muted-foreground">
                  이전 기록 ({previousData.sessionDate ? formatDate(previousData.sessionDate) : ''})
                </p>
                <p className="text-sm font-medium text-primary">
                  {convertWeight(previousData.totalVolumeKg, unit).toLocaleString()} {unit}
                </p>
              </div>
              <div className="mt-2 flex flex-wrap gap-1">
                {previousData.sets.map((set) => (
                  <span
                    key={set.setNumber}
                    className="rounded bg-background px-1.5 py-0.5 text-xs"
                  >
                    {convertWeight(set.weightKg, unit)}{unit} x {set.reps}
                  </span>
                ))}
              </div>
            </CardContent>
          </Card>
        )}

        <SetTable
          sessionId={sid}
          sessionExerciseId={seId}
          sets={sessionExercise.sets}
        />
      </main>

      <RestTimerOverlay />

      {exerciseId !== undefined && (
        <OverloadInfoSheet
          open={sheetOpen}
          onOpenChange={setSheetOpen}
          exerciseId={exerciseId}
        />
      )}
    </div>
  );
}
