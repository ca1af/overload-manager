export type WeightUnit = 'kg' | 'lb';
export type ExerciseCategory = 'CHEST' | 'BACK' | 'LEGS' | 'SHOULDERS' | 'BICEPS' | 'TRICEPS' | 'CORE';
export type ExerciseType = 'COMPOUND' | 'ISOLATION';
export type Equipment = 'BARBELL' | 'DUMBBELL' | 'MACHINE' | 'CABLE' | 'BODYWEIGHT';

export interface User {
  id: number;
  email: string;
  nickname: string;
  weightUnit: WeightUnit;
}

// --- Exercise ---
// Matches: ExerciseResponse (ExerciseDtos.kt:10-24)
export interface Exercise {
  id: number;
  nameKo: string;
  nameEn: string;
  category: ExerciseCategory;
  exerciseType: ExerciseType;
  equipment: Equipment;
  primaryMuscle: string;
  secondaryMuscles: string[];
  defaultSetsMin: number;
  defaultSetsMax: number;
  defaultRepsMin: number;
  defaultRepsMax: number;
  isCustom: boolean;
}

// --- Workout Set ---
// Matches: WorkoutSetResponse (WorkoutDtos.kt:91-99)
export interface WorkoutSet {
  id: number;
  setNumber: number;
  weight: number;       // BE: BigDecimal "weight" (not "weightKg")
  reps: number;
  completed: boolean;
  restSeconds: number | null;
  completedAt: string | null;
}

// --- Session Exercise ---
// Matches: SessionExerciseResponse (WorkoutDtos.kt:47-55) — flat structure
export interface SessionExercise {
  id: number;
  exerciseId: number;
  exerciseNameKo: string;
  exerciseNameEn: string;
  category: string;
  orderIndex: number;
  sets: WorkoutSet[];
}

// --- Workout Session ---
// Matches: SessionDetailResponse (WorkoutDtos.kt:35-45)
export interface WorkoutSession {
  id: number;
  sessionDate: string;
  notes: string | null;
  startedAt: string | null;
  finishedAt: string | null;
  durationSeconds: number | null;
  exercises: SessionExercise[];
  createdAt: string;
  updatedAt: string;
}

// Matches: SessionSummaryResponse (WorkoutDtos.kt:24-33)
export interface WorkoutSessionSummary {
  id: number;
  sessionDate: string;
  notes: string | null;
  startedAt: string | null;
  finishedAt: string | null;
  durationSeconds: number | null;
  exerciseCount: number;
  createdAt: string;
}

// --- Previous Session ---
// Matches: PreviousSessionResponse (ExerciseDtos.kt:26-30)
export interface PreviousSessionData {
  sessionId: number;
  sessionDate: string;
  sets: PreviousSet[];
}

// Matches: PreviousSetResponse (ExerciseDtos.kt:32-37)
export interface PreviousSet {
  setNumber: number;
  weight: number;       // BE: BigDecimal "weight"
  reps: number;
  completed: boolean;
}

// --- Exercise History ---
// Matches: ExerciseHistoryResponse (ExerciseDtos.kt:39-46)
export interface ExerciseHistoryEntry {
  sessionId: number;
  sessionDate: string;
  maxWeightKg: number;
  totalVolumeKg: number;
  estimatedOneRepMax: number;
  sets: PreviousSet[];
}

// --- Weekly Summary ---
// Matches: WeeklySummaryResponse (ReportDtos.kt:7-14)
export interface WeeklySummary {
  weekStart: string;
  weekEnd: string;
  sessionCount: number;
  totalVolume: number;          // BE: "totalVolume" (not "totalVolumeKg")
  exerciseSummaries: ExerciseWeeklySummary[];
  overloadAchievements: OverloadAchievement[];  // BE: "overloadAchievements"
}

export interface ExerciseWeeklySummary {
  exerciseId: number;
  exerciseNameKo: string;
  exerciseNameEn: string;
  totalSets: number;
  totalReps: number;
  maxWeight: number;
  totalVolume: number;
}

export interface OverloadAchievement {
  exerciseId: number;
  exerciseNameKo: string;
  exerciseNameEn: string;
  previousMaxWeight: number;
  currentMaxWeight: number;
  improvement: number;
}

// --- Pagination ---
// Matches: PageResponse (ApiResponse.kt:28-37)
export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;     // BE: "number" (not "page")
  size: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}
