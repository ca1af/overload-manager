import { test, expect } from '@playwright/test';
import { registerAndLogin } from './helpers';

test.describe('Report Page', () => {
  test.beforeEach(async ({ page }) => {
    await registerAndLogin(page);
    await page.getByText('통계').click();
    await page.waitForURL('/report', { timeout: 5_000 });
  });

  test('should display report page with tabs', async ({ page }) => {
    await expect(page.getByText('운동별')).toBeVisible();
    await expect(page.getByText('주간 요약')).toBeVisible();
  });

  test('should show exercise select dropdown on exercise tab', async ({ page }) => {
    await expect(page.locator('select')).toBeVisible();
    await expect(page.locator('option', { hasText: '운동을 선택하세요' })).toBeAttached();
  });

  test.fixme('should load exercises in dropdown (BUG-12: API param mismatch keyword vs search)', async ({ page }) => {
    const options = page.locator('select option');
    const count = await options.count();
    expect(count).toBeGreaterThan(1);
  });

  test('should display weekly summary tab without crash', async ({ page }) => {
    await page.getByText('주간 요약').click();

    // Should show week navigation and summary data
    await expect(page.getByText('운동 횟수')).toBeVisible({ timeout: 5_000 });
    await expect(page.getByText('총 세트')).toBeVisible();
    await expect(page.getByText('총 볼륨')).toBeVisible();
  });
});
