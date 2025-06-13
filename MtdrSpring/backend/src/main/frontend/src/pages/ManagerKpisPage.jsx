import React, { useState, useEffect, useRef } from 'react';
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
  const [loadedKeys, setLoadedKeys] = useState(new Set());
  const [pageLoading, setPageLoading] = useState(true);
  const failSafe = useRef(null);

  const teamKeys = [
    'teamHours',
    'teamDevHours',
    'teamCompleted',
    'lastSprint',
    'teamTasks',
    'teamReport',
  ];
  const devKeys = ['devHours', 'devCompleted', 'userTasks', 'userReport'];
  const expectedKeys = selectedDev ? devKeys : teamKeys;

  const startLoading = () => {
    setLoadedKeys(new Set());
    setPageLoading(true);
    if (failSafe.current) clearTimeout(failSafe.current);
    failSafe.current = setTimeout(() => setPageLoading(false), 30000);
  };

  useEffect(() => {
    if (role === 'developer' && developerId) {
      const dev = teamMembers.find((m) => m.id === developerId);
      if (dev) setSelectedDev(dev);
    }
  }, [role, developerId]);

  useEffect(() => {
    startLoading();
  }, [selectedDev]);

  useEffect(() => {
    if (loadedKeys.size >= expectedKeys.length) {
      if (failSafe.current) clearTimeout(failSafe.current);
      setPageLoading(false);
    }
  }, [loadedKeys, expectedKeys.length]);

  const handleChartLoad = (key) =>
    setLoadedKeys((prev) => {
      const next = new Set(prev);
      next.add(key);
      return next;
    });

  const openMenu = (e) => setAnchorEl(e.currentTarget);
  const closeMenu = () => setAnchorEl(null);
  const selectDev = (m) => {
    startLoading();
    setSelectedDev(m);
    closeMenu();
  };
  const clearFilter = () => {
    startLoading();
    setSelectedDev(null);
    closeMenu();
  };

  const paperProps = {
    elevation: 0,
    sx: { p: 3, boxShadow: 'none', border: 'none', height: '100%' },
  };

  return (
    <>
      {pageLoading && (
        <Box
          sx={{
            position: 'fixed',
            inset: 0,
            bgcolor: 'white',
            zIndex: 9999,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center',
          }}
        >
          <CircularProgress size={80} />
          <Typography variant="h6" sx={{ mt: 2 }}>
            Cargando KPIs, por favor espera…
          </Typography>
        </Box>
      )}

      <AppNavbar />

      <Box sx={{ p: 3, visibility: pageLoading ? 'hidden' : 'visible' }}>
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
            <>
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
            </>
          )}
        </Box>

        {!selectedDev && role !== 'developer' && (
          <>
            <Grid container spacing={4}>
              <Grid item xs={12} md={6}>
                <Paper {...paperProps}>
                  <TeamSprintHoursBarChart chartKey="teamHours" onLoad={handleChartLoad} />
                </Paper>
              </Grid>
              <Grid item xs={12} md={6}>
                <Paper {...paperProps}>
                  <TeamSprintDevHoursBarChart chartKey="teamDevHours" onLoad={handleChartLoad} />
                </Paper>
              </Grid>
              <Grid item xs={12}>
                <Paper {...paperProps}>
                  <TeamSprintCompletedTasksChart
                    chartKey="teamCompleted"
                    onLoad={handleChartLoad}
                  />
                </Paper>
              </Grid>
              <Grid item xs={12}>
                <Paper {...paperProps}>
                  <LastSprintTaskReport chartKey="lastSprint" onLoad={handleChartLoad} />
                </Paper>
              </Grid>
            </Grid>

            <Grid container spacing={4} sx={{ mt: 4 }}>
              <Grid item xs={12}>
                <Paper {...paperProps}>
                  <TeamTaskCharts equipoId={1} chartKey="teamTasks" onLoad={handleChartLoad} />
                </Paper>
              </Grid>
              <Grid item xs={12}>
                <Paper {...paperProps}>
                  <TeamKpiReport equipoId={1} chartKey="teamReport" onLoad={handleChartLoad} />
                </Paper>
              </Grid>
            </Grid>
          </>
        )}

        {selectedDev && (
          <>
            <Grid container spacing={4}>
              <Grid item xs={12} md={6}>
                <Paper {...paperProps}>
                  <TeamSprintDevHoursBarChart
                    equipoId={1}
                    usuarioId={selectedDev.id}
                    chartKey="devHours"
                    onLoad={handleChartLoad}
                  />
                </Paper>
              </Grid>
              <Grid item xs={12} md={6}>
                <Paper {...paperProps}>
                  <TeamSprintCompletedTasksChart
                    equipoId={1}
                    usuarioId={selectedDev.id}
                    chartKey="devCompleted"
                    onLoad={handleChartLoad}
                  />
                </Paper>
              </Grid>
            </Grid>

            <Grid container spacing={4} sx={{ mt: 4 }}>
              <Grid item xs={12}>
                <Paper {...paperProps}>
                  <UserTaskCharts
                    usuarioId={selectedDev.id}
                    chartKey="userTasks"
                    onLoad={handleChartLoad}
                  />
                </Paper>
              </Grid>
              <Grid item xs={12}>
                <Paper {...paperProps}>
                  <UserKpiReport
                    usuarioId={selectedDev.id}
                    chartKey="userReport"
                    onLoad={handleChartLoad}
                  />
                </Paper>
              </Grid>
            </Grid>
          </>
        )}
      </Box>
    </>
  );
}
