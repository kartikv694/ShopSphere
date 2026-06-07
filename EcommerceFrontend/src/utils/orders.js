export const ORDER_STEPS = [
  {
    key: "PLACED",
    label: "Order Placed"
  },
  {
    key: "SHIPPED",
    label: "Order Shipped"
  },
  {
    key: "DELIVERED",
    label: "Order Delivered"
  }
];

export const formatPrice = (value) =>
  new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 0
  }).format(Number(value || 0));

export function normalizeOrderStatus(status) {
  const value = String(status || "PLACED")
    .trim()
    .toUpperCase()
    .replaceAll(" ", "_")
    .replaceAll("-", "_");

  if (value.includes("DELIVER")) {
    return "DELIVERED";
  }

  if (value.includes("SHIP")) {
    return "SHIPPED";
  }

  return "PLACED";
}

export function getOrderItems(order) {
  return order?.orderItems || order?.items || [];
}

export function getOrderTotal(order) {
  return order?.totalPrice || order?.totalAmount || order?.total || 0;
}

export function getProductFromOrderItem(item) {
  return item?.product || item || {};
}

export function getOrderProductImage(product) {
  return (
    product?.imageUrls?.[0] ||
    product?.imageUrl ||
    "https://via.placeholder.com/160?text=Product"
  );
}

export function getStepIndex(status) {
  const normalizedStatus = normalizeOrderStatus(status);
  const index = ORDER_STEPS.findIndex((step) => step.key === normalizedStatus);

  return index === -1 ? 0 : index;
}
