import { useState, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { AppHeader } from '@/components/AppHeader';
import { ExerciseList } from '@/features/exercise/ExerciseList';
import { addExercisesToSession } from '@/api/sessions';
import type { Exercise } from '@/types/domain';

export default function ExerciseSelectPage() {
  const { sessionId } = useParams<{ sessionId: string }>();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [selectedIds, setSelectedIds] = useState<number[]>([]);

  const mutation = useMutation({
    mutationFn: (exerciseIds: number[]) =>
      addExercisesToSession(Number(sessionId), exerciseIds),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['session', Number(sessionId)] });
      navigate(`/sessions/${sessionId}`);
    },
  });

  const handleToggle = useCallback((exercise: Exercise) => {
    setSelectedIds((prev) =>
      prev.includes(exercise.id)
        ? prev.filter((id) => id !== exercise.id)
        : [...prev, exercise.id],
    );
  }, []);

  const handleConfirm = useCallback(() => {
    if (selectedIds.length > 0) {
      mutation.mutate(selectedIds);
    }
  }, [mutation, selectedIds]);

  return (
    <div className="flex min-h-dvh flex-col">
      <AppHeader title="운동 선택" showBack />
      <ExerciseList
        selectedIds={selectedIds}
        onToggle={handleToggle}
        onConfirm={handleConfirm}
        isPending={mutation.isPending}
      />
    </div>
  );
}
