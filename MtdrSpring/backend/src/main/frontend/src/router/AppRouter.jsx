import React from 'react';
import { HashRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import ProtectedRoute from './ProtectedRoute';

// Páginas públicas
import LandingPage from '../pages/LandingPage';
import SignInPage from '../pages/SignInPage';
import SignUpPage from '../pages/SignUpPage';

// Páginas developer
import DevTasksPage from '../pages/DevTasksPage';
import ManagerKpisPage from '../pages/ManagerKpisPage'; // reutilizamos vista KPIs
import UserKpisPage from '../pages/UserKpisPage';

// Páginas manager
import ManagerTasksPage from '../pages/ManagerTasksPage';
import TeamKpisPage from '../pages/TeamKpisPage';

function AppRouter() {
  return (
    <Router>
      <Routes>
        {/* Públicas */}
        <Route path="/" element={<LandingPage />} />
        <Route path="/sign-in" element={<SignInPage />} />
        <Route path="/sign-up" element={<SignUpPage />} />

        {/* Developer */}
        <Route
          path="/dev/tasks"
          element={
            <ProtectedRoute allowed={['developer']}>
              <DevTasksPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/kpis"
          element={
            <ProtectedRoute allowed={['developer']}>
              <ManagerKpisPage /> {/* Mismos componentes pero filtrados en UI */}
            </ProtectedRoute>
          }
        />

        {/* Manager */}
        <Route
          path="/manager/tasks"
          element={
            <ProtectedRoute allowed={['manager']}>
              <ManagerTasksPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/manager/kpis"
          element={
            <ProtectedRoute allowed={['manager']}>
              <ManagerKpisPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/manager/kpis/user/:usuarioId"
          element={
            <ProtectedRoute allowed={['manager']}>
              <UserKpisPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/manager/kpis/team/:equipoId"
          element={
            <ProtectedRoute allowed={['manager']}>
              <TeamKpisPage />
            </ProtectedRoute>
          }
        />

        {/* Fallback */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Router>
  );
}

export default AppRouter;
