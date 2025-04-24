import React from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Box, Button, Typography } from "@mui/material";
import TeamTaskCharts from "./TeamTaskCharts";
import TeamKpiReport from "./TeamKpiReport"; // ⬅️ New import

function TeamKpisPage() {
    const { equipoId } = useParams();
    const navigate = useNavigate();

    return (
        <Box sx={{ p: 3 }}>
            <Button onClick={() => navigate(-1)} sx={{ mb: 2 }}>← Back</Button>

            <Typography variant="h5" sx={{ fontWeight: "bold", mb: 3 }}>
                KPIs del equipo #{equipoId}
            </Typography>

            {/* Charts (existing) */}
            <TeamTaskCharts equipoId={Number(equipoId)} />

            {/* Report (new) */}
            <TeamKpiReport equipoId={Number(equipoId)} />
        </Box>
    );
}

export default TeamKpisPage;