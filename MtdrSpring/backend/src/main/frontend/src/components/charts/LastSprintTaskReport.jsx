import React, { useEffect, useState, useRef } from 'react';
import {
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

export default function LastSprintTaskReport({ sprintId: propSprintId, chartKey, onLoad }) {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [rows, setRows] = useState([]);
  const [sprintName, setSprintName] = useState('');
  const loadedRef = useRef(false);

  useEffect(() => {
    (async () => {
      try {
        let sprintId = propSprintId;
        let tareas = [];
        if (!sprintId) {
          const sRes = await fetch(API_SPRINTS);
          const sprints = await sRes.json();
          if (!Array.isArray(sprints) || !sprints.length) throw new Error('No sprints found');
          const lastSprint = sprints.reduce((a, b) => (a.sprintId > b.sprintId ? a : b));
          sprintId = lastSprint.sprintId;
        }
        const sprintRes = await fetch(`${API_SPRINTS}/${sprintId}`);
        const sprint = await sprintRes.json();
        setSprintName(sprint.nombre ?? `Sprint ${sprint.sprintId}`);
        tareas = sprint.tareas || [];
        const tableRows = tareas.map((t) => {
          const tm = teamMembers.find((m) => m.id === t.usuarioId);
          return {
            taskName: t.titulo,
            developer: tm ? tm.name : `Usuario ${t.usuarioId}`,
            estimated: t.horasEstimadas,
            actual: t.horasReales,
          };
        });
        setRows(tableRows);
      } catch (err) {
        setError(err);
      } finally {
        setLoading(false);
      }
    })();
  }, [propSprintId]);

  useEffect(() => {
    if (!loading && !loadedRef.current) {
      loadedRef.current = true;
      if (onLoad) onLoad(chartKey);
    }
  }, [loading, onLoad, chartKey]);

  if (loading) return null;
  if (error)
    return <Typography color="error">Error al cargar el reporte: {error.message}</Typography>;

  return (
    <div style={{ margin: '2rem 0' }}>
      <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 2 }}>
        Reporte de tareas del sprint ({sprintName})
      </Typography>
      <TableContainer component={Paper} elevation={0} sx={{ boxShadow: 'none', border: 'none' }}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>
                <strong>Nombre de la tarea</strong>
              </TableCell>
              <TableCell>
                <strong>Desarrollador</strong>
              </TableCell>
              <TableCell>
                <strong>Horas estimadas</strong>
              </TableCell>
              <TableCell>
                <strong>Horas reales</strong>
              </TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {rows.map((r, i) => (
              <TableRow key={i}>
                <TableCell>{r.taskName}</TableCell>
                <TableCell>{r.developer}</TableCell>
                <TableCell>{r.estimated}</TableCell>
                <TableCell>{r.actual}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </div>
  );
}
