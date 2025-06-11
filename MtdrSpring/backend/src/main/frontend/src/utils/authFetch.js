// src/utils/authFetch.js
//
// Utilidad para realizar peticiones autenticadas contra el backend
// usando el ID Token de Firebase.  Se expone como un *custom hook*
// para mantener la firma original (`useAuthFetch`) pero no depende
// de ningún contexto: lee directamente `auth.currentUser`.
//
// Uso:
//
//   const authFetch = useAuthFetch();
//   const res = await authFetch('/tareas');
//
// Si el usuario no está logueado, envía la petición sin cabecera
// Authorization.
//
import { auth } from '../firebase';

/**
 * Devuelve una función `fetch` preconfigurada.
 * Cada llamada añade el header:
 *
 *   Authorization: Bearer <ID_TOKEN>
 *
 * cuando hay sesión activa en Firebase.
 */
export function useAuthFetch() {
  return async function authFetch(url, options = {}) {
    const user = auth.currentUser;
    const token = user ? await user.getIdToken() : null;

    return fetch(url, {
      ...options,
      headers: {
        ...(options.headers || {}),
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
      },
    });
  };
}
