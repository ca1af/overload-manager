import { createBrowserRouter, Navigate, Outlet } from 'react-router-dom';
import { useAuthStore } from '@/store/authStore';
import AuthPage from '@/pages/AuthPage';
import DashboardPage from '@/pages/DashboardPage';
import ActiveSessionPage from '@/pages/ActiveSessionPage';
import ExerciseSelectPage from '@/pages/ExerciseSelectPage';
import SetRecordPage from '@/pages/SetRecordPage';
import ReportPage from '@/pages/ReportPage';

function ProtectedRoute() {
  const token = useAuthStore((s) => s.accessToken);
  if (!token) {
    return <Navigate to="/auth" replace />;
  }
  return <Outlet />;
}

function PublicOnlyRoute() {
  const token = useAuthStore((s) => s.accessToken);
  if (token) {
    return <Navigate to="/" replace />;
  }
  return <Outlet />;
}

export const router = createBrowserRouter([
  {
    element: <PublicOnlyRoute />,
    children: [
      { path: '/auth', element: <AuthPage /> },
    ],
  },
  {
    element: <ProtectedRoute />,
    children: [
      { path: '/', element: <DashboardPage /> },
      { path: '/sessions/:sessionId', element: <ActiveSessionPage /> },
      {
        path: '/sessions/:sessionId/exercises/add',
        element: <ExerciseSelectPage />,
      },
      {
        path: '/sessions/:sessionId/exercises/:sessionExerciseId',
        element: <SetRecordPage />,
      },
      { path: '/report', element: <ReportPage /> },
    ],
  },
  {
    path: '*',
    element: <Navigate to="/" replace />,
  },
]);
