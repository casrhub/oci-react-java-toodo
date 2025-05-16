import React, { useEffect, useState } from 'react';
import { BarChart, Bar, XAxis, YAxis, Tooltip, Legend, ResponsiveContainer, CartesianGrid } from 'recharts';
import { CircularProgress, Typography } from '@mui/material';
import { API_SPRINTS, API_TEAM_KPIS } from '../../api';

// Hardcoded team members for Equipo 1 (same as in ManagerKpisPage)
const teamMembers = [
  { id: 102, name: 'Cesar Alan Silva Ramos' },
  { id: 101, name: 'Jose Maria' },
  { id: 104, name: 'Miguel Angel Barrientos Ballesteros' },
  { id: 100, name: 'Diego Iván Morales Gallardo' },
  { id: 103, name: 'Fernanda Díaz Gutiérrez' }
];

const COLORS = ['#4fc3f7', '#81c784', '#ba68c8', '#ffd54f', '#ff8a65'];

function TeamSprintDevHoursBarChart({ equipoId = 1 }) {
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
            // For each member, fetch their hours for this sprint
            const memberHours = await Promise.all(
              teamMembers.map(async (member) => {
                const res = await fetch(`${API_TEAM_KPIS}${equipoId}/sprint/${sprint.sprintId}/usuario/${member.id}/horas-trabajadas`);
                const horas = await res.json();
                return { [member.name]: Number(horas) };
              })
            );
            // Merge all member hours into one object
            const sprintData = memberHours.reduce((acc, cur) => ({ ...acc, ...cur }), {});
            return {
              sprint: sprint.nombre ?? `Sprint ${sprint.sprintId}`,
              sprintId: sprint.sprintId,
              ...sprintData
            };
          })
        );
        results.sort((a, b) => a.sprintId - b.sprintId);
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
        Gráfica 2: Horas Trabajadas por Developer por Sprint
      </Typography>
      <ResponsiveContainer width="100%" height={340}>
        <BarChart data={data} margin={{ top: 20, right: 30, left: 0, bottom: 5 }}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="sprint" />
          <YAxis allowDecimals={false} label={{ value: 'Horas trabajadas', angle: -90, position: 'insideLeft' }} />
          <Tooltip />
          <Legend />
          {teamMembers.map((member, idx) => (
            <Bar key={member.id} dataKey={member.name} fill={COLORS[idx % COLORS.length]} name={member.name} barSize={30} />
          ))}
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}

export default TeamSprintDevHoursBarChart; 