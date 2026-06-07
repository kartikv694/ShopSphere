export const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

export function getToken() {
  return localStorage.getItem("token");
}

export function getRole() {
  return localStorage.getItem("role");
}

export function getStoredUser() {
  try {
    return JSON.parse(localStorage.getItem("user")) || null;
  } catch {
    return null;
  }
}

export function getAuthHeaders() {
  const token = getToken();

  return token
    ? {
        Authorization: `Bearer ${token}`
      }
    : {};
}

export function setSession(data) {
  localStorage.setItem("token", data.token);
  localStorage.setItem("role", data.role);
  localStorage.setItem("user", JSON.stringify(data));
}

export function updateStoredUser(updates) {
  const currentUser = getStoredUser() || {};
  const nextUser = {
    ...currentUser,
    ...updates
  };

  localStorage.setItem("user", JSON.stringify(nextUser));
  window.dispatchEvent(new Event("userUpdated"));

  return nextUser;
}

export function clearSession() {
  // Note: selectedLocation is intentionally NOT cleared — address persists across sessions
  localStorage.removeItem("token");
  localStorage.removeItem("role");
  localStorage.removeItem("user");
  localStorage.removeItem("cart");
  window.dispatchEvent(new Event("cartUpdated"));
  window.dispatchEvent(new Event("userUpdated"));
}

export function isTokenExpired(token = getToken()) {
  if (!token) {
    return true;
  }

  try {
    const payload = JSON.parse(atob(token.split(".")[1]));
    const expiryTime = payload.exp * 1000;

    return Date.now() >= expiryTime;
  } catch {
    return true;
  }
}

export function hasValidSession(expectedRole) {
  const token = getToken();
  const role = getRole();

  if (!token || isTokenExpired(token)) {
    return false;
  }

  return expectedRole ? role === expectedRole : true;
}
