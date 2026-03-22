import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { format } from 'date-fns';
import { ko } from 'date-fns/locale';
import { Dumbbell, TrendingUp, Calendar } from 'lucide-react';
import { AppHeader } from '@/components/AppHeader';
import { BottomTabBar } from '@/components/BottomTabBar';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { useAuthStore } from '@/store/authStore';
import { useSessionStore } from '@/store/sessionStore';
import { getSessions, createSession } from '@/api/sessions';
import { getWeeklySummary } from '@/api/reports';
import { formatWeight } from '@/utils/weight';
import { formatDuration, formatDateKo } from '@/utils/format';
import type { OverloadAchievement } from '@/types/domain';

export default function DashboardPage() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const user = useAuthStore((s) => s.user);
  const setActiveSession = useSessionStore((s) => s.setActiveSession);
  const setElapsedSeconds = useSessionStore((s) => s.setElapsedSeconds);
  const unit = user?.weightUnit ?? 'kg';
  const today = format(new Date(), 'M월 d일 EEEE', { locale: ko });

  const { data: sessionsData } = useQuery({
    queryKey: ['sessions', { page: 0, size: 5 }],
    queryFn: () => getSessions({ page: 0, size: 5 }),
  });

  const { data: weeklySummary } = useQuery({
    queryKey: ['weeklySummary'],
    queryFn: () => getWeeklySummary(),
  });

  const createMutation = useMutation({
    mutationFn: createSession,
    onSuccess: (session) => {
      queryClient.invalidateQueries({ queryKey: ['sessions'] });
      setActiveSession(session.id);
      setElapsedSeconds(0);
      navigate(`/sessions/${session.id}`);
    },
  });

  const recentSessions = sessionsData?.content ?? [];
  const overloadCount = weeklySummary?.overloadAchievements?.filter(
    (o: OverloadAchievement) => o.improvement > 0,
  ).length ?? 0;

  return (
    <div className="flex min-h-dvh flex-col pb-20">
      <AppHeader />

      <main className="flex-1 space-y-4 px-4 py-4">
        <div>
          <h2 className="text-lg font-bold">
            {user?.nickname ?? '사용자'}님, 안녕하세요
          </h2>
          <p className="text-sm text-muted-foreground">{today}</p>
        </div>

        {weeklySummary && (
          <Card>
            <CardContent className="grid grid-cols-3 gap-4 p-4">
              <div className="text-center">
                <div className="mx-auto mb-1 flex h-8 w-8 items-center justify-center rounded-full bg-accent">
                  <Calendar className="h-4 w-4 text-primary" />
                </div>
                <p className="text-lg font-bold">
                  {weeklySummary.sessionCount ?? 0}회
                </p>
                <p className="text-xs text-muted-foreground">운동 횟수</p>
              </div>
              <div className="text-center">
                <div className="mx-auto mb-1 flex h-8 w-8 items-center justify-center rounded-full bg-accent">
                  <Dumbbell className="h-4 w-4 text-primary" />
                </div>
                <p className="text-lg font-bold">
                  {formatWeight(weeklySummary.totalVolume ?? 0, unit)}
                </p>
                <p className="text-xs text-muted-foreground">총 볼륨</p>
              </div>
              <div className="text-center">
                <div className="mx-auto mb-1 flex h-8 w-8 items-center justify-center rounded-full bg-accent">
                  <TrendingUp className="h-4 w-4 text-primary" />
                </div>
                <p className="text-lg font-bold">{overloadCount}</p>
                <p className="text-xs text-muted-foreground">과부하 달성</p>
              </div>
            </CardContent>
          </Card>
        )}

        <Button
          size="lg"
          className="w-full"
          onClick={() => createMutation.mutate()}
          disabled={createMutation.isPending}
        >
          {createMutation.isPending ? '세션 생성 중...' : '오늘 운동 시작'}
        </Button>

        {recentSessions.length > 0 && (
          <div>
            <h3 className="mb-3 text-sm font-semibold">최근 운동</h3>
            <div className="space-y-2">
              {recentSessions.map((session) => (
                <Card
                  key={session.id}
                  className="cursor-pointer"
                  onClick={() => {
                    if (!session.finishedAt) {
                      setActiveSession(session.id);
                      navigate(`/sessions/${session.id}`);
                    }
                  }}
                >
                  <CardContent className="flex items-center justify-between p-3">
                    <div>
                      <p className="text-sm font-medium">
                        {formatDateKo(session.sessionDate)}
                      </p>
                      <p className="text-xs text-muted-foreground">
                        {session.exerciseCount}개 운동
                      </p>
                    </div>
                    <div className="text-right">
                      {session.durationSeconds && (
                        <p className="text-xs text-muted-foreground">
                          {formatDuration(session.durationSeconds)}
                        </p>
                      )}
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>
          </div>
        )}
      </main>

      <BottomTabBar />
    </div>
  );
}
