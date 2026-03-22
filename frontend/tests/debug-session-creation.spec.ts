import { test, expect } from '@playwright/test';
import { registerAndLogin } from './helpers';

test('debug: create session button full flow', async ({ page }) => {
  // Register and login
  await registerAndLogin(page);
  await expect(page.getByRole('button', { name: '오늘 운동 시작' })).toBeVisible();

  // Listen to network requests
  const responsePromise = page.waitForResponse(
    (resp) => resp.url().includes('/api/v1/sessions') && resp.request().method() === 'POST',
    { timeout: 10_000 }
  );

  // Click the button
  await page.getByRole('button', { name: '오늘 운동 시작' }).click();

  // Wait for the API response
  const response = await responsePromise;
  const status = response.status();
  const body = await response.json();
  console.log('Response status:', status);
  console.log('Response body:', JSON.stringify(body));

  // Check if navigated to session page
  await page.waitForURL(/\/sessions\/\d+/, { timeout: 10_000 });
  console.log('Final URL:', page.url());
});
