// src/utils/authFetch.js
import { useAuth } from "@clerk/clerk-react";

// Custom hook que regresa un fetch preconfigurado con JWT
export function useAuthFetch() {
  const { getToken } = useAuth();

  return async function authFetch(url, options = {}) {
    const token = await getToken();
    return fetch(url, {
      ...options,
      headers: {
        ...(options.headers || {}),
        Authorization: `Bearer ${token}`,
      },
    });
  };
}
