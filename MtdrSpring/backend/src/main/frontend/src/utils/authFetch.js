import { auth } from '../firebase';

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
