// File: MtdrSpring/backend/src/main/frontend/src/pages/LandingPage.jsx
import React from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/LandingPage.css';

function LandingPage() {
  const nav = useNavigate();

  return (
    <div className="landing-page">
      <h1 className="landing-title">Bienvenido</h1>
      <button className="landing-button" onClick={() => nav('/sign-in')}>
        Iniciar sesión / Registrarse
      </button>
    </div>
  );
}

export default LandingPage;
