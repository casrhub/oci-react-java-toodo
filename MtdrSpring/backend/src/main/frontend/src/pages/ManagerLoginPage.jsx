import React from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/LoginPage.css'; // reuse same styles

function ManagerLoginPage() {
  const history = useNavigate();

  const handleSSOLogin = () => {
    history('/manager-main');
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
