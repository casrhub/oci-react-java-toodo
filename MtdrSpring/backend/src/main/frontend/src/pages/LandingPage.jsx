import React from 'react';
import { useHistory } from 'react-router-dom';
import './LandingPage.css';

function LandingPage() {
  const history = useHistory();

  const handleClick = () => {
    history.push('/home');
  };

  return (
    <div className="landing-page">
      <h1 className="landing-title">Welcome</h1>
      <button className="landing-button" onClick={handleClick}>
        I’m a developer
      </button>
      <button className="landing-button" onClick={handleClick}>
        I’m a manager
      </button>
    </div>
  );
}

export default LandingPage;
