import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Box, Button, Typography } from '@mui/material';
import AppNavbar from '../components/AppNavbar';
import UserTaskCharts from './UserTaskCharts';
import UserKpiReport from './UserKpiReport';

function UserKpisPage() {
  const { usuarioId } = useParams();
  const navigate = useNavigate();

  return (
    <>
      <AppNavbar />

      <Box sx={{ p: 3 }}>
        <Button variant="text" onClick={() => navigate(-1)} sx={{ mb: 2 }}>
          ← Back
        </Button>

        <Typography variant="h5" sx={{ fontWeight: 'bold', mb: 3 }}>
          KPIs del usuario #{usuarioId}
        </Typography>

        <UserTaskCharts usuarioId={parseInt(usuarioId, 10)} />
        <UserKpiReport usuarioId={parseInt(usuarioId, 10)} />
      </Box>
    </>
  );
}

export default UserKpisPage;
