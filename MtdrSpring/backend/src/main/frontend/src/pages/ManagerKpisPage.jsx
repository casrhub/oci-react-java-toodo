import React from 'react';
import '../styles/ManagerKpisPage.css';
import MemberCard from '../components/Pages/MemberCard';
import { useNavigate } from 'react-router-dom';
import TeamSprintHoursBarChart from '../components/charts/TeamSprintHoursBarChart';
import TeamSprintDevHoursBarChart from '../components/charts/TeamSprintDevHoursBarChart';
import LastSprintTaskReport from '../components/charts/LastSprintTaskReport';
import { Box, Typography } from '@mui/material';
import ManagerInsights from '../components/charts/ManagerInsights';

function ManagerKpisPage() {
  const navigate = useNavigate();

  const teamMembers = [
    { id: 102, name: 'Cesar Alan Silva Ramos' },
    { id: 101,   name: 'Jose Maria' },
    { id: 104, name: 'Miguel Angel Barrientos Ballesteros' },
    { id: 100, name: 'Diego Iván Morales Gallardo' },
    { id: 103, name: 'Fernanda Díaz Gutiérrez' }
  ];

  const handleMemberClick = (member) => {
    console.log(`Clicked on ${member.name}`);
    navigate(`/manager/kpis/user/${member.id}`);
  };

  const handleTeamClick = () => {
    console.log("Clicked on Equipo 1");
    navigate(`/manager/kpis/team/1`);
  };

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" sx={{ mb: 4, fontWeight: 'bold' }}>
        KPIs del Manager
      </Typography>
      
      <ManagerInsights />

      <div className="manager-kpis-page">
        <h1 className="title">Team Memberss</h1>
        <TeamSprintHoursBarChart equipoId={1} />
        <TeamSprintDevHoursBarChart equipoId={1} />
        <div className="members-container">
          {teamMembers.map(member => (
              <MemberCard
                  key={member.id}
                  name={member.name}
                  onClick={() => handleMemberClick(member)}
              />

          ))}

          {/* Nueva card para el equipo */}
          <MemberCard
              key="team-1"
              name="Equipo 1"
              onClick={handleTeamClick}
          />
        </div>
        <LastSprintTaskReport sprintId={2} />
      </div>
    </Box>
  );
}

export default ManagerKpisPage;
