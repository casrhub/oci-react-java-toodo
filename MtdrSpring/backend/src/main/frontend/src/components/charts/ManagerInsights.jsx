import React from 'react';
import { Box, Typography, Paper, Grid } from '@mui/material';
import TeamSprintHoursBarChart from './TeamSprintHoursBarChart';
import TeamSprintDevHoursBarChart from './TeamSprintDevHoursBarChart';
import LastSprintTaskReport from './LastSprintTaskReport';

function ManagerInsights() {
  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" sx={{ mb: 4, fontWeight: 'bold' }}>
        Insights del Equipo
      </Typography>
      
      <Grid container spacing={4}>
        {/* First Row: Charts */}
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

        {/* Second Row: Task Report */}
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