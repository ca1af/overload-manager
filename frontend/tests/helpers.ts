import { type Page } from '@playwright/test';

let userCounter = 0;

export function generateTestUser() {
  userCounter++;
  const ts = Date.now();
  return {
    nickname: `tester${ts}`,
    email: `tester${ts}_${userCounter}@test.com`,
    password: 'Test1234!',
  };
}

export async function registerAndLogin(page: Page) {
  const user = generateTestUser();

  await page.goto('/auth');

  // Click register tab
  await page.getByRole('tab', { name: '회원가입' }).click();

  // Fill registration form
  await page.locator('#reg-nickname').fill(user.nickname);
  await page.locator('#reg-email').fill(user.email);
  await page.locator('#reg-password').fill(user.password);
  await page.locator('#reg-confirm').fill(user.password);

  // Submit
  await page.getByRole('button', { name: '회원가입' }).click();

  // Wait for navigation to dashboard
  await page.waitForURL('/', { timeout: 10_000 });

  return user;
}
