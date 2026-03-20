import { useNavigate, useLocation } from 'react-router-dom';
import { ArrowLeft } from 'lucide-react';
import { Button } from '@/components/ui/button';

interface AppHeaderProps {
  title?: string;
  showBack?: boolean;
  rightAction?: React.ReactNode;
}

export function AppHeader({ title, showBack = false, rightAction }: AppHeaderProps) {
  const navigate = useNavigate();
  const location = useLocation();

  const isHome = location.pathname === '/';

  return (
    <header className="sticky top-0 z-40 flex h-14 items-center border-b bg-background px-4">
      <div className="flex flex-1 items-center gap-2">
        {showBack && (
          <Button variant="ghost" size="icon" onClick={() => navigate(-1)}>
            <ArrowLeft className="h-5 w-5" />
          </Button>
        )}
        {isHome && !title ? (
          <span className="text-lg font-bold text-primary">Overload</span>
        ) : (
          <h1 className="text-base font-semibold">{title}</h1>
        )}
      </div>
      {rightAction && <div>{rightAction}</div>}
    </header>
  );
}
