import React from 'react';
import { BarChart, Bar, XAxis, YAxis, Tooltip, Legend, ResponsiveContainer } from 'recharts';

function TaskComparisonBar({
  title,
  asignadas,
  completadas,
  color1 = '#C74634',
  color2 = '#6A9BFF',
}) {
  const data = [{ name: title, Asignadas: asignadas, Completadas: completadas }];

  return (
    <ResponsiveContainer width="100%" height={260}>
      <BarChart data={data} margin={{ top: 20, right: 30, bottom: 5, left: 0 }}>
        <XAxis dataKey="name" />
        <YAxis allowDecimals={false} />
        <Tooltip />
        <Legend />
        <Bar dataKey="Asignadas" barSize={30} fill={color1} />
        <Bar dataKey="Completadas" barSize={30} fill={color2} />
      </BarChart>
    </ResponsiveContainer>
  );
}

export default TaskComparisonBar;
