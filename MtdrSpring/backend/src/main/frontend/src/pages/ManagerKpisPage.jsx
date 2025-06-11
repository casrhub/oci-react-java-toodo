// src/pages/ManagerKpisPage.jsx
import React, { useState } from 'react';
import { Box, Button, Grid, Menu, MenuItem, Paper, Typography } from '@mui/material';
import FilterListIcon from '@mui/icons-material/FilterList';

import AppNavbar from '../components/AppNavbar';

// ─── Componentes para vista de EQUIPO ─────────────────────────────────────────
import TeamSprintHoursBarChart from '../components/charts/TeamSprintHoursBarChart';
import TeamSprintDevHoursBarChart from '../components/charts/TeamSprintDevHoursBarChart';
import LastSprintTaskReport from '../components/charts/LastSprintTaskReport';
import TeamSprintCompletedTasksChart from '../components/charts/TeamSprintCompletedTasksChart';

// ─── KPIs extra de equipo ────────────────────────────────────────────────────
import TeamTaskCharts from './TeamTaskCharts';
import TeamKpiReport from './TeamKpiReport';

// ─── Componentes para vista de DESARROLLADOR ────────────────────────────────
import UserTaskCharts from './UserTaskCharts';
import UserKpiReport from './UserKpiReport';

function ManagerKpisPage() {
  // ─── Miembros del equipo (hard-codeados para Equipo 1) ────────────────────
  const teamMembers = [
    { id: 102, name: 'Cesar Alan Silva Ramos' },
    { id: 101, name: 'Jose Maria' },
    { id: 104, name: 'Miguel Angel Barrientos Ballesteros' },
    { id: 100, name: 'Diego Iván Morales Gallardo' },
    { id: 103, name: 'Fernanda Díaz Gutiérrez' },
  ];

  // ─── Estado del filtro ────────────────────────────────────────────────────
  const [anchorEl, setAnchorEl] = useState(null);
  const [selectedDev, setSelectedDev] = useState(null);

  const openMenu = (e) => setAnchorEl(e.currentTarget);
  const closeMenu = () => setAnchorEl(null);
  const selectDev = (m) => {
    setSelectedDev(m);
    closeMenu();
  };
  const clearFilter = () => {
    setSelectedDev(null);
    closeMenu();
  };

  /* ────────────────────────────────────────────────────────────────────────── */
  return (
    <>
      <AppNavbar />

      <Box sx={{ p: 3 }}>
        {/* Encabezado + filtro */}
        <Box
          sx={{
            mb: 3,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            flexWrap: 'wrap',
            gap: 2,
          }}
        >
          <Typography variant="h4" sx={{ fontWeight: 'bold' }}>
            {selectedDev ? `KPIs — ${selectedDev.name}` : 'KPIs del Equipo'}
          </Typography>

          <Button startIcon={<FilterListIcon />} variant="outlined" onClick={openMenu}>
            Filtrar por developer
          </Button>

          <Menu anchorEl={anchorEl} open={Boolean(anchorEl)} onClose={closeMenu}>
            <MenuItem onClick={clearFilter}>Todo el equipo</MenuItem>
            {teamMembers.map((m) => (
              <MenuItem key={m.id} onClick={() => selectDev(m)}>
                {m.name}
              </MenuItem>
            ))}
          </Menu>
        </Box>

        {/* ─── Vista GLOBAL ─────────────────────────────────────────────────── */}
        {!selectedDev && (
          <>
            <Grid container spacing={4}>
              <Grid item xs={12} md={6}>
                <Paper elevation={3} sx={{ p: 3, height: '100%' }}>
                  <TeamSprintHoursBarChart />
                </Paper>
              </Grid>

              <Grid item xs={12} md={6}>
                <Paper elevation={3} sx={{ p: 3, height: '100%' }}>
                  <TeamSprintDevHoursBarChart />
                </Paper>
              </Grid>

              <Grid item xs={12}>
                <Paper elevation={3} sx={{ p: 3 }}>
                  <TeamSprintCompletedTasksChart />
                </Paper>
              </Grid>

              <Grid item xs={12}>
                <Paper elevation={3} sx={{ p: 3 }}>
                  <LastSprintTaskReport />
                </Paper>
              </Grid>
            </Grid>

            <Box sx={{ mt: 6 }}>
              <TeamTaskCharts equipoId={1} />
              <TeamKpiReport equipoId={1} />
            </Box>
          </>
        )}

        {/* ─── Vista de DEVELOPER ───────────────────────────────────────────── */}
        {selectedDev && (
          <Box>
            {/* Fila superior con las dos gráficas filtradas */}
            <Grid container spacing={4} sx={{ mb: 4 }}>
              <Grid item xs={12} md={6}>
                <Paper elevation={3} sx={{ p: 3, height: '100%' }}>
                  <TeamSprintDevHoursBarChart equipoId={1} usuarioId={selectedDev.id} />
                </Paper>
              </Grid>

              <Grid item xs={12} md={6}>
                <Paper elevation={3} sx={{ p: 3, height: '100%' }}>
                  <TeamSprintCompletedTasksChart equipoId={1} usuarioId={selectedDev.id} />
                </Paper>
              </Grid>
            </Grid>

            {/* Componentes existentes (sin cambios) */}
            <UserTaskCharts usuarioId={selectedDev.id} />
            <UserKpiReport usuarioId={selectedDev.id} />
          </Box>
        )}
      </Box>
    </>
  );
}

export default ManagerKpisPage;
