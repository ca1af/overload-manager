import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useMutation } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { register as registerApi } from '@/api/auth';
import { useAuthStore } from '@/store/authStore';

const registerSchema = z
  .object({
    nickname: z
      .string()
      .min(2, '닉네임은 2자 이상이어야 합니다')
      .max(20, '닉네임은 20자 이하여야 합니다'),
    email: z.string().email('올바른 이메일을 입력해주세요'),
    password: z
      .string()
      .min(8, '비밀번호는 8자 이상이어야 합니다')
      .regex(/[A-Za-z]/, '영문자를 포함해야 합니다')
      .regex(/[0-9]/, '숫자를 포함해야 합니다'),
    confirmPassword: z.string(),
    weightUnit: z.enum(['kg', 'lb']),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: '비밀번호가 일치하지 않습니다',
    path: ['confirmPassword'],
  });

type RegisterFormData = z.infer<typeof registerSchema>;

export function RegisterForm() {
  const navigate = useNavigate();
  const setAuth = useAuthStore((s) => s.setAuth);

  const {
    register,
    handleSubmit,
    formState: { errors },
    watch,
  } = useForm<RegisterFormData>({
    resolver: zodResolver(registerSchema),
    defaultValues: {
      weightUnit: 'kg',
    },
  });

  const selectedUnit = watch('weightUnit');

  const mutation = useMutation({
    mutationFn: registerApi,
    onSuccess: (data) => {
      setAuth(data);
      navigate('/');
    },
  });

  const onSubmit = (data: RegisterFormData) => {
    const { confirmPassword: _, ...rest } = data;
    mutation.mutate(rest);
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <div className="space-y-2">
        <Label htmlFor="reg-nickname">닉네임</Label>
        <Input
          id="reg-nickname"
          placeholder="닉네임"
          {...register('nickname')}
        />
        {errors.nickname && (
          <p className="text-sm text-destructive">{errors.nickname.message}</p>
        )}
      </div>

      <div className="space-y-2">
        <Label htmlFor="reg-email">이메일</Label>
        <Input
          id="reg-email"
          type="email"
          placeholder="email@example.com"
          {...register('email')}
        />
        {errors.email && (
          <p className="text-sm text-destructive">{errors.email.message}</p>
        )}
      </div>

      <div className="space-y-2">
        <Label htmlFor="reg-password">비밀번호</Label>
        <Input
          id="reg-password"
          type="password"
          placeholder="8자 이상, 영문+숫자"
          {...register('password')}
        />
        {errors.password && (
          <p className="text-sm text-destructive">{errors.password.message}</p>
        )}
      </div>

      <div className="space-y-2">
        <Label htmlFor="reg-confirm">비밀번호 확인</Label>
        <Input
          id="reg-confirm"
          type="password"
          placeholder="비밀번호 확인"
          {...register('confirmPassword')}
        />
        {errors.confirmPassword && (
          <p className="text-sm text-destructive">{errors.confirmPassword.message}</p>
        )}
      </div>

      <div className="space-y-2">
        <Label>무게 단위</Label>
        <div className="flex gap-4">
          <label className="flex items-center gap-2">
            <input
              type="radio"
              value="kg"
              {...register('weightUnit')}
              className="h-4 w-4 accent-primary"
            />
            <span className={selectedUnit === 'kg' ? 'font-medium text-primary' : ''}>
              kg
            </span>
          </label>
          <label className="flex items-center gap-2">
            <input
              type="radio"
              value="lb"
              {...register('weightUnit')}
              className="h-4 w-4 accent-primary"
            />
            <span className={selectedUnit === 'lb' ? 'font-medium text-primary' : ''}>
              lb
            </span>
          </label>
        </div>
      </div>

      {mutation.isError && (
        <p className="text-sm text-destructive">
          회원가입에 실패했습니다. 다시 시도해주세요.
        </p>
      )}

      <Button type="submit" size="lg" className="w-full" disabled={mutation.isPending}>
        {mutation.isPending ? '가입 중...' : '회원가입'}
      </Button>
    </form>
  );
}
