import React from 'react';
import { useHistory } from 'react-router-dom';
import '../styles/LoginPage.css'; // reuse same styles

function ManagerLoginPage() {
  const history = useHistory();

  const handleSSOLogin = () => {
    history.push('/manager-main');
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

export default ManagerLoginPage;
