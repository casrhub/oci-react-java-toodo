import React from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/LandingPage.css';

function LandingPage() {
  const history = useNavigate();

  const goToDevLogin = () => {
    history('/dev-login');
  };

  const goToManagerLogin = () => {
    history('/manager-login');
  };

  return (
    <div className="landing-page">
      <h1 className="landing-title">Welcome</h1>
      <button className="landing-button" onClick={goToDevLogin}>
        I’m a developer
      </button>
      <button className="landing-button" onClick={goToManagerLogin}>
        I’m a manager
      </button>
    </div>
  );
}

export default LandingPage;
