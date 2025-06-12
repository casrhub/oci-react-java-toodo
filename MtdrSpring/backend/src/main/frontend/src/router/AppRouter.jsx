import React from 'react';
import { HashRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import ProtectedRoute from './ProtectedRoute';

import LandingPage from '../pages/LandingPage';
import SignInPage from '../pages/SignInPage';
import SignUpPage from '../pages/SignUpPage';

import DevTasksPage from '../pages/DevTasksPage';
import ManagerKpisPage from '../pages/ManagerKpisPage';
import UserKpisPage from '../pages/UserKpisPage';

import ManagerTasksPage from '../pages/ManagerTasksPage';
import TeamKpisPage from '../pages/TeamKpisPage';

function AppRouter() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/sign-in" element={<SignInPage />} />
        <Route path="/sign-up" element={<SignUpPage />} />

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
              <ManagerKpisPage />
            </ProtectedRoute>
          }
        />

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

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Router>
  );
}

export default AppRouter;
