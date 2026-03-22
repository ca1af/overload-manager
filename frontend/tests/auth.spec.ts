import { test, expect } from '@playwright/test';
import { generateTestUser, registerAndLogin } from './helpers';

test.describe('Auth - Register', () => {
  test('should display register form when register tab is clicked', async ({ page }) => {
    await page.goto('/auth');
    await page.getByRole('tab', { name: '회원가입' }).click();

    await expect(page.locator('#reg-nickname')).toBeVisible();
    await expect(page.locator('#reg-email')).toBeVisible();
    await expect(page.locator('#reg-password')).toBeVisible();
    await expect(page.locator('#reg-confirm')).toBeVisible();
    await expect(page.getByRole('button', { name: '회원가입' })).toBeVisible();
  });

  test('should show validation errors on empty form submit', async ({ page }) => {
    await page.goto('/auth');
    await page.getByRole('tab', { name: '회원가입' }).click();

    await page.getByRole('button', { name: '회원가입' }).click();

    // Validation errors should appear
    await expect(page.getByText('닉네임은 2자 이상이어야 합니다')).toBeVisible();
    await expect(page.getByText('올바른 이메일을 입력해주세요')).toBeVisible();
    await expect(page.getByText('비밀번호는 8자 이상이어야 합니다')).toBeVisible();
  });

  test('should register successfully and navigate to dashboard', async ({ page }) => {
    const user = generateTestUser();

    await page.goto('/auth');
    await page.getByRole('tab', { name: '회원가입' }).click();

    await page.locator('#reg-nickname').fill(user.nickname);
    await page.locator('#reg-email').fill(user.email);
    await page.locator('#reg-password').fill(user.password);
    await page.locator('#reg-confirm').fill(user.password);

    await page.getByRole('button', { name: '회원가입' }).click();

    await page.waitForURL('/', { timeout: 10_000 });
    await expect(page.getByText('안녕하세요')).toBeVisible();
  });
});

test.describe('Auth - Login', () => {
  test('should display login form by default', async ({ page }) => {
    await page.goto('/auth');

    await expect(page.locator('#login-email')).toBeVisible();
    await expect(page.locator('#login-password')).toBeVisible();
    await expect(page.getByRole('button', { name: '로그인' })).toBeVisible();
  });

  test('should login successfully and navigate to dashboard', async ({ page }) => {
    // First register a user
    const user = generateTestUser();
    await page.goto('/auth');
    await page.getByRole('tab', { name: '회원가입' }).click();

    await page.locator('#reg-nickname').fill(user.nickname);
    await page.locator('#reg-email').fill(user.email);
    await page.locator('#reg-password').fill(user.password);
    await page.locator('#reg-confirm').fill(user.password);
    await page.getByRole('button', { name: '회원가입' }).click();
    await page.waitForURL('/', { timeout: 10_000 });

    // Clear auth state by clearing localStorage and reloading
    await page.evaluate(() => localStorage.clear());
    await page.goto('/auth');

    // Now login
    await page.locator('#login-email').fill(user.email);
    await page.locator('#login-password').fill(user.password);
    await page.getByRole('button', { name: '로그인' }).click();

    await page.waitForURL('/', { timeout: 10_000 });
    await expect(page.getByText('안녕하세요')).toBeVisible();
  });
});

test.describe('Auth - Protected Routes', () => {
  test('should redirect to /auth when accessing protected page without auth', async ({ page }) => {
    // Clear any stored auth
    await page.goto('/auth');
    await page.evaluate(() => localStorage.clear());

    await page.goto('/');
    await page.waitForURL('/auth', { timeout: 5_000 });
  });

  test('should redirect to /auth when accessing report page without auth', async ({ page }) => {
    await page.goto('/auth');
    await page.evaluate(() => localStorage.clear());

    await page.goto('/report');
    await page.waitForURL('/auth', { timeout: 5_000 });
  });
});
