/**
 * Crea un WebDriver de Chrome listo para usar (headless en CI).
 */
require('chromedriver'); // registra el binario en PATH

const { Builder } = require('selenium-webdriver');
const chrome      = require('selenium-webdriver/chrome');

function createDriver () {
  const options = new chrome.Options();

  // En entornos CI/--headless (por ej. GitHub Actions) ahorra recursos.
  if (process.env.CI) options.addArguments('--headless=new');

  return new Builder()
    .forBrowser('chrome')
    .setChromeOptions(options)
    .build();
}

module.exports = createDriver;
