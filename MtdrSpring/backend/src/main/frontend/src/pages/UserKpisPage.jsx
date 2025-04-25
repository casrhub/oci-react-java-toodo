import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Box, Button, Typography } from "@mui/material";
import UserTaskCharts from "./UserTaskCharts";
import UserKpiReport from "./UserKpiReport";

function UserKpisPage() {
  const { usuarioId } = useParams();
  const navigate = useNavigate();
  const [nombreUsuario, setNombreUsuario] = useState(null);

  useEffect(() => {
    fetch(`http://localhost:8080/usuarios/${usuarioId}/nombre`)
      .then((res) => {
        if (!res.ok) throw new Error("Usuario no encontrado");
        return res.json();
      })
      .then((data) => {
        setNombreUsuario(data.nombre);
      })
      .catch((err) => {
        console.error("Error al obtener nombre del usuario:", err);
        setNombreUsuario(null);
      });
  }, [usuarioId]);

  return (
    <Box sx={{ p: 3 }}>
      <Button variant="text" onClick={() => navigate(-1)} sx={{ mb: 2 }}>← Back</Button>

      <Typography variant="h5" sx={{ fontWeight: "bold", mb: 3 }}>
        {nombreUsuario ? `KPIs de ${nombreUsuario}` : "Cargando KPIs..."}
      </Typography>

      <UserTaskCharts usuarioId={parseInt(usuarioId, 10)} />
      <UserKpiReport usuarioId={parseInt(usuarioId, 10)} />
    </Box>
  );
}

export default UserKpisPage;