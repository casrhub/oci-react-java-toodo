import React from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/LoginPage.css'; 

function DevLoginPage() {
  const navigate = useNavigate();

  const handleSSOLogin = () => {
    navigate('/dev-main');
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
