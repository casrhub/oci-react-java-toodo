const createDriver  = require('../driver');
const { By, until } = require('selenium-webdriver');

describe('Navegación → Dev Login', () => {
  let driver;
  beforeAll(async () => {
    driver = createDriver();
    await driver.get('http://localhost:8080/#/');
  });
  afterAll(() => driver.quit());

  test('clic en “I’m a developer” lleva a /dev-login', async () => {
    const devBtn = await driver.wait(
      until.elementLocated(By.css('.landing-button:nth-of-type(1)')), 10000);
    await devBtn.click();

    // Espera a que cambie la URL y se vea el título de login.
    await driver.wait(until.urlContains('/dev-login'), 5000);
    const title = await driver.wait(
      until.elementLocated(By.css('.login-title')), 5000);

    expect(await title.getText()).toBe('Iniciar Sesión');
  });
});
