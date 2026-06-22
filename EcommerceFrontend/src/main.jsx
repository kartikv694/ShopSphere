import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import "leaflet/dist/leaflet.css";
import { initAuthSession, getRefreshToken, getRole } from './utils/auth.js'
import { syncCartFromServer } from './utils/cartApi.js'
import {
  syncSavedLocationFromServer,
  syncRecentlyViewedFromServer
} from './utils/userPreferencesApi.js'

// Resumes the silent token-refresh cycle if a session already exists
// (e.g. the browser was closed and reopened), so the user stays logged in.
initAuthSession();

// If a customer session already exists (refresh token present), pull all
// their cross-device data from the server immediately on boot — so cart
// count, saved address, and recently-viewed are correct before any
// component even mounts, not just after the first network round-trip.
if (getRefreshToken() && getRole() !== "ADMIN") {
  syncCartFromServer();
  syncSavedLocationFromServer();
  syncRecentlyViewedFromServer();
}

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
