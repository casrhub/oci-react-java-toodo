import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function ProtectedRoute({ children, allowed }) {
  const { user, role, loading } = useAuth();

  if (loading) return null;

  if (!user) return <Navigate to="/" replace />;

  if (allowed && !allowed.includes(role)) {
    return <Navigate to="/" replace />;
  }

  return children;
}
