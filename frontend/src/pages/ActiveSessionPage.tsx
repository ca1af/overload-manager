import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Plus, Square } from 'lucide-react';
import { AppHeader } from '@/components/AppHeader';
import { Button } from '@/components/ui/button';
import { RestTimerOverlay } from '@/components/RestTimerOverlay';
import { SessionExerciseCard } from '@/features/session/SessionExerciseCard';
import { SessionSummaryModal } from '@/features/session/SessionSummaryModal';
import { getSession, updateSession } from '@/api/sessions';
import { useSessionStore } from '@/store/sessionStore';
import { useSessionTimer } from '@/hooks/useTimer';
import { formatDuration } from '@/utils/format';

export default function ActiveSessionPage() {
  const { sessionId } = useParams<{ sessionId: string }>();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const sid = Number(sessionId);

  const elapsedSeconds = useSessionStore((s) => s.elapsedSeconds);
  const setActiveSession = useSessionStore((s) => s.setActiveSession);

  const [showSummary, setShowSummary] = useState(false);

  const { data: session } = useQuery({
    queryKey: ['session', sid],
    queryFn: () => getSession(sid),
    refetchInterval: 5000,
  });

  const isActive = session !== undefined && session.finishedAt === null;
  useSessionTimer(isActive);

  useEffect(() => {
    setActiveSession(sid);
  }, [sid, setActiveSession]);

  const finishMutation = useMutation({
    mutationFn: () =>
      updateSession(sid, { finishedAt: new Date().toISOString() }),
    onSuccess: (updatedSession) => {
      queryClient.setQueryData(['session', sid], updatedSession);
      queryClient.invalidateQueries({ queryKey: ['sessions'] });
      setShowSummary(true);
    },
  });

  const exercises = session?.exercises ?? [];

  return (
    <div className="flex min-h-dvh flex-col">
      <AppHeader
        title="운동 세션"
        showBack
        rightAction={
          isActive && (
            <div className="flex items-center gap-2">
              <span className="rounded-lg bg-accent px-2 py-1 text-sm font-medium tabular-nums text-primary">
                {formatDuration(elapsedSeconds)}
              </span>
            </div>
          )
        }
      />

      <main className="flex-1 space-y-3 px-4 py-4">
        {exercises.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-16">
            <p className="mb-4 text-muted-foreground">운동을 추가해주세요</p>
            <Button onClick={() => navigate(`/sessions/${sid}/exercises/add`)}>
              <Plus className="mr-1 h-4 w-4" />
              운동 추가
            </Button>
          </div>
        ) : (
          <>
            {exercises.map((se) => (
              <SessionExerciseCard
                key={se.id}
                sessionId={sid}
                sessionExercise={se}
              />
            ))}

            {isActive && (
              <Button
                variant="outline"
                className="w-full"
                onClick={() => navigate(`/sessions/${sid}/exercises/add`)}
              >
                <Plus className="mr-1 h-4 w-4" />
                운동 추가
              </Button>
            )}
          </>
        )}
      </main>

      {isActive && exercises.length > 0 && (
        <div className="sticky bottom-0 border-t bg-background p-4">
          <Button
            size="lg"
            variant="destructive"
            className="w-full"
            onClick={() => finishMutation.mutate()}
            disabled={finishMutation.isPending}
          >
            <Square className="mr-2 h-4 w-4" />
            {finishMutation.isPending ? '종료 중...' : '세션 종료'}
          </Button>
        </div>
      )}

      <RestTimerOverlay />

      <SessionSummaryModal
        open={showSummary}
        onClose={() => {
          setShowSummary(false);
          navigate('/');
        }}
        session={session ?? null}
      />
    </div>
  );
}
