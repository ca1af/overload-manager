import { useNavigate } from 'react-router-dom';
import { ChevronRight } from 'lucide-react';
import { Card, CardContent } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import type { SessionExercise } from '@/types/domain';
import { cn } from '@/utils/cn';

interface SessionExerciseCardProps {
  sessionId: number;
  sessionExercise: SessionExercise;
}

export function SessionExerciseCard({ sessionId, sessionExercise }: SessionExerciseCardProps) {
  const navigate = useNavigate();
  const completedSets = sessionExercise.sets.filter((s) => s.completed).length;
  const totalSets = sessionExercise.sets.length;
  const allDone = totalSets > 0 && completedSets === totalSets;

  return (
    <Card
      className={cn(
        'cursor-pointer transition-colors hover:bg-muted/50',
        allDone && 'border-green-200 bg-green-50',
      )}
      onClick={() =>
        navigate(`/sessions/${sessionId}/exercises/${sessionExercise.id}`)
      }
    >
      <CardContent className="flex items-center gap-3 p-4">
        <div className="flex-1">
          <p className="text-sm font-medium">{sessionExercise.exerciseNameKo}</p>
          <p className="mt-1 text-xs text-muted-foreground">
            {completedSets} / {totalSets} 세트 완료
          </p>
        </div>
        <div className="flex items-center gap-2">
          {allDone && (
            <Badge variant="default" className="bg-green-500">
              완료
            </Badge>
          )}
          {!allDone && totalSets > 0 && (
            <div className="flex gap-0.5">
              {sessionExercise.sets.map((set) => (
                <div
                  key={set.id}
                  className={cn(
                    'h-2 w-2 rounded-full',
                    set.completed ? 'bg-primary' : 'bg-border',
                  )}
                />
              ))}
            </div>
          )}
          <ChevronRight className="h-4 w-4 text-muted-foreground" />
        </div>
      </CardContent>
    </Card>
  );
}
