// src/context/AuthContext.jsx
import React, { createContext, useContext, useEffect, useState } from 'react';
import { onAuthStateChanged, signOut as fbSignOut } from 'firebase/auth';
import { doc, getDoc, setDoc } from 'firebase/firestore';
import { auth, db } from '../firebase';

const AuthContext = createContext();

/**
 * Provee `user`, `role` y helpers de sesión.
 */
export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [role, setRole] = useState(null);
  const [loading, setLoad] = useState(true);

  useEffect(() => {
    const unsub = onAuthStateChanged(auth, async (fbUser) => {
      setUser(fbUser);
      if (fbUser) {
        const ref = doc(db, 'roles', fbUser.uid);
        const snap = await getDoc(ref);
        setRole(snap.exists() ? snap.data().role : null);
      } else {
        setRole(null);
      }
      setLoad(false);
    });
    return unsub;
  }, []);

  const signOut = () => fbSignOut(auth);

  return (
    <AuthContext.Provider value={{ user, role, loading, signOut }}>{children}</AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);

/**
 * Helper para guardar rol durante el registro.
 */
export async function saveUserRole(uid, role) {
  await setDoc(doc(db, 'roles', uid), { role }, { merge: true });
}
