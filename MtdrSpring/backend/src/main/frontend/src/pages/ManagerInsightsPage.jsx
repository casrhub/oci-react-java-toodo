import React from 'react';
import { Box, Typography, Paper, Grid } from '@mui/material';
import AppNavbar from '../components/AppNavbar';
import TeamSprintHoursBarChart from '../components/charts/TeamSprintHoursBarChart';
import TeamSprintDevHoursBarChart from '../components/charts/TeamSprintDevHoursBarChart';
import LastSprintTaskReport from '../components/charts/LastSprintTaskReport';
import TeamSprintCompletedTasksChart from '../components/charts/TeamSprintCompletedTasksChart';

function ManagerInsightsPage() {
  return (
    <>
      <AppNavbar />

      <Box sx={{ p: 3 }}>
        <Typography variant="h4" sx={{ mb: 4, fontWeight: 'bold' }}>
          Insights del Equipo
        </Typography>

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
              <LastSprintTaskReport sprintId={4} />
            </Paper>
          </Grid>

          <Grid item xs={12}>
            <Paper elevation={3} sx={{ p: 3 }}>
              <TeamSprintCompletedTasksChart />
            </Paper>
          </Grid>
        </Grid>
      </Box>
    </>
  );
}

export default ManagerInsightsPage;
