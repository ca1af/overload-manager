import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { LoginForm } from '@/features/auth/LoginForm';
import { RegisterForm } from '@/features/auth/RegisterForm';

export default function AuthPage() {
  return (
    <div className="flex min-h-dvh flex-col items-center justify-center px-4">
      <div className="w-full max-w-[480px]">
        <div className="mb-8 text-center">
          <h1 className="text-2xl font-bold text-primary">Overload</h1>
          <p className="mt-1 text-sm text-muted-foreground">
            점진적 과부하 트래킹
          </p>
        </div>

        <Tabs defaultValue="login" className="w-full">
          <TabsList className="grid w-full grid-cols-2">
            <TabsTrigger value="login">로그인</TabsTrigger>
            <TabsTrigger value="register">회원가입</TabsTrigger>
          </TabsList>
          <TabsContent value="login" className="mt-6">
            <LoginForm />
          </TabsContent>
          <TabsContent value="register" className="mt-6">
            <RegisterForm />
          </TabsContent>
        </Tabs>
      </div>
    </div>
  );
}
