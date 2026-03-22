import { useCallback, useRef, useEffect } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { Plus, Trash2 } from 'lucide-react';
import { Input } from '@/components/ui/input';
import { Checkbox } from '@/components/ui/checkbox';
import { Button } from '@/components/ui/button';
import { updateSet, createSet, deleteSet } from '@/api/sessions';
import { useSessionStore } from '@/store/sessionStore';
import { useAuthStore } from '@/store/authStore';
import { convertWeight, convertToKg } from '@/utils/weight';
import type { WorkoutSet } from '@/types/domain';

interface SetTableProps {
  sessionId: number;
  sessionExerciseId: number;
  sets: WorkoutSet[];
  defaultRestSeconds?: number;
}

export function SetTable({
  sessionId,
  sessionExerciseId,
  sets,
  defaultRestSeconds = 90,
}: SetTableProps) {
  const queryClient = useQueryClient();
  const startRestTimer = useSessionStore((s) => s.startRestTimer);
  const user = useAuthStore((s) => s.user);
  const unit = user?.weightUnit ?? 'kg';
  const debounceTimers = useRef<Record<number, ReturnType<typeof setTimeout>>>({});

  useEffect(() => {
    return () => {
      Object.values(debounceTimers.current).forEach(clearTimeout);
    };
  }, []);

  const invalidate = useCallback(() => {
    queryClient.invalidateQueries({ queryKey: ['session', sessionId] });
  }, [queryClient, sessionId]);

  const updateMutation = useMutation({
    mutationFn: (params: {
      setId: number;
      data: { weight?: number; reps?: number; completed?: boolean; restSeconds?: number };
    }) => updateSet(sessionId, sessionExerciseId, params.setId, params.data),
    onSuccess: invalidate,
  });

  const addMutation = useMutation({
    mutationFn: (data: { weight: number; reps: number }) =>
      createSet(sessionId, sessionExerciseId, data),
    onSuccess: invalidate,
  });

  const deleteMutation = useMutation({
    mutationFn: (setId: number) => deleteSet(sessionId, sessionExerciseId, setId),
    onSuccess: invalidate,
  });

  const handleDebouncedUpdate = useCallback(
    (setId: number, data: { weight?: number; reps?: number }) => {
      if (debounceTimers.current[setId]) {
        clearTimeout(debounceTimers.current[setId]);
      }
      debounceTimers.current[setId] = setTimeout(() => {
        updateMutation.mutate({ setId, data });
      }, 500);
    },
    [updateMutation],
  );

  const handleComplete = useCallback(
    (set: WorkoutSet) => {
      const newCompleted = !set.completed;
      updateMutation.mutate({
        setId: set.id,
        data: { completed: newCompleted, restSeconds: defaultRestSeconds },
      });
      if (newCompleted) {
        startRestTimer(defaultRestSeconds);
      }
    },
    [updateMutation, startRestTimer, defaultRestSeconds],
  );

  const handleAddSet = useCallback(() => {
    const lastSet = sets[sets.length - 1];
    addMutation.mutate({
      weight: lastSet?.weight ?? 0,
      reps: lastSet?.reps ?? 10,
    });
  }, [addMutation, sets]);

  const totalVolume = sets.reduce((acc, set) => {
    if (set.completed) {
      return acc + set.weight * set.reps;
    }
    return acc;
  }, 0);

  return (
    <div>
      <div className="mb-4 rounded-xl bg-accent p-3 text-center">
        <p className="text-xs text-muted-foreground">총 볼륨</p>
        <p className="text-lg font-bold text-primary">
          {convertWeight(totalVolume, unit).toLocaleString()} {unit}
        </p>
      </div>

      <div className="mb-2 grid grid-cols-[40px_1fr_1fr_48px_36px] items-center gap-2 px-1 text-xs font-medium text-muted-foreground">
        <span>세트</span>
        <span>무게 ({unit})</span>
        <span>횟수</span>
        <span className="text-center">완료</span>
        <span />
      </div>

      {sets.map((set) => (
        <div
          key={set.id}
          className="mb-2 grid grid-cols-[40px_1fr_1fr_48px_36px] items-center gap-2"
        >
          <span className="text-center text-sm font-medium text-muted-foreground">
            {set.setNumber}
          </span>
          <Input
            type="number"
            defaultValue={convertWeight(set.weight, unit)}
            className="h-10 text-center"
            onChange={(e) => {
              const val = parseFloat(e.target.value);
              if (!isNaN(val)) {
                handleDebouncedUpdate(set.id, { weight: convertToKg(val, unit) });
              }
            }}
          />
          <Input
            type="number"
            defaultValue={set.reps}
            className="h-10 text-center"
            onChange={(e) => {
              const val = parseInt(e.target.value, 10);
              if (!isNaN(val)) {
                handleDebouncedUpdate(set.id, { reps: val });
              }
            }}
          />
          <div className="flex justify-center">
            <Checkbox
              checked={set.completed}
              onCheckedChange={() => handleComplete(set)}
            />
          </div>
          <button
            onClick={() => deleteMutation.mutate(set.id)}
            className="flex h-8 w-8 items-center justify-center rounded-md text-muted-foreground hover:bg-destructive/10 hover:text-destructive"
          >
            <Trash2 className="h-3.5 w-3.5" />
          </button>
        </div>
      ))}

      <Button
        variant="outline"
        size="sm"
        className="mt-3 w-full"
        onClick={handleAddSet}
        disabled={addMutation.isPending}
      >
        <Plus className="mr-1 h-4 w-4" />
        세트 추가
      </Button>
    </div>
  );
}
