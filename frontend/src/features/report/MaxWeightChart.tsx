import {
  ResponsiveContainer,
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
} from 'recharts';
import type { ExerciseHistoryEntry } from '@/types/domain';
import { useAuthStore } from '@/store/authStore';
import { convertWeight } from '@/utils/weight';
import { formatDate } from '@/utils/format';

interface MaxWeightChartProps {
  history: ExerciseHistoryEntry[];
}

export function MaxWeightChart({ history }: MaxWeightChartProps) {
  const user = useAuthStore((s) => s.user);
  const unit = user?.weightUnit ?? 'kg';

  const data = [...history].reverse().map((entry) => ({
    date: formatDate(entry.sessionDate, 'M/d'),
    maxWeight: convertWeight(entry.maxWeightKg, unit),
    volume: convertWeight(entry.totalVolumeKg, unit),
  }));

  if (data.length === 0) {
    return (
      <div className="flex h-48 items-center justify-center rounded-xl border">
        <p className="text-sm text-muted-foreground">기록이 없습니다</p>
      </div>
    );
  }

  return (
    <div className="h-48">
      <ResponsiveContainer width="100%" height="100%">
        <LineChart data={data} margin={{ top: 5, right: 5, left: -20, bottom: 5 }}>
          <CartesianGrid strokeDasharray="3 3" stroke="#E5E7EB" />
          <XAxis dataKey="date" tick={{ fontSize: 11 }} />
          <YAxis tick={{ fontSize: 11 }} />
          <Tooltip
            contentStyle={{
              borderRadius: '12px',
              border: '1px solid #E5E7EB',
              fontSize: '12px',
            }}
            formatter={(value) => [`${value} ${unit}`, '최고 무게']}
          />
          <Line
            type="monotone"
            dataKey="maxWeight"
            stroke="#4F46E5"
            strokeWidth={2}
            dot={{ fill: '#4F46E5', r: 3 }}
            activeDot={{ r: 5 }}
          />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
}
