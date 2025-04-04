import React from 'react';
import { HashRouter as Router, Routes, Route } from 'react-router-dom';

// Pages
import LandingPage from '../pages/LandingPage';
import DevLoginPage from '../pages/DevLoginPage';
import ManagerLoginPage from '../pages/ManagerLoginPage';
import DevMainMenuPage from '../pages/DevMainMenuPage';
import ManagerMainMenuPage from '../pages/ManagerMainMenuPage';
import Home from '../pages/Home';
import DevTasksPage from '../pages/DevTasksPage';
import ManagerTasksPage from '../pages/ManagerTasksPage';
import ManagerKpisPage from '../pages/ManagerKpisPage';

function AppRouter() {
  return (
    <Router>
      <Routes>
        {/* Landing */}
        <Route path="/" element={<LandingPage />} />

        {/* Role-specific Login */}
        <Route path="/dev-login" element={<DevLoginPage />} />
        <Route path="/manager-login" element={<ManagerLoginPage />} />

        {/* Role-specific Main Menus */}
        <Route path="/dev-main" element={<DevMainMenuPage />} />
        <Route path="/manager-main" element={<ManagerMainMenuPage />} />

        {/* Pages */}
        <Route path="/home" element={<Home />} />
        <Route path="/dev/tasks" element={<DevTasksPage />} />
        <Route path="/manager/tasks" element={<ManagerTasksPage />} />
        <Route path="/manager/kpis" element={<ManagerKpisPage />} />
      </Routes>
    </Router>
  );
}

export default AppRouter;
