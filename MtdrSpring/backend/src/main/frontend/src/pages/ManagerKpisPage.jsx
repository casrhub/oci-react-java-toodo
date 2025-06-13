import React, { useState, useEffect } from 'react';
import {
  Box,
  Button,
  Grid,
  Menu,
  MenuItem,
  Paper,
  Typography,
  CircularProgress,
} from '@mui/material';
import FilterListIcon from '@mui/icons-material/FilterList';
import { useAuth } from '../context/AuthContext';
import AppNavbar from '../components/AppNavbar';
import TeamSprintHoursBarChart from '../components/charts/TeamSprintHoursBarChart';
import TeamSprintDevHoursBarChart from '../components/charts/TeamSprintDevHoursBarChart';
import TeamSprintCompletedTasksChart from '../components/charts/TeamSprintCompletedTasksChart';
import LastSprintTaskReport from '../components/charts/LastSprintTaskReport';
import TeamTaskCharts from './TeamTaskCharts';
import TeamKpiReport from './TeamKpiReport';
import UserTaskCharts from './UserTaskCharts';
import UserKpiReport from './UserKpiReport';

const teamMembers = [
  { id: 102, name: 'Cesar Alan Silva Ramos' },
  { id: 101, name: 'Jose Maria' },
  { id: 104, name: 'Miguel Angel Barrientos Ballesteros' },
  { id: 100, name: 'Diego Iván Morales Gallardo' },
  { id: 103, name: 'Fernanda Díaz Gutiérrez' },
];

export default function ManagerKpisPage() {
  const { role, developerId } = useAuth();
  const [anchorEl, setAnchorEl] = useState(null);
  const [selectedDev, setSelectedDev] = useState(null);
  const [loadedCount, setLoadedCount] = useState(0);
  const totalCharts = selectedDev ? 4 : 6;
  const [pageLoading, setPageLoading] = useState(true);

  useEffect(() => {
    if (role === 'developer' && developerId) {
      const dev = teamMembers.find((m) => m.id === developerId);
      if (dev) setSelectedDev(dev);
    }
  }, [role, developerId]);

  useEffect(() => {
    if (loadedCount === totalCharts) {
      setPageLoading(false);
    }
  }, [loadedCount, totalCharts]);

  const handleChartLoad = () => {
    setLoadedCount((prev) => prev + 1);
  };

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

  return (
    <>
      <AppNavbar />
      <Box sx={{ position: 'relative', p: 3 }}>
        {pageLoading && (
          <Box
            sx={{
              position: 'absolute',
              top: 0,
              left: 0,
              right: 0,
              bottom: 0,
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              justifyContent: 'center',
              bgcolor: 'rgba(255,255,255,0.8)',
              zIndex: 1,
            }}
          >
            <CircularProgress size={80} />
            <Typography variant="h6" sx={{ mt: 2 }}>
              Cargando KPIs, por favor espera...
            </Typography>
          </Box>
        )}
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
          {role !== 'developer' && (
            <Button startIcon={<FilterListIcon />} variant="outlined" onClick={openMenu}>
              Filtrar por developer
            </Button>
          )}
          {role !== 'developer' && (
            <Menu anchorEl={anchorEl} open={Boolean(anchorEl)} onClose={closeMenu}>
              <MenuItem onClick={clearFilter}>Todo el equipo</MenuItem>
              {teamMembers.map((m) => (
                <MenuItem key={m.id} onClick={() => selectDev(m)}>
                  {m.name}
                </MenuItem>
              ))}
            </Menu>
          )}
        </Box>
        {!selectedDev && role !== 'developer' && (
          <>
            <Grid container spacing={6}>
              <Grid item xs={12} md={6}>
                <Paper elevation={3} sx={{ p: 3 }}>
                  <TeamSprintHoursBarChart onLoad={handleChartLoad} />
                </Paper>
              </Grid>
              <Grid item xs={12} md={6}>
                <Paper elevation={3} sx={{ p: 3 }}>
                  <TeamSprintDevHoursBarChart onLoad={handleChartLoad} />
                </Paper>
              </Grid>
              <Grid item xs={12}>
                <Paper elevation={3} sx={{ p: 3 }}>
                  <TeamSprintCompletedTasksChart onLoad={handleChartLoad} />
                </Paper>
              </Grid>
              <Grid item xs={12}>
                <Paper elevation={3} sx={{ p: 3 }}>
                  <LastSprintTaskReport onLoad={handleChartLoad} />
                </Paper>
              </Grid>
            </Grid>
            <Grid container spacing={6} sx={{ mt: 6 }}>
              <Grid item xs={12}>
                <Paper elevation={3} sx={{ p: 3 }}>
                  <TeamTaskCharts equipoId={1} onLoad={handleChartLoad} />
                </Paper>
              </Grid>
              <Grid item xs={12}>
                <Paper elevation={3} sx={{ p: 3 }}>
                  <TeamKpiReport equipoId={1} onLoad={handleChartLoad} />
                </Paper>
              </Grid>
            </Grid>
          </>
        )}
        {selectedDev && (
          <>
            <Grid container spacing={6} sx={{ mb: 6 }}>
              <Grid item xs={12} md={6}>
                <Paper elevation={3} sx={{ p: 3 }}>
                  <TeamSprintDevHoursBarChart
                    equipoId={1}
                    usuarioId={selectedDev.id}
                    onLoad={handleChartLoad}
                  />
                </Paper>
              </Grid>
              <Grid item xs={12} md={6}>
                <Paper elevation={3} sx={{ p: 3 }}>
                  <TeamSprintCompletedTasksChart
                    equipoId={1}
                    usuarioId={selectedDev.id}
                    onLoad={handleChartLoad}
                  />
                </Paper>
              </Grid>
            </Grid>
            <Grid container spacing={6}>
              <Grid item xs={12}>
                <Paper elevation={3} sx={{ p: 3 }}>
                  <UserTaskCharts usuarioId={selectedDev.id} onLoad={handleChartLoad} />
                </Paper>
              </Grid>
              <Grid item xs={12}>
                <Paper elevation={3} sx={{ p: 3 }}>
                  <UserKpiReport usuarioId={selectedDev.id} onLoad={handleChartLoad} />
                </Paper>
              </Grid>
            </Grid>
          </>
        )}
      </Box>
    </>
  );
}
