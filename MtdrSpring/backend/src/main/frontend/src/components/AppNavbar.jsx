import React, { useState } from 'react';
import { AppBar, Toolbar, Button, IconButton, Menu, MenuItem, Typography } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { LogOut } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export default function AppNavbar() {
  const { role, signOut } = useAuth();
  const navigate = useNavigate();

  const [anchor, setAnchor] = useState(null);
  const openMenu = (e) => setAnchor(e.currentTarget);
  const closeMenu = () => setAnchor(null);

  const go = (path) => {
    navigate(path);
    closeMenu();
  };

  const links =
    role === 'manager'
      ? [
          { label: 'KPIs', path: '/manager/kpis' },
          { label: 'Tareas', path: '/manager/tasks' },
        ]
      : [
          { label: 'KPIs', path: '/dev/kpis' },
          { label: 'Tareas', path: '/dev/tasks' },
        ];

  return (
    <AppBar position="static" sx={{ bgcolor: '#C74634' }}>
      <Toolbar sx={{ gap: 2 }}>
        {links.map((l) => (
          <Button key={l.path} color="inherit" onClick={() => go(l.path)}>
            {l.label}
          </Button>
        ))}

        <Typography sx={{ flexGrow: 1 }} />

        <IconButton color="inherit" onClick={openMenu}>
          <LogOut />
        </IconButton>
        <Menu anchorEl={anchor} open={Boolean(anchor)} onClose={closeMenu}>
          <MenuItem
            onClick={() => {
              signOut();
              closeMenu();
            }}
          >
            Cerrar sesión
          </MenuItem>
        </Menu>
      </Toolbar>
    </AppBar>
  );
}
