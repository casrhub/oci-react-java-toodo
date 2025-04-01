import React from 'react';
import './ManagerKpisPage.css'; 
import { CircleUserRound } from 'lucide-react';

function ManagerKpisPage() {
  // Hardcoded team members
  const teamMembers = [
    { id: 1, name: 'Cesar' },
    { id: 2, name: 'Jose María' },
    { id: 3, name: 'Miguel Ángel' },
    { id: 4, name: 'Diego' },
    { id: 5, name: 'Fernanda' },
  ];

  return (
    <div className="manager-kpis-page">
      <h1 className="title">Team Members</h1>
      <div className="members-container">
        {teamMembers.map(member => (
          <div className="member-card" key={member.id}>
            <CircleUserRound className="member-icon" />
            <span className="member-name">{member.name}</span>
          </div>
        ))}
      </div>
    </div>
  );
}

export default ManagerKpisPage;
