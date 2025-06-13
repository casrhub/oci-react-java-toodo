import React from 'react';
import { Box, Typography, Paper, Grid } from '@mui/material';
import TeamSprintHoursBarChart from './TeamSprintHoursBarChart';
import TeamSprintDevHoursBarChart from './TeamSprintDevHoursBarChart';
import LastSprintTaskReport from './LastSprintTaskReport';
import TeamSprintCompletedTasksChart from './TeamSprintCompletedTasksChart';

function ManagerInsights() {
  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" sx={{ mb: 4, fontWeight: 'bold' }}>
        Perspectivas para gerentes
      </Typography>

      <Grid container spacing={4}>
        {/* First Row: Hours Charts */}
        <Grid item xs={12} md={6}>
          <Paper elevation={3} sx={{ p: 3, height: '100%' }}>
            <Typography variant="h6" sx={{ mb: 2, fontWeight: 'bold' }}>
              Horas totales trabajadas por sprint
            </Typography>
            <TeamSprintHoursBarChart />
          </Paper>
        </Grid>

        <Grid item xs={12} md={6}>
          <Paper elevation={3} sx={{ p: 3, height: '100%' }}>
            <Typography variant="h6" sx={{ mb: 2, fontWeight: 'bold' }}>
              Horas trabajadas por desarrollador por sprint
            </Typography>
            <TeamSprintDevHoursBarChart />
          </Paper>
        </Grid>

        {/* Second Row: Completed Tasks Chart */}
        <Grid item xs={12}>
          <Paper elevation={3} sx={{ p: 3 }}>
            <Typography variant="h6" sx={{ mb: 2, fontWeight: 'bold' }}>
              Tareas completadas por desarrollador por sprint
            </Typography>
            <TeamSprintCompletedTasksChart />
          </Paper>
        </Grid>

        {/* Third Row: Task Report */}
        <Grid item xs={12}>
          <Paper elevation={3} sx={{ p: 3 }}>
            <LastSprintTaskReport sprintId={2} />
          </Paper>
        </Grid>
      </Grid>
    </Box>
  );
}

export default ManagerInsights;
