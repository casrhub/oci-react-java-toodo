const createDriver  = require('../driver');
const { By, until } = require('selenium-webdriver');

describe('Dev Login – botón SSO', () => {
  let driver;
  beforeAll(async () => {
    driver = createDriver();
    await driver.get('http://localhost:3000/#/dev-login');
  });
  afterAll(() => driver.quit());

  test('el botón de SSO tiene el texto correcto', async () => {
    const btn = await driver.wait(
      until.elementLocated(By.css('.login-button')), 10000);

    expect(await btn.getText())
      .toBe('Iniciar Sesión Con Oracle SSO');
  });
});
