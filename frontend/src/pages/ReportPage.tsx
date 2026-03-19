import { AppHeader } from '@/components/AppHeader';
import { BottomTabBar } from '@/components/BottomTabBar';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { ExerciseReportView } from '@/features/report/ExerciseReportView';
import { WeeklySummaryView } from '@/features/report/WeeklySummaryView';

export default function ReportPage() {
  return (
    <div className="flex min-h-dvh flex-col pb-20">
      <AppHeader title="통계" />

      <main className="flex-1 px-4 py-4">
        <Tabs defaultValue="exercise" className="w-full">
          <TabsList className="grid w-full grid-cols-2">
            <TabsTrigger value="exercise">운동별</TabsTrigger>
            <TabsTrigger value="weekly">주간 요약</TabsTrigger>
          </TabsList>
          <TabsContent value="exercise" className="mt-4">
            <ExerciseReportView />
          </TabsContent>
          <TabsContent value="weekly" className="mt-4">
            <WeeklySummaryView />
          </TabsContent>
        </Tabs>
      </main>

      <BottomTabBar />
    </div>
  );
}
