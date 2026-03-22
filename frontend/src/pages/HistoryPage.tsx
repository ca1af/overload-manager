import { useQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { AppHeader } from '@/components/AppHeader';
import { BottomTabBar } from '@/components/BottomTabBar';
import { Card, CardContent } from '@/components/ui/card';
import { getSessions } from '@/api/sessions';
import { formatDuration, formatDateKo } from '@/utils/format';

export default function HistoryPage() {
  const navigate = useNavigate();

  const { data, isLoading } = useQuery({
    queryKey: ['sessions', { page: 0, size: 50 }],
    queryFn: () => getSessions({ page: 0, size: 50 }),
  });

  const sessions = data?.content ?? [];

  return (
    <div className="flex min-h-dvh flex-col pb-20">
      <AppHeader title="기록" />

      <main className="flex-1 px-4 py-4">
        {isLoading ? (
          <div className="flex items-center justify-center py-12">
            <p className="text-muted-foreground">불러오는 중...</p>
          </div>
        ) : sessions.length === 0 ? (
          <div className="flex items-center justify-center py-12">
            <p className="text-muted-foreground">운동 기록이 없습니다</p>
          </div>
        ) : (
          <div className="space-y-2">
            {sessions.map((session) => (
              <Card
                key={session.id}
                className="cursor-pointer"
                onClick={() => navigate(`/sessions/${session.id}`)}
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
        )}
      </main>

      <BottomTabBar />
    </div>
  );
}
