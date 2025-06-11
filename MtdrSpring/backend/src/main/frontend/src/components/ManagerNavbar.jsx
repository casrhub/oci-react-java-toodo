import React from 'react';
import { AppBar, Toolbar, Button } from '@mui/material';
import { useNavigate } from 'react-router-dom';

/**
 * Barra fija para las páginas del manager.
 * (Se eliminó el botón de “Insights”.)
 */
export default function ManagerNavbar() {
  const navigate = useNavigate();

  return (
    <AppBar position="static" sx={{ bgcolor: '#C74634' }}>
      <Toolbar sx={{ gap: 2 }}>
        <Button color="inherit" onClick={() => navigate('/manager/kpis')}>
          KPIs
        </Button>
        <Button color="inherit" onClick={() => navigate('/manager/tasks')}>
          Tasks
        </Button>
      </Toolbar>
    </AppBar>
  );
}
