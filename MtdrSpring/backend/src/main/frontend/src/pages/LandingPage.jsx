import React from 'react';
import { useHistory } from 'react-router-dom';
import './LandingPage.css';

function LandingPage() {
  const history = useHistory();

  const goToDevLogin = () => {
    history.push('/dev-login');
  };

  const goToManagerLogin = () => {
    history.push('/manager-login');
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
