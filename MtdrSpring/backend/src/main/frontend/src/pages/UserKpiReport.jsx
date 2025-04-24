import React, { useEffect, useState } from 'react';
import {
    Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
    Paper, CircularProgress, Typography
} from '@mui/material';

function UserKpiReport({ usuarioId }) {
    const [data, setData] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const sprintRes = await fetch('http://localhost:8080/sprints');
                const sprints = await sprintRes.json();

                const results = await Promise.all(
                    sprints.map(async (sprint) => {
                        const [horasRes, tareasRes] = await Promise.all([
                            fetch(`http://localhost:8080/tareas/usuario/${usuarioId}/sprint/${sprint.sprintId}/horas-trabajadas`),
                            fetch(`http://localhost:8080/tareas/usuario/${usuarioId}/sprint/${sprint.sprintId}/tareas-completadas`)
                        ]);

                        const horas = await horasRes.json();
                        const tareas = await tareasRes.json();

                        return {
                            sprintId: sprint.sprintId,
                            sprintNombre: sprint.nombre ?? `Sprint ${sprint.sprintId}`,
                            horasTrabajadas: Number(horas),
                            tareasCompletadas: Number(tareas)
                        };
                    })
                );

                setData(results);
            } catch (err) {
                console.error('❌ Error loading user report:', err);
                setError(err);
            } finally {
                setLoading(false);
            }
        };

        fetchData(); // don't prefix with `void`, that's more common in TS
    }, [usuarioId]);

    if (loading) return <CircularProgress />;
    if (error) return <Typography color="error">Error loading report: {error.message}</Typography>;

    return (
        <div style={{ marginTop: '2rem' }}>
            <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 2 }}>
                Reporte por Sprint
            </Typography>

            <TableContainer component={Paper}>
                <Table>
                    <TableHead sx={{ backgroundColor: '#f5f5f5' }}>
                        <TableRow>
                            <TableCell><strong>Sprint</strong></TableCell>
                            <TableCell><strong>Horas Trabajadas</strong></TableCell>
                            <TableCell><strong>Tareas Completadas</strong></TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {Array.isArray(data) && data.map((row) => (
                            <TableRow key={row.sprintId}>
                                <TableCell>{row.sprintNombre}</TableCell>
                                <TableCell>{row.horasTrabajadas}</TableCell>
                                <TableCell>{row.tareasCompletadas}</TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </div>
    );
}

export default UserKpiReport;