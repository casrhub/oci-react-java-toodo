import React from 'react';
import { useHistory } from 'react-router-dom';
import '../styles/LoginPage.css'; 

function DevLoginPage() {
  const history = useHistory();

  const handleSSOLogin = () => {
    history.push('/dev-main');
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

export default DevLoginPage;
