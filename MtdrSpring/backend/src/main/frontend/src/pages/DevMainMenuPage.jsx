import React from 'react';
import { useHistory } from 'react-router-dom';
import './MainMenuPage.css';

function MainMenuPage() {
  const history = useHistory();

  return (
    <div className="main-menu-page">
      <h1 className="main-menu-title">Project Management Tool</h1>
      <div className="button-group">
        <button className="main-menu-button" onClick={() => history.push('/kpis')}>
          KPIs
        </button>
        <button className="main-menu-button" onClick={() => history.push('/dev/tasks')}>
          Tasks
        </button>
      </div>
    </div>
  );
}

export default MainMenuPage;
