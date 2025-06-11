// src/router/ProtectedRoute.jsx
import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

/**
 * Envuelve rutas privadas.  Si `allowed` se pasa, verifica rol.
 */
export default function ProtectedRoute({ children, allowed }) {
  const { user, role, loading } = useAuth();

  if (loading) return null; // spinner opcional

  if (!user) return <Navigate to="/" replace />;

  if (allowed && !allowed.includes(role)) {
    // Usuario autenticado pero sin permiso
    return <Navigate to="/" replace />;
  }

  return children;
}
