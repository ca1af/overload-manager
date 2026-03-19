export type WeightUnit = 'kg' | 'lb';
export type ExerciseCategory = 'CHEST' | 'BACK' | 'LEGS' | 'SHOULDERS' | 'BICEPS' | 'TRICEPS' | 'CORE';
export type ExerciseType = 'COMPOUND' | 'ISOLATION';
export type Equipment = 'BARBELL' | 'DUMBBELL' | 'MACHINE' | 'CABLE' | 'BODYWEIGHT';

export interface User {
  id: number;
  email: string;
  nickname: string;
  weightUnit: WeightUnit;
  weeklyGoalSessions: number;
  emailVerified: boolean;
  createdAt: string;
}

export interface Exercise {
  id: number;
  nameKo: string;
  nameEn: string;
  category: ExerciseCategory;
  exerciseType: ExerciseType;
  equipment: Equipment;
  primaryMuscle: string;
  secondaryMuscles: string[];
  defaultSetsMin: number | null;
  defaultSetsMax: number | null;
  defaultRepsMin: number | null;
  defaultRepsMax: number | null;
  isCustom: boolean;
}

export interface WorkoutSet {
  id: number;
  setNumber: number;
  weightKg: number;
  reps: number;
  completed: boolean;
  restSeconds: number | null;
  completedAt: string | null;
}

export interface SessionExercise {
  id: number;
  orderIndex: number;
  exercise: Pick<Exercise, 'id' | 'nameKo' | 'category'>;
  sets: WorkoutSet[];
}

export interface WorkoutSession {
  id: number;
  sessionDate: string;
  notes: string | null;
  startedAt: string;
  finishedAt: string | null;
  durationSeconds: number | null;
  exerciseCount: number;
  totalSets: number;
  totalVolumeKg: number;
  exercises?: SessionExercise[];
}

export interface PreviousSessionData {
  sessionId: number | null;
  sessionDate: string | null;
  sets: Pick<WorkoutSet, 'setNumber' | 'weightKg' | 'reps' | 'completed'>[];
  totalVolumeKg: number;
}

export interface ExerciseHistoryEntry {
  sessionId: number;
  sessionDate: string;
  sets: Pick<WorkoutSet, 'setNumber' | 'weightKg' | 'reps' | 'completed'>[];
  maxWeightKg: number;
  totalVolumeKg: number;
  estimatedOneRepMax: number;
}

export interface WeeklySummary {
  weekStart: string;
  weekEnd: string;
  sessionCount: number;
  weeklyGoalSessions: number;
  totalSets: number;
  totalVolumeKg: number;
  previousWeekVolumeKg: number;
  volumeChangePercent: number;
  overloadAchieved: { exerciseId: number; exerciseName: string; achieved: boolean }[];
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
}
