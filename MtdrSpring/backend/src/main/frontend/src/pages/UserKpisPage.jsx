import React from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Box, Button, Typography } from "@mui/material";
import UserTaskCharts from "./UserTaskCharts";
import UserKpiReport from "./UserKpiReport";

function UserKpisPage() {
    const { usuarioId } = useParams();
    const navigate = useNavigate();

    return (
        <Box sx={{ p: 3 }}>
            <Button variant="text" onClick={() => navigate(-1)} sx={{ mb: 2 }}>← Back</Button>

            <Typography variant="h5" sx={{ fontWeight: "bold", mb: 3 }}>
                KPIs del usuario #{usuarioId}
            </Typography>

            {/* Chart */}
            <UserTaskCharts usuarioId={parseInt(usuarioId, 10)} />

            {/* Report below the chart */}
            <UserKpiReport usuarioId={parseInt(usuarioId, 10)} />
        </Box>
    );
}

export default UserKpisPage;