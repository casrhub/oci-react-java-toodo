import React from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/MainMenuPage.css';

function MainMenuPage() {
  const history = useNavigate();

  return (
    <div className="main-menu-page">
      <h1 className="main-menu-title">Project Management Tool</h1>
      <div className="button-group">
        <button className="main-menu-button" onClick={() => history('/kpis')}>
          KPIs
        </button>
        <button className="main-menu-button" onClick={() => history('/dev/tasks')}>
          Tasks
        </button>
      </div>
    </div>
  );
}

export default MainMenuPage;
