import React from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/LandingPage.css';

function LandingPage() {
  const nav = useNavigate();

  return (
    <div className="landing-page">
      <h1 className="landing-title">Welcome</h1>
      <button className="landing-button" onClick={() => nav('/sign-in')}>
        Sign In / Sign Up
      </button>
    </div>
  );
}

export default LandingPage;
