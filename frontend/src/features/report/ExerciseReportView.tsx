import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { getExercises, getExerciseHistory } from '@/api/exercises';
import { MaxWeightChart } from './MaxWeightChart';
import { useAuthStore } from '@/store/authStore';
import { convertWeight } from '@/utils/weight';
import { formatDate } from '@/utils/format';
import { Card, CardContent } from '@/components/ui/card';

export function ExerciseReportView() {
  const [selectedExerciseId, setSelectedExerciseId] = useState<number | null>(null);
  const user = useAuthStore((s) => s.user);
  const unit = user?.weightUnit ?? 'kg';

  const { data: exercisesData } = useQuery({
    queryKey: ['exercises', 'all'],
    queryFn: () => getExercises({ size: 200 }),
  });

  const { data: historyData, isLoading: historyLoading } = useQuery({
    queryKey: ['exerciseHistory', selectedExerciseId],
    queryFn: () => getExerciseHistory(selectedExerciseId!, { size: 20 }),
    enabled: selectedExerciseId !== null,
  });

  const exercises = exercisesData?.content ?? [];

  return (
    <div className="space-y-4">
      <select
        value={selectedExerciseId ?? ''}
        onChange={(e) => {
          const val = e.target.value;
          setSelectedExerciseId(val ? Number(val) : null);
        }}
        className="w-full rounded-xl border border-input bg-background px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-ring"
      >
        <option value="">운동을 선택하세요</option>
        {exercises.map((ex) => (
          <option key={ex.id} value={ex.id}>
            {ex.nameKo}
          </option>
        ))}
      </select>

      {selectedExerciseId && (
        <>
          {historyLoading ? (
            <div className="flex items-center justify-center py-12">
              <p className="text-sm text-muted-foreground">불러오는 중...</p>
            </div>
          ) : (
            <>
              <Card>
                <CardContent className="p-4">
                  <p className="mb-3 text-sm font-medium">최고 무게 추이</p>
                  <MaxWeightChart history={historyData?.content ?? []} />
                </CardContent>
              </Card>

              <div className="space-y-2">
                <p className="text-sm font-medium">세션 기록</p>
                {(historyData?.content ?? []).map((entry) => (
                  <Card key={entry.sessionId}>
                    <CardContent className="p-3">
                      <div className="mb-2 flex items-center justify-between">
                        <span className="text-sm font-medium">
                          {formatDate(entry.sessionDate)}
                        </span>
                        <span className="text-xs text-muted-foreground">
                          최고 {convertWeight(entry.maxWeightKg, unit)} {unit}
                        </span>
                      </div>
                      <div className="flex flex-wrap gap-2">
                        {entry.sets.map((set) => (
                          <span
                            key={set.setNumber}
                            className="rounded-md bg-muted px-2 py-1 text-xs"
                          >
                            {convertWeight(set.weight, unit)}{unit} x {set.reps}
                          </span>
                        ))}
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </div>
            </>
          )}
        </>
      )}
    </div>
  );
}
