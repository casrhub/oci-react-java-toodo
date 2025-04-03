import React from 'react';
import '../styles/ManagerKpisPage.css'; 
import MemberCard from '../components/Pages/MemberCard';
import { useHistory } from 'react-router-dom';

function ManagerKpisPage() {
  const history = useHistory();

  // Hardcoded team members (replace later with dynamic data)
  const teamMembers = [
    { id: 1, name: 'Cesar' },
    { id: 2, name: 'Jose María' },
    { id: 3, name: 'Miguel Ángel' },
    { id: 4, name: 'Diego' },
    { id: 5, name: 'Fernanda' },
  ];

  const handleMemberClick = (member) => {
    console.log(`Clicked on ${member.name}`);
    history.push(`/home`); // Navigate to the member's KPIs page
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
      </div>
    </div>
  );
}

export default ManagerKpisPage;
