const createDriver  = require('../driver');
const { By, until } = require('selenium-webdriver');

describe('Navegación → Manager Login', () => {
  let driver;
  beforeAll(async () => {
    driver = createDriver();
    await driver.get('http://localhost:8080/#/');
  });
  afterAll(() => driver.quit());

  test('clic en “I’m a manager” lleva a /manager-login', async () => {
    const mgrBtn = await driver.wait(
      until.elementLocated(By.css('.landing-button:nth-of-type(2)')), 10000);
    await mgrBtn.click();

    await driver.wait(until.urlContains('/manager-login'), 5000);
    const title = await driver.wait(
      until.elementLocated(By.css('.login-title')), 5000);

    expect(await title.getText()).toBe('Iniciar Sesión');
  });
});
