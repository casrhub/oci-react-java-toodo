import React from 'react';
import '../styles/ManagerKpisPage.css';
import MemberCard from '../components/Pages/MemberCard';
import { useNavigate } from 'react-router-dom';

function ManagerKpisPage() {
  const navigate = useNavigate();

  const teamMembers = [
    { id: 102, name: 'Cesar Alan Silva Ramos' },
    { id: 1,   name: 'Jose Maria' },
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
      <div className="manager-kpis-page">
        <h1 className="title">Team Members</h1>
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
      </div>
  );
}

export default ManagerKpisPage;
