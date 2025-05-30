require('chromedriver');

const { Builder } = require('selenium-webdriver');
const chrome      = require('selenium-webdriver/chrome');

function createDriver () {
  const options = new chrome.Options();

  if (process.env.CI) {
    options.addArguments(
      '--headless=new',
      '--no-sandbox',
      '--disable-dev-shm-usage',
      '--disable-gpu',
      '--window-size=1920,1080'
    );
  }

  return new Builder()
    .forBrowser('chrome')
    .setChromeOptions(options)
    .build();
}

module.exports = createDriver;