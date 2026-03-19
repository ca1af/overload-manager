import { useState, useMemo } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Search } from 'lucide-react';
import { Input } from '@/components/ui/input';
import { Checkbox } from '@/components/ui/checkbox';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { getExercises } from '@/api/exercises';
import { useDebounce } from '@/hooks/useDebounce';
import type { Exercise, ExerciseCategory } from '@/types/domain';
import { cn } from '@/utils/cn';

const CATEGORIES: { value: ExerciseCategory | 'ALL'; label: string }[] = [
  { value: 'ALL', label: '전체' },
  { value: 'CHEST', label: '가슴' },
  { value: 'BACK', label: '등' },
  { value: 'LEGS', label: '하체' },
  { value: 'SHOULDERS', label: '어깨' },
  { value: 'BICEPS', label: '이두' },
  { value: 'TRICEPS', label: '삼두' },
  { value: 'CORE', label: '코어' },
];

interface ExerciseListProps {
  selectedIds: number[];
  onToggle: (exercise: Exercise) => void;
  onConfirm: () => void;
  isPending: boolean;
}

export function ExerciseList({ selectedIds, onToggle, onConfirm, isPending }: ExerciseListProps) {
  const [search, setSearch] = useState('');
  const [category, setCategory] = useState<ExerciseCategory | 'ALL'>('ALL');
  const debouncedSearch = useDebounce(search, 300);

  const { data, isLoading } = useQuery({
    queryKey: ['exercises', category, debouncedSearch],
    queryFn: () =>
      getExercises({
        category: category === 'ALL' ? undefined : category,
        keyword: debouncedSearch || undefined,
        size: 100,
      }),
  });

  const grouped = useMemo(() => {
    if (!data?.content) return { COMPOUND: [], ISOLATION: [] };
    const compound = data.content.filter((e) => e.exerciseType === 'COMPOUND');
    const isolation = data.content.filter((e) => e.exerciseType === 'ISOLATION');
    return { COMPOUND: compound, ISOLATION: isolation };
  }, [data]);

  return (
    <div className="flex flex-1 flex-col">
      <div className="sticky top-14 z-30 bg-background px-4 pb-3 pt-3">
        <div className="relative mb-3">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            placeholder="운동 검색..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="pl-9"
          />
        </div>
        <div className="flex gap-2 overflow-x-auto pb-1 scrollbar-hide">
          {CATEGORIES.map((cat) => (
            <button
              key={cat.value}
              onClick={() => setCategory(cat.value)}
              className={cn(
                'shrink-0 rounded-full px-3 py-1.5 text-sm font-medium transition-colors',
                category === cat.value
                  ? 'bg-primary text-primary-foreground'
                  : 'bg-secondary text-secondary-foreground',
              )}
            >
              {cat.label}
            </button>
          ))}
        </div>
      </div>

      <div className="flex-1 overflow-y-auto px-4 pb-24">
        {isLoading ? (
          <div className="flex items-center justify-center py-12">
            <p className="text-muted-foreground">불러오는 중...</p>
          </div>
        ) : (
          <>
            {grouped.COMPOUND.length > 0 && (
              <div className="mb-4">
                <p className="mb-2 text-xs font-medium uppercase text-muted-foreground">
                  복합 운동
                </p>
                {grouped.COMPOUND.map((exercise) => (
                  <ExerciseItem
                    key={exercise.id}
                    exercise={exercise}
                    selected={selectedIds.includes(exercise.id)}
                    onToggle={() => onToggle(exercise)}
                  />
                ))}
              </div>
            )}
            {grouped.ISOLATION.length > 0 && (
              <div>
                <p className="mb-2 text-xs font-medium uppercase text-muted-foreground">
                  고립 운동
                </p>
                {grouped.ISOLATION.map((exercise) => (
                  <ExerciseItem
                    key={exercise.id}
                    exercise={exercise}
                    selected={selectedIds.includes(exercise.id)}
                    onToggle={() => onToggle(exercise)}
                  />
                ))}
              </div>
            )}
            {grouped.COMPOUND.length === 0 && grouped.ISOLATION.length === 0 && (
              <div className="flex items-center justify-center py-12">
                <p className="text-muted-foreground">운동을 찾을 수 없습니다</p>
              </div>
            )}
          </>
        )}
      </div>

      {selectedIds.length > 0 && (
        <div className="fixed bottom-0 left-0 right-0 z-40 border-t bg-background p-4">
          <Button
            size="lg"
            className="w-full"
            onClick={onConfirm}
            disabled={isPending}
          >
            {isPending ? '추가 중...' : `${selectedIds.length}개 운동 추가하기`}
          </Button>
        </div>
      )}
    </div>
  );
}

function ExerciseItem({
  exercise,
  selected,
  onToggle,
}: {
  exercise: Exercise;
  selected: boolean;
  onToggle: () => void;
}) {
  return (
    <button
      onClick={onToggle}
      className={cn(
        'mb-2 flex w-full items-center gap-3 rounded-xl border p-3 text-left transition-colors',
        selected ? 'border-primary bg-accent' : 'border-border',
      )}
    >
      <Checkbox checked={selected} />
      <div className="flex-1">
        <p className="text-sm font-medium">{exercise.nameKo}</p>
        <p className="text-xs text-muted-foreground">{exercise.nameEn}</p>
      </div>
      <Badge variant="secondary" className="text-xs">
        {exercise.equipment}
      </Badge>
    </button>
  );
}
