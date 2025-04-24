import React, { useEffect, useState } from 'react';
import TaskComparisonBar from '../components/charts/TaskComparisonBar';
import { CircularProgress } from '@mui/material';

function TeamTaskCharts({ equipoId }) {
    const [summary, setSummary] = useState(null);
    const [err, setErr] = useState(null);

    useEffect(() => {
        fetch(`http://localhost:8080/tareas/equipo/${equipoId}/summary`)
            .then(async (r) => {
                if (!r.ok) {
                    const text = await r.text();
                    throw new Error(`HTTP ${r.status} - ${text}`);
                }

                const contentType = r.headers.get("content-type");
                if (contentType && contentType.includes("application/json")) {
                    return r.json();
                } else {
                    const text = await r.text();
                    throw new Error("Expected JSON but got:\n" + text);
                }
            })
            .then(setSummary)
            .catch((e) => {
                console.error("❌ Error en fetch:", e);
                setErr(e);
            });
    }, [equipoId]);

    if (err) {
        return (
            <div style={{ color: 'red', padding: '1rem' }}>
                <h3>Error al cargar KPIs del equipo</h3>
                <pre>{err.message}</pre>
            </div>
        );
    }

    if (!summary) return <CircularProgress />;

    return (
        <>
            <TaskComparisonBar
                title="Después del deadline"
                asignadas={summary.asignadas}
                completadas={summary.completadasDespues}
            />
            <TaskComparisonBar
                title="Antes del deadline"
                asignadas={summary.asignadas}
                completadas={summary.completadasAntes}
                color1="#C74634"
                color2="#2EAD5F"
            />
        </>
    );
}

export default TeamTaskCharts;
