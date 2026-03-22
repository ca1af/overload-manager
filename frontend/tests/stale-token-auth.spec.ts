import { test, expect } from '@playwright/test';
import { generateTestUser } from './helpers';

test('should allow register even with stale token in localStorage', async ({ page }) => {
  // Inject stale/expired token
  await page.goto('/auth');
  await page.evaluate(() => {
    localStorage.setItem('auth-storage', JSON.stringify({
      state: {
        accessToken: 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI5OTkiLCJlbWFpbCI6Im9sZEB0ZXN0LmNvbSIsImlhdCI6MTAwMDAwMDAwMCwiZXhwIjoxMDAwMDAwMDAxfQ.fake',
        refreshToken: 'expired-refresh',
        user: { id: 999, email: 'old@test.com', nickname: 'old', weightUnit: 'kg' }
      },
      version: 0
    }));
  });
  
  // Reload - stale token should be cleared, should stay on /auth
  await page.goto('/auth');
  await page.waitForURL('/auth', { timeout: 5_000 });
  
  // Register should work
  const user = generateTestUser();
  await page.getByRole('tab', { name: '회원가입' }).click();
  await page.locator('#reg-nickname').fill(user.nickname);
  await page.locator('#reg-email').fill(user.email);
  await page.locator('#reg-password').fill(user.password);
  await page.locator('#reg-confirm').fill(user.password);
  await page.getByRole('button', { name: '회원가입' }).click();
  
  await page.waitForURL('/', { timeout: 10_000 });
  await expect(page.getByText('안녕하세요')).toBeVisible({ timeout: 5_000 });
});
