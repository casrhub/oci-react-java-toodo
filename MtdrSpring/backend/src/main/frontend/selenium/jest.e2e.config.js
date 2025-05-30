/** Configuración específica para los tests E2E con Selenium */
module.exports = {
  testEnvironment: 'node',          // no usamos JSDOM
  testTimeout:    30000,            // 30 s por test
  maxWorkers:     1,                // se ejecutan en serie → 1 pestaña de Chrome
  testMatch: ['**/selenium/tests/**/*.test.js']
};
