import React, { useState } from 'react';
import {
  Box,
  Button,
  MenuItem,
  Select,
  TextField,
  Typography,
  Paper,
  InputLabel,
  FormControl,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { auth } from '../firebase';
import { createUserWithEmailAndPassword } from 'firebase/auth';
import { saveUserRole } from '../context/AuthContext';

const developers = [
  { id: 102, name: 'Cesar Alan Silva Ramos' },
  { id: 101, name: 'Jose Maria' },
  { id: 104, name: 'Miguel Angel Barrientos Ballesteros' },
  { id: 100, name: 'Diego Iván Morales Gallardo' },
  { id: 103, name: 'Fernanda Díaz Gutiérrez' },
];

export default function SignUpPage() {
  const nav = useNavigate();
  const [email, setEmail] = useState('');
  const [pass, setPass] = useState('');
  const [role, setRole] = useState('developer');
  const [developerId, setDeveloperId] = useState('');
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const { user } = await createUserWithEmailAndPassword(auth, email, pass);
      await saveUserRole(user.uid, role, role === 'developer' ? Number(developerId) : null);
      nav(role === 'manager' ? '/manager/tasks' : '/dev/tasks', { replace: true });
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box sx={{ mt: 6, display: 'flex', justifyContent: 'center' }}>
      <Paper sx={{ p: 4, minWidth: 320 }}>
        <Typography variant="h5" gutterBottom>
          Create account
        </Typography>

        <Box
          component="form"
          onSubmit={handleSubmit}
          sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}
        >
          <TextField
            label="Email"
            type="email"
            required
            fullWidth
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <TextField
            label="Password"
            type="password"
            required
            fullWidth
            value={pass}
            onChange={(e) => setPass(e.target.value)}
          />
          <FormControl fullWidth size="small">
            <InputLabel id="role-label">Role</InputLabel>
            <Select
              labelId="role-label"
              label="Role"
              value={role}
              onChange={(e) => setRole(e.target.value)}
            >
              <MenuItem value="developer">Developer</MenuItem>
              <MenuItem value="manager">Manager</MenuItem>
            </Select>
          </FormControl>

          {role === 'developer' && (
            <FormControl fullWidth size="small">
              <InputLabel id="dev-label">Developer</InputLabel>
              <Select
                labelId="dev-label"
                label="Developer"
                value={developerId}
                onChange={(e) => setDeveloperId(e.target.value)}
                required
              >
                {developers.map((d) => (
                  <MenuItem key={d.id} value={d.id}>
                    {d.name}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          )}

          {error && <Typography color="error">{error}</Typography>}

          <Button
            type="submit"
            variant="contained"
            disabled={loading || (role === 'developer' && developerId === '')}
            sx={{ bgcolor: '#C74634' }}
          >
            {loading ? 'Creating…' : 'Sign Up'}
          </Button>

          <Button onClick={() => nav('/sign-in')} disabled={loading}>
            Already have an account?
          </Button>
        </Box>
      </Paper>
    </Box>
  );
}