import { expect, test } from '@playwright/test';

const routes = [
  '/',
  '/requirements/new',
  '/builds/bg-1001',
  '/self-quote',
  '/builds/bg-1001/change-part',
  '/my/quotes',
  '/support/new',
  '/support/AS-1031',
  '/login',
  '/signup',
  '/admin',
  '/admin/agent-sessions/demo-session',
  '/admin/parts',
  '/admin/as-tickets'
];

for (const route of routes) {
  test(`renders ${route}`, async ({ page }) => {
    await page.goto(route);
    await expect(page.locator('body')).toContainText('BuildGraph');
    await expect(page.getByRole('main')).toBeVisible();
  });
}
