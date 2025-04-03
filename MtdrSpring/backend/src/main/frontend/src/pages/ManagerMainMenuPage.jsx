import React from 'react';
import { useHistory } from 'react-router-dom';
import '../styles/MainMenuPage.css'; 

function ManagerMainMenuPage() {
  const history = useHistory();

  return (
    <div className="main-menu-page">
      <h1 className="main-menu-title">Project Management Tool</h1>
      <div className="button-group">
        <button className="main-menu-button" onClick={() => history.push('/manager/kpis')}>
          KPIs
        </button>
        <button className="main-menu-button" onClick={() => history.push('/manager/tasks')}>
          Tasks
        </button>
      </div>
    </div>
  );
}

export default ManagerMainMenuPage;
