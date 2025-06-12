import React, { useEffect, useState } from 'react';
import {
  CircularProgress,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
} from '@mui/material';
import { API_SPRINTS } from '../../api';

const teamMembers = [
  { id: 102, name: 'Cesar Alan Silva Ramos' },
  { id: 101, name: 'Jose Maria' },
  { id: 104, name: 'Miguel Angel Barrientos Ballesteros' },
  { id: 100, name: 'Diego Iván Morales Gallardo' },
  { id: 103, name: 'Fernanda Díaz Gutiérrez' },
];

function LastSprintTaskReport({ sprintId: propSprintId }) {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [rows, setRows] = useState([]);
  const [sprintName, setSprintName] = useState('');

  useEffect(() => {
    const fetchData = async () => {
      try {
        let sprintId = propSprintId;
        let sprintName = '';
        let tareas = [];
        if (!sprintId) {
          const sprintsRes = await fetch(API_SPRINTS);
          const sprints = await sprintsRes.json();
          if (!Array.isArray(sprints) || sprints.length === 0) throw new Error('No sprints found');
          const lastSprint = sprints.reduce((a, b) => (a.sprintId > b.sprintId ? a : b));
          sprintId = lastSprint.sprintId;
        }
        const sprintRes = await fetch(`${API_SPRINTS}/${sprintId}`);
        const sprint = await sprintRes.json();
        sprintName = sprint.nombre ?? `Sprint ${sprint.sprintId}`;
        tareas = sprint.tareas || [];

        const tableRows = tareas.map((t) => {
          const teamMember = teamMembers.find((member) => member.id === t.usuarioId);
          return {
            taskName: t.titulo,
            developer: teamMember ? teamMember.name : `Usuario ${t.usuarioId}`,
            estimated: t.horasEstimadas,
            actual: t.horasReales,
          };
        });

        setSprintName(sprintName);
        setRows(tableRows);
      } catch (err) {
        setError(err);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [propSprintId]);

  if (loading) return <CircularProgress />;
  if (error) return <Typography color="error">Error loading report: {error.message}</Typography>;

  return (
    <div style={{ margin: '2rem 0' }}>
      <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 2 }}>
        Reporte de tareas del sprint ({sprintName})
      </Typography>
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>
                <strong>Task Name</strong>
              </TableCell>
              <TableCell>
                <strong>Developer</strong>
              </TableCell>
              <TableCell>
                <strong>Estimated Hours</strong>
              </TableCell>
              <TableCell>
                <strong>Actual Hours</strong>
              </TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {rows.map((row, idx) => (
              <TableRow key={idx}>
                <TableCell>{row.taskName}</TableCell>
                <TableCell>{row.developer}</TableCell>
                <TableCell>{row.estimated}</TableCell>
                <TableCell>{row.actual}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </div>
  );
}

export default LastSprintTaskReport;
