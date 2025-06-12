import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Box, Button, Typography } from '@mui/material';
import AppNavbar from '../components/AppNavbar';
import TeamTaskCharts from './TeamTaskCharts';
import TeamKpiReport from './TeamKpiReport';

function TeamKpisPage() {
  const { equipoId } = useParams();
  const navigate = useNavigate();

  return (
    <>
      <AppNavbar />

      <Box sx={{ p: 3 }}>
        <Button onClick={() => navigate(-1)} sx={{ mb: 2 }}>
          ← Back
        </Button>

        <Typography variant="h5" sx={{ fontWeight: 'bold', mb: 3 }}>
          KPIs del equipo #{equipoId}
        </Typography>

        <TeamTaskCharts equipoId={Number(equipoId)} />
        <TeamKpiReport equipoId={Number(equipoId)} />
      </Box>
    </>
  );
}

export default TeamKpisPage;
