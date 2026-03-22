import client from './client';
import type { WorkoutSession, WorkoutSessionSummary, WorkoutSet, PageResponse } from '@/types/domain';

export interface AddExerciseResult {
  id: number;
  exerciseId: number;
  orderIndex: number;
}

export async function getSessions(params?: {
  page?: number;
  size?: number;
}): Promise<PageResponse<WorkoutSessionSummary>> {
  const res = await client.get<PageResponse<WorkoutSessionSummary>>('/sessions', {
    params,
  });
  return res.data;
}

export async function createSession(): Promise<WorkoutSession> {
  const now = new Date();
  const sessionDate = now.toISOString().split('T')[0];
  const startedAt = now.toISOString().replace('Z', '');
  const res = await client.post<WorkoutSession>('/sessions', {
    sessionDate,
    startedAt,
  });
  return res.data;
}

export async function getSession(id: number): Promise<WorkoutSession> {
  const res = await client.get<WorkoutSession>(`/sessions/${id}`);
  return res.data;
}

export async function updateSession(
  id: number,
  data: { notes?: string; finishedAt?: string },
): Promise<WorkoutSession> {
  const res = await client.patch<WorkoutSession>(`/sessions/${id}`, data);
  return res.data;
}

export async function deleteSession(id: number): Promise<void> {
  await client.delete(`/sessions/${id}`);
}

export async function addExercisesToSession(
  sessionId: number,
  exerciseIds: number[],
): Promise<AddExerciseResult[]> {
  const res = await client.post<AddExerciseResult[]>(
    `/sessions/${sessionId}/exercises`,
    { exerciseIds },
  );
  return res.data;
}

export async function removeExerciseFromSession(
  sessionId: number,
  sessionExerciseId: number,
): Promise<void> {
  await client.delete(
    `/sessions/${sessionId}/exercises/${sessionExerciseId}`,
  );
}

export async function createSet(
  sessionId: number,
  sessionExerciseId: number,
  data: { weight: number; reps: number },
): Promise<WorkoutSet> {
  const res = await client.post<WorkoutSet>(
    `/sessions/${sessionId}/exercises/${sessionExerciseId}/sets`,
    data,
  );
  return res.data;
}

export async function updateSet(
  sessionId: number,
  sessionExerciseId: number,
  setId: number,
  data: { weight?: number; reps?: number; completed?: boolean; restSeconds?: number },
): Promise<WorkoutSet> {
  const res = await client.patch<WorkoutSet>(
    `/sessions/${sessionId}/exercises/${sessionExerciseId}/sets/${setId}`,
    data,
  );
  return res.data;
}

export async function deleteSet(
  sessionId: number,
  sessionExerciseId: number,
  setId: number,
): Promise<void> {
  await client.delete(
    `/sessions/${sessionId}/exercises/${sessionExerciseId}/sets/${setId}`,
  );
}
