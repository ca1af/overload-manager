import client from './client';
import type { WeeklySummary } from '@/types/domain';

export async function getWeeklySummary(weekStart?: string): Promise<WeeklySummary> {
  const res = await client.get<WeeklySummary>('/reports/weekly-summary', {
    params: weekStart ? { weekStart } : undefined,
  });
  return res.data;
}
