import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { ChevronLeft, ChevronRight, TrendingUp, TrendingDown } from 'lucide-react';
import { addDays, startOfWeek, format } from 'date-fns';
import { ko } from 'date-fns/locale';
import { Card, CardContent } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { getWeeklySummary } from '@/api/reports';
import { useAuthStore } from '@/store/authStore';
import { formatWeight } from '@/utils/weight';
import { cn } from '@/utils/cn';

export function WeeklySummaryView() {
  const [weekOffset, setWeekOffset] = useState(0);
  const user = useAuthStore((s) => s.user);
  const unit = user?.weightUnit ?? 'kg';

  const currentWeekStart = startOfWeek(addDays(new Date(), weekOffset * 7), {
    weekStartsOn: 1,
  });
  const weekStartStr = format(currentWeekStart, 'yyyy-MM-dd');

  const { data: summary, isLoading } = useQuery({
    queryKey: ['weeklySummary', weekStartStr],
    queryFn: () => getWeeklySummary(weekStartStr),
  });

  const weekLabel = format(currentWeekStart, 'M/d', { locale: ko }) +
    ' - ' +
    format(addDays(currentWeekStart, 6), 'M/d', { locale: ko });

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <Button variant="ghost" size="icon" onClick={() => setWeekOffset((w) => w - 1)}>
          <ChevronLeft className="h-5 w-5" />
        </Button>
        <span className="text-sm font-medium">{weekLabel}</span>
        <Button
          variant="ghost"
          size="icon"
          onClick={() => setWeekOffset((w) => w + 1)}
          disabled={weekOffset >= 0}
        >
          <ChevronRight className="h-5 w-5" />
        </Button>
      </div>

      {isLoading ? (
        <div className="flex items-center justify-center py-12">
          <p className="text-sm text-muted-foreground">불러오는 중...</p>
        </div>
      ) : summary ? (
        <>
          <div className="grid grid-cols-2 gap-3">
            <Card>
              <CardContent className="p-4 text-center">
                <p className="text-2xl font-bold text-primary">
                  {summary.sessionCount} / {summary.weeklyGoalSessions}
                </p>
                <p className="text-xs text-muted-foreground">운동 횟수</p>
              </CardContent>
            </Card>
            <Card>
              <CardContent className="p-4 text-center">
                <p className="text-2xl font-bold text-primary">{summary.totalSets}</p>
                <p className="text-xs text-muted-foreground">총 세트</p>
              </CardContent>
            </Card>
          </div>

          <Card>
            <CardContent className="p-4">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-muted-foreground">총 볼륨</p>
                  <p className="text-xl font-bold">
                    {formatWeight(summary.totalVolumeKg, unit)}
                  </p>
                </div>
                <div className="flex items-center gap-1">
                  {summary.volumeChangePercent >= 0 ? (
                    <TrendingUp className="h-4 w-4 text-green-500" />
                  ) : (
                    <TrendingDown className="h-4 w-4 text-destructive" />
                  )}
                  <span
                    className={cn(
                      'text-sm font-medium',
                      summary.volumeChangePercent >= 0
                        ? 'text-green-500'
                        : 'text-destructive',
                    )}
                  >
                    {summary.volumeChangePercent >= 0 ? '+' : ''}
                    {summary.volumeChangePercent.toFixed(1)}%
                  </span>
                </div>
              </div>
              <div className="mt-3 h-2 overflow-hidden rounded-full bg-muted">
                <div
                  className="h-full rounded-full bg-primary transition-all"
                  style={{
                    width: `${Math.min(
                      100,
                      summary.previousWeekVolumeKg > 0
                        ? (summary.totalVolumeKg / summary.previousWeekVolumeKg) * 100
                        : summary.totalVolumeKg > 0
                          ? 100
                          : 0,
                    )}%`,
                  }}
                />
              </div>
            </CardContent>
          </Card>

          {summary.overloadAchieved.length > 0 && (
            <Card>
              <CardContent className="p-4">
                <p className="mb-3 text-sm font-medium">점진적 과부하 달성</p>
                <div className="space-y-2">
                  {summary.overloadAchieved.map((item) => (
                    <div
                      key={item.exerciseId}
                      className="flex items-center justify-between text-sm"
                    >
                      <span>{item.exerciseName}</span>
                      <span
                        className={cn(
                          'text-xs font-medium',
                          item.achieved ? 'text-green-500' : 'text-muted-foreground',
                        )}
                      >
                        {item.achieved ? '달성' : '미달성'}
                      </span>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
          )}
        </>
      ) : (
        <div className="flex items-center justify-center py-12">
          <p className="text-sm text-muted-foreground">데이터가 없습니다</p>
        </div>
      )}
    </div>
  );
}
