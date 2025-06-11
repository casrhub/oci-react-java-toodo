const createDriver = require('../driver');
const { By, until } = require('selenium-webdriver');

describe('Sign In Page', () => {
  let driver;
  beforeAll(async () => {
    driver = createDriver();
    await driver.get('http://localhost:3000/#/sign-in');
  });

  afterAll(() => driver.quit());

  test('muestra el título Sign In', async () => {
    const title = await driver.wait(until.elementLocated(By.css('h5')), 10000);
    expect(await title.getText()).toBe('Sign In');
  });

  test('tiene campos email y password y botón SIGN IN', async () => {
    const emailField = await driver.findElement(By.css('input[type="email"]'));
    const passField = await driver.findElement(By.css('input[type="password"]'));
    const submitBtn = await driver.findElement(By.css('button[type="submit"]'));
    expect(emailField).toBeDefined();
    expect(passField).toBeDefined();
    expect(await submitBtn.getText()).toBe('SIGN IN');
  });
});
