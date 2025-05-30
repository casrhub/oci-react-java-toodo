const createDriver      = require('../driver');
const { By, until }     = require('selenium-webdriver');

describe('Landing page – título', () => {
  let driver;
  beforeAll(async () => {
    driver = createDriver();
    await driver.get('http://localhost:3000/#/');
  });
  afterAll(() => driver.quit());

  test('muestra “Welcome”', async () => {
    const title = await driver.wait(
      until.elementLocated(By.css('.landing-title')), 10000);
    expect(await title.getText()).toBe('Welcome');
  });
});
