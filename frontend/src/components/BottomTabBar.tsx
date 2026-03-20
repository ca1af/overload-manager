import { useNavigate, useLocation } from 'react-router-dom';
import { Home, Dumbbell, ClipboardList, BarChart3 } from 'lucide-react';
import { cn } from '@/utils/cn';

const tabs = [
  { path: '/', label: '홈', icon: Home },
  { path: '/sessions/new', label: '운동', icon: Dumbbell },
  { path: '/history', label: '기록', icon: ClipboardList },
  { path: '/report', label: '통계', icon: BarChart3 },
] as const;

export function BottomTabBar() {
  const navigate = useNavigate();
  const location = useLocation();

  return (
    <nav className="fixed bottom-0 left-0 right-0 z-40 border-t bg-background">
      <div className="mx-auto flex h-16 max-w-lg items-center justify-around">
        {tabs.map((tab) => {
          const isActive =
            tab.path === '/'
              ? location.pathname === '/'
              : location.pathname.startsWith(tab.path);
          const Icon = tab.icon;
          return (
            <button
              key={tab.path}
              onClick={() => {
                if (tab.path === '/sessions/new') {
                  navigate('/');
                } else {
                  navigate(tab.path);
                }
              }}
              className={cn(
                'flex min-h-12 min-w-12 flex-col items-center justify-center gap-0.5 rounded-lg px-3 py-1',
                isActive ? 'text-primary' : 'text-muted-foreground',
              )}
            >
              <Icon className="h-5 w-5" />
              <span className="text-xs">{tab.label}</span>
            </button>
          );
        })}
      </div>
    </nav>
  );
}
