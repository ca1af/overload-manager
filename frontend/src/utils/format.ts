import { format, parseISO } from 'date-fns';
import { ko } from 'date-fns/locale';

export function formatDate(dateStr: string, pattern: string = 'yyyy.MM.dd'): string {
  return format(parseISO(dateStr), pattern, { locale: ko });
}

export function formatDuration(seconds: number): string {
  const h = Math.floor(seconds / 3600);
  const m = Math.floor((seconds % 3600) / 60);
  const s = seconds % 60;
  if (h > 0) {
    return `${h}:${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`;
  }
  return `${m}:${String(s).padStart(2, '0')}`;
}

export function formatDateKo(dateStr: string): string {
  return format(parseISO(dateStr), 'M/d (EEE)', { locale: ko });
}
