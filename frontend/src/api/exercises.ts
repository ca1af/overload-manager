import client from './client';
import type {
  Exercise,
  ExerciseCategory,
  ExerciseHistoryEntry,
  PageResponse,
  PreviousSessionData,
} from '@/types/domain';

export async function getExercises(params?: {
  category?: ExerciseCategory;
  keyword?: string;
  page?: number;
  size?: number;
}): Promise<PageResponse<Exercise>> {
  const res = await client.get<PageResponse<Exercise>>('/exercises', {
    params,
  });
  return res.data;
}

export async function getExercise(id: number): Promise<Exercise> {
  const res = await client.get<Exercise>(`/exercises/${id}`);
  return res.data;
}

export async function getPreviousSession(
  exerciseId: number,
): Promise<PreviousSessionData> {
  const res = await client.get<PreviousSessionData>(
    `/exercises/${exerciseId}/previous-session`,
  );
  return res.data;
}

export async function createExercise(data: {
  nameKo: string;
  category: ExerciseCategory;
}): Promise<Exercise> {
  const res = await client.post<Exercise>('/exercises', data);
  return res.data;
}

export async function getExerciseHistory(
  exerciseId: number,
  params?: { page?: number; size?: number },
): Promise<PageResponse<ExerciseHistoryEntry>> {
  const res = await client.get<PageResponse<ExerciseHistoryEntry>>(
    `/exercises/${exerciseId}/history`,
    { params },
  );
  return res.data;
}
