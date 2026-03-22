import { createBrowserRouter, Navigate, Outlet } from 'react-router-dom';
import { useAuthStore } from '@/store/authStore';
import AuthPage from '@/pages/AuthPage';
import DashboardPage from '@/pages/DashboardPage';
import ActiveSessionPage from '@/pages/ActiveSessionPage';
import ExerciseSelectPage from '@/pages/ExerciseSelectPage';
import SetRecordPage from '@/pages/SetRecordPage';
import ReportPage from '@/pages/ReportPage';
import ExercisesPage from '@/pages/ExercisesPage';
import HistoryPage from '@/pages/HistoryPage';

function isTokenValid(token: string | null): boolean {
  if (!token) return false;
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    // Check expiry with 30s buffer
    return payload.exp * 1000 > Date.now() - 30_000;
  } catch {
    return false;
  }
}

function ProtectedRoute() {
  const token = useAuthStore((s) => s.accessToken);
  if (!isTokenValid(token)) {
    // Clear stale auth if token exists but is invalid
    if (token) {
      useAuthStore.getState().clearAuth();
    }
    return <Navigate to="/auth" replace />;
  }
  return <Outlet />;
}

function PublicOnlyRoute() {
  const token = useAuthStore((s) => s.accessToken);
  if (isTokenValid(token)) {
    return <Navigate to="/" replace />;
  }
  // Clear stale auth if token exists but expired
  if (token) {
    useAuthStore.getState().clearAuth();
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
      { path: '/exercises', element: <ExercisesPage /> },
      { path: '/history', element: <HistoryPage /> },
      { path: '/report', element: <ReportPage /> },
    ],
  },
  {
    path: '*',
    element: <Navigate to="/" replace />,
  },
]);
