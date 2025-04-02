import React from 'react';
import PropTypes from 'prop-types';
import { CircleUserRound } from 'lucide-react';
import './MemberCard.css';

function MemberCard({ name, onClick }) {
  return (
    <div className="member-card" onClick={onClick}>
      <CircleUserRound className="member-icon" />
      <span className="member-name">{name}</span>
    </div>
  );
}

MemberCard.propTypes = {
  name: PropTypes.string.isRequired,
  onClick: PropTypes.func,
};

MemberCard.defaultProps = {
  onClick: () => {},
};

export default MemberCard;
