import React, { useEffect, useState } from 'react';
import { BarChart, Bar, XAxis, YAxis, Tooltip, Legend, ResponsiveContainer, CartesianGrid } from 'recharts';
import { CircularProgress, Typography } from '@mui/material';
import { API_SPRINTS, API_TEAM_KPIS } from '../../api';

function TeamSprintHoursBarChart({ equipoId = 1 }) {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const sprintRes = await fetch(API_SPRINTS);
        const sprints = await sprintRes.json();
        const results = await Promise.all(
          sprints.map(async (sprint) => {
            const horasRes = await fetch(`${API_TEAM_KPIS}${equipoId}/sprint/${sprint.sprintId}/horas-trabajadas`);
            const horas = await horasRes.json();
            return {
              sprint: sprint.nombre ?? `Sprint ${sprint.sprintId}`,
              horas: Number(horas)
            };
          })
        );
        setData(results);
      } catch (err) {
        setError(err);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [equipoId]);

  if (loading) return <CircularProgress />;
  if (error) return <Typography color="error">Error loading chart: {error.message}</Typography>;

  return (
    <div style={{ margin: '2rem 0' }}>
      <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 2 }}>
        Gráfica 1: Horas Totales trabajadas por Sprint
      </Typography>
      <ResponsiveContainer width="100%" height={300}>
        <BarChart data={data} margin={{ top: 20, right: 30, left: 0, bottom: 5 }}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="sprint" />
          <YAxis allowDecimals={false} label={{ value: 'Horas', angle: -90, position: 'insideLeft' }} />
          <Tooltip />
          <Legend />
          <Bar dataKey="horas" fill="#8884d8" name="Horas Trabajadas" barSize={40} />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}

export default TeamSprintHoursBarChart; 