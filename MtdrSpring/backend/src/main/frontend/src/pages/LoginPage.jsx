import React from 'react';
import { useHistory } from 'react-router-dom'; // <-- Add this line
import './LoginPage.css';

function LoginPage() {
  const history = useHistory(); // <-- Add this line

  const handleSSOLogin = () => {
    // Simulated SSO success → redirect to main menu
    history.push('/main');
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
