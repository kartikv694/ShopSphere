import axios from "axios";

export const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

// ----- basic getters -----

export function getToken() {
  return localStorage.getItem("token");
}

export function getRefreshToken() {
  return localStorage.getItem("refreshToken");
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

// ----- session lifecycle -----

export function setSession(data) {
  localStorage.setItem("token", data.token);

  if (data.refreshToken) {
    localStorage.setItem("refreshToken", data.refreshToken);
  }

  if (data.role) {
    localStorage.setItem("role", data.role);
  }

  localStorage.setItem("user", JSON.stringify(data));

  // Restart the background refresh cycle with the freshly issued token.
  scheduleTokenRefresh();
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

function clearSessionStorageOnly() {
  localStorage.removeItem("token");
  localStorage.removeItem("refreshToken");
  localStorage.removeItem("role");
  localStorage.removeItem("user");
  localStorage.removeItem("cart");
  // Clear cross-device sync mirrors — the server is the source of truth
  // now, so the next user on this browser shouldn't see the previous
  // user's address or recently-viewed history.
  localStorage.removeItem("selectedLocation");
  localStorage.removeItem("recentlyViewed");
}

export function clearSession() {
  cancelScheduledRefresh();
  clearSessionStorageOnly();
  window.dispatchEvent(new Event("cartUpdated"));
  window.dispatchEvent(new Event("userUpdated"));
}

/**
 * Manual logout: tells the backend to revoke the refresh token (so it can
 * never be used again even if it had not yet expired) and only then clears
 * the local session. Falls back to a local-only clear if the request fails
 * (e.g. offline), since the user should always be able to log out locally.
 */
export async function logout() {
  const refreshToken = getRefreshToken();

  cancelScheduledRefresh();

  if (refreshToken) {
    try {
      await fetch(`${API_BASE_URL}/api/auth/logout`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({ refreshToken })
      });
    } catch {
      // Ignore network errors on logout — we still clear the local session below.
    }
  }

  clearSessionStorageOnly();
  window.dispatchEvent(new Event("cartUpdated"));
  window.dispatchEvent(new Event("userUpdated"));
}

// ----- token expiry helpers -----

export function decodeTokenPayload(token) {
  try {
    return JSON.parse(atob(token.split(".")[1]));
  } catch {
    return null;
  }
}

export function isTokenExpired(token = getToken()) {
  if (!token) {
    return true;
  }

  const payload = decodeTokenPayload(token);

  if (!payload?.exp) {
    return true;
  }

  return Date.now() >= payload.exp * 1000;
}

/**
 * A session is considered valid as long as we have a refresh token on file.
 * The short-lived access token is allowed to expire silently in the
 * background — scheduleTokenRefresh() / apiFetch() take care of renewing it
 * — so the user is only ever logged out when the refresh token itself is
 * gone (expired server-side, revoked, or the user explicitly logged out).
 */
export function hasValidSession(expectedRole) {
  const refreshToken = getRefreshToken();
  const token = getToken();

  if (!refreshToken && !token) {
    return false;
  }

  const role = getRole();

  return expectedRole ? role === expectedRole : true;
}

// ----- silent refresh -----

let refreshTimerId = null;
let refreshInFlight = null;

export function cancelScheduledRefresh() {
  if (refreshTimerId) {
    clearTimeout(refreshTimerId);
    refreshTimerId = null;
  }
}

/**
 * Calls /api/auth/refresh using the stored refresh token, rotates both
 * tokens in localStorage, and returns the new access token. Concurrent
 * callers share a single in-flight request instead of triggering parallel
 * refreshes (which would otherwise race against the server-side rotation).
 */
export async function refreshAccessToken() {
  if (refreshInFlight) {
    return refreshInFlight;
  }

  const refreshToken = getRefreshToken();

  if (!refreshToken) {
    return null;
  }

  refreshInFlight = (async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/api/auth/refresh`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({ refreshToken })
      });

      if (!response.ok) {
        // Refresh token is invalid, expired, or revoked — the session is over.
        clearSession();
        return null;
      }

      const data = await response.json();

      localStorage.setItem("token", data.token);

      if (data.refreshToken) {
        localStorage.setItem("refreshToken", data.refreshToken);
      }

      scheduleTokenRefresh();

      return data.token;
    } catch {
      // Network hiccup — keep the existing tokens, try again on the next cycle.
      return null;
    } finally {
      refreshInFlight = null;
    }
  })();

  return refreshInFlight;
}

/**
 * Schedules a silent refresh shortly before the current access token
 * expires, and keeps rescheduling itself after every successful refresh.
 * This is what keeps a user logged in indefinitely (until the refresh
 * token itself expires or they log out) without ever seeing a login
 * screen just because 15 minutes passed.
 */
export function scheduleTokenRefresh() {
  cancelScheduledRefresh();

  const token = getToken();
  const refreshToken = getRefreshToken();

  if (!token || !refreshToken) {
    return;
  }

  const payload = decodeTokenPayload(token);

  if (!payload?.exp) {
    return;
  }

  const expiresAtMs = payload.exp * 1000;
  const now = Date.now();

  // Refresh 60s before expiry, but never schedule something further than
  // ~10 minutes out at a time, and never less than 2s out.
  const bufferMs = 60 * 1000;
  const msUntilRefresh = Math.max(expiresAtMs - now - bufferMs, 2000);

  refreshTimerId = setTimeout(async () => {
    await refreshAccessToken();
  }, msUntilRefresh);
}

// ----- axios interceptor (covers the many components using axios directly) -----

let axiosInterceptorInstalled = false;

/**
 * Installs a global axios response interceptor that, on a 401, attempts a
 * single silent token refresh and retries the original request once. This
 * gives every existing axios.get/post call site in the app the same
 * "don't log the user out just because the access token expired" behavior
 * as apiFetch(), without having to touch each call site individually.
 */
function installAxiosInterceptor() {
  if (axiosInterceptorInstalled) {
    return;
  }

  axiosInterceptorInstalled = true;

  axios.interceptors.response.use(
    (response) => response,
    async (error) => {
      const originalRequest = error.config;

      if (
        error.response?.status === 401 &&
        !originalRequest._retried &&
        getRefreshToken()
      ) {
        originalRequest._retried = true;

        const newToken = await refreshAccessToken();

        if (newToken) {
          originalRequest.headers = {
            ...originalRequest.headers,
            Authorization: `Bearer ${newToken}`
          };

          return axios(originalRequest);
        }
      }

      return Promise.reject(error);
    }
  );
}

/**
 * Restores the refresh cycle on app startup / page reload, if a session
 * exists. This is what makes "closing the browser and reopening it" keep
 * the user logged in: the refresh token survives in localStorage, and as
 * soon as the app boots again this resumes silently renewing the access
 * token in the background.
 */
export function initAuthSession() {
  installAxiosInterceptor();

  const refreshToken = getRefreshToken();

  if (!refreshToken) {
    return;
  }

  if (isTokenExpired()) {
    // Access token already expired while the browser was closed — refresh now.
    refreshAccessToken();
  } else {
    scheduleTokenRefresh();
  }
}

// ----- authenticated fetch wrapper -----

/**
 * fetch() wrapper that attaches the current access token, and transparently
 * retries once after a silent refresh if the server responds 401 (e.g. the
 * access token expired right before the scheduled background refresh ran).
 * Intended for new call sites (like the cart API); existing axios/fetch
 * call sites keep working unmodified since scheduleTokenRefresh() already
 * keeps localStorage's token fresh in the background.
 */
export async function apiFetch(path, options = {}) {
  const url = path.startsWith("http") ? path : `${API_BASE_URL}${path}`;

  const buildOptions = () => ({
    ...options,
    headers: {
      ...(options.headers || {}),
      ...getAuthHeaders()
    }
  });

  let response = await fetch(url, buildOptions());

  if (response.status === 401 && getRefreshToken()) {
    const newToken = await refreshAccessToken();

    if (newToken) {
      response = await fetch(url, buildOptions());
    }
  }

  return response;
}
