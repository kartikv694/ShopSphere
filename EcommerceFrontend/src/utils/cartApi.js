import { apiFetch } from "./auth";

const CART_CACHE_KEY = "cart";

/**
 * The cart now lives in the database, tied to the logged-in user (see
 * /api/cart on the backend). Every write here goes to the server first.
 * We also keep a local mirror in localStorage under the same "cart" key
 * the UI already reads from, so existing components (cart count badge,
 * CartPage, Checkout summary) keep working without having to become
 * async-aware themselves — they just re-read localStorage whenever the
 * "cartUpdated" event fires, same as before.
 */

function readCache() {
  try {
    return JSON.parse(localStorage.getItem(CART_CACHE_KEY)) || [];
  } catch {
    return [];
  }
}

function writeCache(items) {
  localStorage.setItem(CART_CACHE_KEY, JSON.stringify(items));
  window.dispatchEvent(new Event("cartUpdated"));
}

// Server CartItemResponse -> the flat shape the existing UI expects
// ({ id, name, price, category, imageUrls, quantity, ... }).
function toUiShape(serverItem) {
  return {
    id: serverItem.productId,
    cartId: serverItem.cartId,
    name: serverItem.name,
    description: serverItem.description,
    price: serverItem.price,
    category: serverItem.category,
    imageUrls: serverItem.imageUrls,
    quantity: serverItem.quantity
  };
}

/**
 * Fetches the authoritative cart from the server and refreshes the local
 * mirror. Call this after login and whenever the cart page mounts, so the
 * displayed cart always reflects what's actually saved for this user.
 */
export async function syncCartFromServer() {
  try {
    const response = await apiFetch("/api/cart");

    if (!response.ok) {
      return readCache();
    }

    const serverItems = await response.json();
    const uiItems = serverItems.map(toUiShape);

    writeCache(uiItems);

    return uiItems;
  } catch {
    // Offline or server unreachable — fall back to whatever we have locally.
    return readCache();
  }
}

/**
 * Adds a product to the cart (or increments its quantity if already
 * present), persisting it against the logged-in user on the server.
 */
export async function addToCart(product, quantity = 1) {
  const response = await apiFetch(
    `/api/cart/add?productId=${product.id}&quantity=${quantity}`,
    { method: "POST" }
  );

  if (!response.ok) {
    throw new Error("Could not add item to cart");
  }

  // Re-sync the full cart so the local mirror (and anything reading
  // quantities/totals from it) stays perfectly in step with the server.
  return syncCartFromServer();
}

export async function updateCartQuantity(productId, quantity) {
  const response = await apiFetch(
    `/api/cart/update?productId=${productId}&quantity=${quantity}`,
    { method: "PUT" }
  );

  if (!response.ok) {
    throw new Error("Could not update item quantity");
  }

  return syncCartFromServer();
}

export async function removeFromCart(productId) {
  const response = await apiFetch(
    `/api/cart/remove?productId=${productId}`,
    { method: "DELETE" }
  );

  if (!response.ok) {
    throw new Error("Could not remove item from cart");
  }

  return syncCartFromServer();
}

export async function clearCart() {
  const response = await apiFetch("/api/cart/clear", { method: "DELETE" });

  if (!response.ok) {
    throw new Error("Could not clear cart");
  }

  writeCache([]);
}

export function getCachedCart() {
  return readCache();
}
