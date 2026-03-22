import { test, expect } from '@playwright/test';
import { registerAndLogin } from './helpers';

test.describe('Dashboard', () => {
  test.beforeEach(async ({ page }) => {
    await registerAndLogin(page);
  });

  test('should show greeting and date after login', async ({ page }) => {
    await expect(page.getByText('안녕하세요')).toBeVisible();
    // Date should be visible (Korean format like "3월 20일 목요일")
    await expect(page.getByText(/\d+월 \d+일/)).toBeVisible();
  });

  test('should show weekly summary card', async ({ page }) => {
    // Weekly summary card with exercise count, volume, overload stats
    await expect(page.getByText('운동 횟수')).toBeVisible();
    await expect(page.getByText('총 볼륨')).toBeVisible();
    await expect(page.getByText('과부하 달성')).toBeVisible();
  });

  test('should show start workout button', async ({ page }) => {
    await expect(page.getByRole('button', { name: '오늘 운동 시작' })).toBeVisible();
  });

  test('should show bottom tab bar with home and report tabs', async ({ page }) => {
    const tabBar = page.locator('nav');
    await expect(tabBar).toBeVisible();

    await expect(page.getByText('홈')).toBeVisible();
    await expect(page.getByText('통계')).toBeVisible();
  });

  test('should navigate to report page via tab bar', async ({ page }) => {
    await page.getByText('통계').click();
    await page.waitForURL('/report', { timeout: 5_000 });
    await expect(page.getByText('운동별')).toBeVisible();
  });

  test('should navigate back to home via tab bar', async ({ page }) => {
    // Go to report first
    await page.getByText('통계').click();
    await page.waitForURL('/report', { timeout: 5_000 });

    // Navigate back to home
    await page.getByText('홈').click();
    await page.waitForURL('/', { timeout: 5_000 });
    await expect(page.getByText('안녕하세요')).toBeVisible();
  });

  test('should navigate to exercises tab', async ({ page }) => {
    await page.getByRole('button', { name: '운동', exact: true }).click();
    await page.waitForURL('/exercises', { timeout: 5_000 });
  });

  test('should navigate to history tab', async ({ page }) => {
    await page.getByText('기록').click();
    await page.waitForURL('/history', { timeout: 5_000 });
  });

  test('should create session successfully', async ({ page }) => {
    await page.getByRole('button', { name: '오늘 운동 시작' }).click();
    await page.waitForURL(/\/sessions\/\d+/, { timeout: 10_000 });
  });
});
