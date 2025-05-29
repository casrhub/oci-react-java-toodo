import React from 'react';
import { HashRouter as Router, Routes, Route } from 'react-router-dom';
import ProtectedRoute from './ProtectedRoute';

// Clerk Auth Pages
import { SignIn, SignUp } from '@clerk/clerk-react';

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
import UserKpisPage from '../pages/UserKpisPage';
import TeamKpisPage from "../pages/TeamKpisPage";
import ManagerInsightsPage from '../pages/ManagerInsightsPage';

function AppRouter() {
  return (
    <Router>
      <Routes>
        {/* Public Pages */}
        <Route path="/" element={<LandingPage />} />
        <Route path="/sign-in" element={<SignIn routing="hash" path="/sign-in" />} />
        <Route path="/sign-up" element={<SignUp routing="hash" path="/sign-up" />} />
        <Route path="/dev-login" element={<DevLoginPage />} />
        <Route path="/manager-login" element={<ManagerLoginPage />} />

        {/* Protected Pages */}
        <Route path="/dev-main" element={
          <ProtectedRoute>
            <DevMainMenuPage />
          </ProtectedRoute>
        } />
        <Route path="/manager-main" element={
          <ProtectedRoute>
            <ManagerMainMenuPage />
          </ProtectedRoute>
        } />
        <Route path="/home" element={
          <ProtectedRoute>
            <Home />
          </ProtectedRoute>
        } />
        <Route path="/dev/tasks" element={
          <ProtectedRoute>
            <DevTasksPage />
          </ProtectedRoute>
        } />
        <Route path="/manager/tasks" element={
          <ProtectedRoute>
            <ManagerTasksPage />
          </ProtectedRoute>
        } />
        <Route path="/manager/kpis" element={
          <ProtectedRoute>
            <ManagerKpisPage />
          </ProtectedRoute>
        } />
        <Route path="/manager/kpis/user/:usuarioId" element={
          <ProtectedRoute>
            <UserKpisPage />
          </ProtectedRoute>
        } />
        <Route path="/manager/kpis/team/:equipoId" element={
          <ProtectedRoute>
            <TeamKpisPage />
          </ProtectedRoute>
        } />
        <Route path="/manager/insights" element={
          <ProtectedRoute>
            <ManagerInsightsPage />
          </ProtectedRoute>
        } />
      </Routes>
    </Router>
  );
}

export default AppRouter;
