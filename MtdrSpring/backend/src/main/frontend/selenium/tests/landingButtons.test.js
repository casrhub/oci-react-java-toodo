const createDriver  = require('../driver');
const { By, until } = require('selenium-webdriver');

describe('Landing page – botones', () => {
  let driver;
  beforeAll(async () => {
    driver = createDriver();
    await driver.get('http://localhost:3000/#/');
  });
  afterAll(() => driver.quit());

  test('hay dos botones principales', async () => {
    await driver.wait(until.elementsLocated(By.css('.landing-button')), 10000);
    const buttons = await driver.findElements(By.css('.landing-button'));
    expect(buttons.length).toBe(2);
  });
});
