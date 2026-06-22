import { apiFetch } from "./auth";

const LOCATION_CACHE_KEY = "selectedLocation";
const RECENTLY_VIEWED_CACHE_KEY = "recentlyViewed";
const RECENTLY_VIEWED_LIMIT = 6;

/**
 * Saved delivery location and "Recently Viewed" products now live in the
 * database, tied to the logged-in user (see /api/user/saved-location and
 * /api/user/recently-viewed on the backend). This is what makes them
 * follow the account across browsers/devices, the same way the cart does
 * — add a product to the cart or pick an address in Edge, close it, open
 * Chrome, log into the same account, and it's all still there.
 *
 * Just like cartApi.js, every write goes to the server first, and we keep
 * a local mirror under the same localStorage keys the UI already reads
 * from, so existing components (CustomerNavbar's address pill, Checkout's
 * "Use Saved Address", RecentlyViewed) keep working without becoming
 * async-aware themselves.
 */

function readLocationCache() {
  try {
    return JSON.parse(localStorage.getItem(LOCATION_CACHE_KEY)) || null;
  } catch {
    return null;
  }
}

function writeLocationCache(location) {
  if (location) {
    localStorage.setItem(LOCATION_CACHE_KEY, JSON.stringify(location));
  } else {
    localStorage.removeItem(LOCATION_CACHE_KEY);
  }
  window.dispatchEvent(new Event("locationUpdated"));
}

function readRecentlyViewedCache() {
  try {
    return JSON.parse(localStorage.getItem(RECENTLY_VIEWED_CACHE_KEY)) || [];
  } catch {
    return [];
  }
}

function writeRecentlyViewedCache(items) {
  localStorage.setItem(RECENTLY_VIEWED_CACHE_KEY, JSON.stringify(items));
  window.dispatchEvent(new Event("recentlyViewedUpdated"));
}

// ===================== SAVED LOCATION =====================

/**
 * Fetches the authoritative saved location from the server and refreshes
 * the local mirror. Call this after login (and the navbar already does
 * this on mount) so a location saved in another browser shows up here too.
 */
export async function syncSavedLocationFromServer() {
  try {
    const response = await apiFetch("/api/user/saved-location");

    if (!response.ok) {
      return readLocationCache();
    }

    const data = await response.json();

    if (!data.value) {
      return readLocationCache();
    }

    const location = JSON.parse(data.value);
    writeLocationCache(location);

    return location;
  } catch {
    // Offline or server unreachable — fall back to whatever we have locally.
    return readLocationCache();
  }
}

/**
 * Saves the delivery location for the logged-in user, persisting it to the
 * server so it follows the account into any other browser/device.
 */
export async function saveLocation(location) {
  writeLocationCache(location);

  const response = await apiFetch("/api/user/saved-location", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ value: JSON.stringify(location) })
  });

  if (!response.ok) {
    throw new Error("Could not save location");
  }

  return location;
}

export function getCachedLocation() {
  return readLocationCache();
}

// ===================== RECENTLY VIEWED =====================

/**
 * Fetches the authoritative "Recently Viewed" list from the server and
 * refreshes the local mirror.
 */
export async function syncRecentlyViewedFromServer() {
  try {
    const response = await apiFetch("/api/user/recently-viewed");

    if (!response.ok) {
      return readRecentlyViewedCache();
    }

    const data = await response.json();

    if (!data.value) {
      return readRecentlyViewedCache();
    }

    const items = JSON.parse(data.value) || [];
    writeRecentlyViewedCache(items);

    return items;
  } catch {
    return readRecentlyViewedCache();
  }
}

/**
 * Records a product view: moves it to the front of "Recently Viewed",
 * de-duplicates it, caps the list at 6 (same cap as before), and persists
 * the result to the server so it follows the account across browsers.
 */
export async function recordRecentlyViewed(product) {
  let items = readRecentlyViewedCache();

  items = items.filter((item) => item.id !== product.id);
  items.unshift(product);
  items = items.slice(0, RECENTLY_VIEWED_LIMIT);

  writeRecentlyViewedCache(items);

  try {
    const response = await apiFetch("/api/user/recently-viewed", {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ value: JSON.stringify(items) })
    });

    if (!response.ok) {
      throw new Error("Could not sync recently viewed");
    }
  } catch (error) {
    // Non-fatal — the local mirror is already updated, so the UI is still
    // correct on this device; it just may not have reached the server.
    console.log(error);
  }

  return items;
}

export function getCachedRecentlyViewed() {
  return readRecentlyViewedCache();
}
