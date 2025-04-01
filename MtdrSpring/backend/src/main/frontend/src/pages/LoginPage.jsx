import React from 'react';
import './LoginPage.css';

function LoginPage() {
  const handleSSOLogin = () => {
    // TODO: Replace with real SSO redirect or API call
    window.location.href = 'https://oracle.com/sso/login'; // placeholder
  };

  return (
    <div className="login-page">
      <h1 className="login-title">Iniciar Sesión</h1>
      <button className="login-button" onClick={handleSSOLogin}>
        Iniciar Sesión Con Oracle SSO
      </button>
    </div>
  );
}

export default LoginPage;
