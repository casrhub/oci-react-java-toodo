import React from 'react';
import PropTypes from 'prop-types';
import { CircleUserRound } from 'lucide-react';
import './MemberCard.css';

export default function MemberCard({ name, onClick }) {
  return (
    <button /* accesible + evita div-onclick -> ESLint */
      type="button"
      className="member-card"
      onClick={onClick}
    >
      <CircleUserRound className="member-icon" />
      <span className="member-name">{name}</span>
    </button>
  );
}

MemberCard.propTypes = {
  name: PropTypes.string.isRequired,
  onClick: PropTypes.func,          // opcional
};

MemberCard.defaultProps = {
  onClick: () => {console.log("alog")},               // función “noop” segura
};
