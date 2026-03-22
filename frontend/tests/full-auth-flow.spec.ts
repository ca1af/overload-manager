import { test, expect } from '@playwright/test';
import { generateTestUser } from './helpers';

test('full flow: register → dashboard → verify auth works', async ({ page }) => {
  const user = generateTestUser();
  
  // 1. Clear any stale auth
  await page.goto('/auth');
  await page.evaluate(() => localStorage.clear());
  await page.reload();
  
  // 2. Should be on auth page
  await expect(page).toHaveURL(/\/auth/);
  
  // 3. Switch to register tab
  await page.getByRole('tab', { name: '회원가입' }).click();
  
  // 4. Fill form
  await page.locator('#reg-nickname').fill(user.nickname);
  await page.locator('#reg-email').fill(user.email);
  await page.locator('#reg-password').fill(user.password);
  await page.locator('#reg-confirm').fill(user.password);
  
  // 5. Listen to register API response
  const regResponse = page.waitForResponse(
    resp => resp.url().includes('/auth/register'),
    { timeout: 10_000 }
  );
  
  await page.getByRole('button', { name: '회원가입' }).click();
  
  const resp = await regResponse;
  const status = resp.status();
  const body = await resp.json();
  console.log('Register status:', status);
  console.log('Register body success:', body.success);
  console.log('Register body has data:', !!body.data);
  console.log('Register body data keys:', body.data ? Object.keys(body.data) : 'N/A');
  
  // 6. Should navigate to dashboard
  await page.waitForURL('/', { timeout: 10_000 });
  await expect(page.getByText('안녕하세요')).toBeVisible({ timeout: 5_000 });
  
  // 7. Verify auth is stored
  const authStorage = await page.evaluate(() => localStorage.getItem('auth-storage'));
  const parsed = JSON.parse(authStorage!);
  console.log('Stored accessToken exists:', !!parsed.state.accessToken);
  console.log('Stored user exists:', !!parsed.state.user);
  
  // 8. Reload and verify still authenticated
  await page.reload();
  await page.waitForURL('/', { timeout: 5_000 });
  await expect(page.getByText('안녕하세요')).toBeVisible({ timeout: 5_000 });
});

test('full flow: register → logout → login → dashboard', async ({ page }) => {
  const user = generateTestUser();
  
  // Clear auth
  await page.goto('/auth');
  await page.evaluate(() => localStorage.clear());
  await page.reload();
  
  // Register
  await page.getByRole('tab', { name: '회원가입' }).click();
  await page.locator('#reg-nickname').fill(user.nickname);
  await page.locator('#reg-email').fill(user.email);
  await page.locator('#reg-password').fill(user.password);
  await page.locator('#reg-confirm').fill(user.password);
  await page.getByRole('button', { name: '회원가입' }).click();
  await page.waitForURL('/', { timeout: 10_000 });
  
  // Clear auth (simulate logout)
  await page.evaluate(() => localStorage.clear());
  await page.goto('/auth');
  
  // Login
  const loginResponse = page.waitForResponse(
    resp => resp.url().includes('/auth/login'),
    { timeout: 10_000 }
  );
  
  await page.locator('#login-email').fill(user.email);
  await page.locator('#login-password').fill(user.password);
  await page.getByRole('button', { name: '로그인' }).click();
  
  const loginResp = await loginResponse;
  console.log('Login status:', loginResp.status());
  
  // Should navigate to dashboard
  await page.waitForURL('/', { timeout: 10_000 });
  await expect(page.getByText('안녕하세요')).toBeVisible({ timeout: 5_000 });
});
