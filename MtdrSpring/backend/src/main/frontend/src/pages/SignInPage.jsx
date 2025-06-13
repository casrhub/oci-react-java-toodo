import React, { useState } from 'react';
import { Box, Button, Paper, TextField, Typography } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { auth, db } from '../firebase';
import { signInWithEmailAndPassword } from 'firebase/auth';
import { doc, getDoc } from 'firebase/firestore';

export default function SignInPage() {
  const nav = useNavigate();
  const [email, setEmail] = useState('');
  const [pass, setPass] = useState('');
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const { user } = await signInWithEmailAndPassword(auth, email, pass);
      const snap = await getDoc(doc(db, 'roles', user.uid));
      const role = snap.exists() ? snap.data().role : null;
      nav(role === 'manager' ? '/manager/tasks' : '/dev/tasks', { replace: true });
    } catch (err) {
      setError(err.message);
      setLoading(false);
    }
  };

  return (
    <Box sx={{ mt: 6, display: 'flex', justifyContent: 'center' }}>
      <Paper sx={{ p: 4, minWidth: 320, textAlign: 'center' }}>
        <Typography variant="h4" sx={{ fontWeight: 'bold', mb: 3 }}>
          Welcome
        </Typography>
        <Typography variant="h5" gutterBottom>
          Sign In
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
          {error && <Typography color="error">{error}</Typography>}
          <Button type="submit" variant="contained" disabled={loading} sx={{ bgcolor: '#C74634' }}>
            {loading ? 'Signing in…' : 'Sign In'}
          </Button>
          <Button onClick={() => nav('/sign-up')} disabled={loading}>
            Create account
          </Button>
        </Box>
      </Paper>
    </Box>
  );
}
