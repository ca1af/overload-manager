import { useState, useMemo } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Search, Plus } from 'lucide-react';
import { AppHeader } from '@/components/AppHeader';
import { BottomTabBar } from '@/components/BottomTabBar';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { getExercises, createExercise } from '@/api/exercises';
import { useDebounce } from '@/hooks/useDebounce';
import { cn } from '@/utils/cn';
import type { ExerciseCategory } from '@/types/domain';

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

const CATEGORY_OPTIONS: ExerciseCategory[] = [
  'CHEST', 'BACK', 'LEGS', 'SHOULDERS', 'BICEPS', 'TRICEPS', 'CORE',
];

const CATEGORY_LABEL: Record<ExerciseCategory, string> = {
  CHEST: '가슴',
  BACK: '등',
  LEGS: '하체',
  SHOULDERS: '어깨',
  BICEPS: '이두',
  TRICEPS: '삼두',
  CORE: '코어',
};

export default function ExercisesPage() {
  const queryClient = useQueryClient();
  const [search, setSearch] = useState('');
  const [category, setCategory] = useState<ExerciseCategory | 'ALL'>('ALL');
  const [newExerciseCategory, setNewExerciseCategory] = useState<ExerciseCategory | null>(null);
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

  const exercises = data?.content ?? [];

  const grouped = useMemo(() => {
    const compound = exercises.filter((e) => e.exerciseType === 'COMPOUND');
    const isolation = exercises.filter((e) => e.exerciseType === 'ISOLATION');
    return { COMPOUND: compound, ISOLATION: isolation };
  }, [exercises]);

  const createMutation = useMutation({
    mutationFn: (cat: ExerciseCategory) =>
      createExercise({ nameKo: debouncedSearch, category: cat }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['exercises'] });
      setSearch('');
      setNewExerciseCategory(null);
    },
  });

  const isEmpty = !isLoading && exercises.length === 0;
  const showAddButton = isEmpty && debouncedSearch.length > 0;

  return (
    <div className="flex min-h-dvh flex-col pb-20">
      <AppHeader title="운동" />

      <main className="flex-1">
        <div className="sticky top-14 z-30 bg-background px-4 pb-3 pt-3">
          <div className="relative mb-3">
            <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
            <Input
              placeholder="운동 검색..."
              value={search}
              onChange={(e) => {
                setSearch(e.target.value);
                setNewExerciseCategory(null);
              }}
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

        <div className="px-4 pb-4">
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
                    <Card key={exercise.id} className="mb-2">
                      <CardContent className="flex items-center gap-3 p-3">
                        <div className="flex-1">
                          <p className="text-sm font-medium">{exercise.nameKo}</p>
                          <p className="text-xs text-muted-foreground">{exercise.nameEn}</p>
                        </div>
                        <Badge variant="secondary" className="text-xs">
                          {exercise.equipment}
                        </Badge>
                      </CardContent>
                    </Card>
                  ))}
                </div>
              )}
              {grouped.ISOLATION.length > 0 && (
                <div className="mb-4">
                  <p className="mb-2 text-xs font-medium uppercase text-muted-foreground">
                    고립 운동
                  </p>
                  {grouped.ISOLATION.map((exercise) => (
                    <Card key={exercise.id} className="mb-2">
                      <CardContent className="flex items-center gap-3 p-3">
                        <div className="flex-1">
                          <p className="text-sm font-medium">{exercise.nameKo}</p>
                          <p className="text-xs text-muted-foreground">{exercise.nameEn}</p>
                        </div>
                        <Badge variant="secondary" className="text-xs">
                          {exercise.equipment}
                        </Badge>
                      </CardContent>
                    </Card>
                  ))}
                </div>
              )}

              {showAddButton && !newExerciseCategory && (
                <div className="flex flex-col items-center gap-3 py-12">
                  <p className="text-muted-foreground">운동을 찾을 수 없습니다</p>
                  <Button
                    variant="outline"
                    onClick={() => setNewExerciseCategory('CHEST')}
                  >
                    <Plus className="mr-1 h-4 w-4" />
                    &apos;{debouncedSearch}&apos; 운동 추가
                  </Button>
                </div>
              )}

              {showAddButton && newExerciseCategory && (
                <div className="flex flex-col items-center gap-3 py-12">
                  <p className="text-sm font-medium">카테고리를 선택하세요</p>
                  <div className="flex flex-wrap justify-center gap-2">
                    {CATEGORY_OPTIONS.map((cat) => (
                      <button
                        key={cat}
                        onClick={() => setNewExerciseCategory(cat)}
                        className={cn(
                          'rounded-full px-3 py-1.5 text-sm font-medium transition-colors',
                          newExerciseCategory === cat
                            ? 'bg-primary text-primary-foreground'
                            : 'bg-secondary text-secondary-foreground',
                        )}
                      >
                        {CATEGORY_LABEL[cat]}
                      </button>
                    ))}
                  </div>
                  <Button
                    onClick={() => createMutation.mutate(newExerciseCategory)}
                    disabled={createMutation.isPending}
                  >
                    {createMutation.isPending
                      ? '생성 중...'
                      : `'${debouncedSearch}' (${CATEGORY_LABEL[newExerciseCategory]}) 추가`}
                  </Button>
                </div>
              )}

              {isEmpty && !showAddButton && (
                <div className="flex items-center justify-center py-12">
                  <p className="text-muted-foreground">운동을 찾을 수 없습니다</p>
                </div>
              )}
            </>
          )}
        </div>
      </main>

      <BottomTabBar />
    </div>
  );
}
