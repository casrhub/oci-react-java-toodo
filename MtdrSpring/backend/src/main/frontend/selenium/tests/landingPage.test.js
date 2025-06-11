const createDriver = require('../driver');
const { By, until } = require('selenium-webdriver');

describe('Landing Page', () => {
  let driver;
  beforeAll(async () => {
    driver = createDriver();
    await driver.get('http://localhost:3000/#/');
  });

  afterAll(() => driver.quit());

  test('muestra el título Welcome', async () => {
    const title = await driver.wait(until.elementLocated(By.css('.landing-title')), 10000);
    expect(await title.getText()).toBe('Welcome');
  });

  test('hay un solo botón Sign In / Sign Up con texto correcto', async () => {
    const buttons = await driver.findElements(By.css('.landing-button'));
    expect(buttons.length).toBe(1);
    expect(await buttons[0].getText()).toBe('Sign In / Sign Up');
  });

  test('clic en el botón navega a /sign-in', async () => {
    const btn = await driver.findElement(By.css('.landing-button'));
    await btn.click();
    await driver.wait(until.urlContains('/sign-in'), 5000);
  });
});
