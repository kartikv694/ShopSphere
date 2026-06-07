import { useCallback, useEffect, useState } from "react";
import axios from "axios";
import { toast } from "react-toastify";
import { API_BASE_URL, getAuthHeaders } from "../utils/auth";
import {
  ORDER_STEPS,
  formatPrice,
  getOrderItems,
  getOrderProductImage,
  getOrderTotal,
  getProductFromOrderItem,
  getStepIndex,
  normalizeOrderStatus
} from "../utils/orders";
import "./AdminOrders.css";

function AdminOrderTracker({ status }) {
  const activeStep = getStepIndex(status);

  return (
    <div className="admin-order-tracker">
      {ORDER_STEPS.map((step, index) => (
        <div
          className={
            index <= activeStep
              ? "admin-tracker-step active"
              : "admin-tracker-step"
          }
          key={step.key}
        >
          <span>{index + 1}</span>
          <p>{step.label}</p>
        </div>
      ))}
    </div>
  );
}

function AdminOrders() {
  const [orders, setOrders] = useState([]);
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [updatingOrderId, setUpdatingOrderId] = useState(null);

  const fetchOrders = useCallback(async (showError = false) => {
    try {
      let response;

      try {
        response = await axios.get(`${API_BASE_URL}/api/orders/all`, {
          headers: getAuthHeaders()
        });
      } catch {
        response = await axios.get(`${API_BASE_URL}/api/orders`, {
          headers: getAuthHeaders()
        });
      }

      const orderList = Array.isArray(response.data) ? response.data : [];
      setOrders(orderList);

      if (selectedOrder) {
        const updatedSelectedOrder = orderList.find(
          (order) => order.id === selectedOrder.id
        );
        setSelectedOrder(updatedSelectedOrder || null);
      }
    } catch (error) {
      console.log(error);

      if (showError) {
        if (error?.response?.status === 403) {
          toast.error("Session expired or insufficient permissions. Please log in again.");
        } else {
          toast.error("Unable to load orders");
        }
      }
    } finally {
      setLoading(false);
    }
  }, [selectedOrder]);

  useEffect(() => {
    const initialLoadId = setTimeout(() => {
      fetchOrders(true);
    }, 0);

    const intervalId = setInterval(() => {
      fetchOrders();
    }, 15000);

    return () => {
      clearTimeout(initialLoadId);
      clearInterval(intervalId);
    };
  }, [fetchOrders]);

  const updateOrderStatus = async (orderId, status) => {
    setUpdatingOrderId(orderId);

    try {
      await axios.put(
        `${API_BASE_URL}/api/orders/${orderId}/status`,
        {
          status
        },
        {
          headers: getAuthHeaders()
        }
      );

      toast.success(`Order marked as ${status.toLowerCase()}`);
      await fetchOrders();

      if (status === "SHIPPED") {
        setTimeout(() => {
          updateOrderStatus(orderId, "DELIVERED");
        }, 30000);
      }
    } catch (error) {
      console.error("Order status update error:", error);
      if (error?.response?.status === 403) {
<<<<<<< HEAD
        toast.error("Access denied: You don\'t have permission to update this order.");
=======
        toast.error("Access denied: You don't have permission to update this order.");
>>>>>>> bd2e608 (Project Completed Deployment Pending)
      } else if (error?.response?.status === 404) {
        toast.error("Order not found.");
      } else {
        toast.error("Unable to update order status. Please try again.");
      }
    } finally {
      setUpdatingOrderId(null);
    }
  };

  const renderAction = (order) => {
    const status = normalizeOrderStatus(order.status);

    if (status === "PLACED") {
      return (
        <button
          className="admin-order-action"
          disabled={updatingOrderId === order.id}
          onClick={() => updateOrderStatus(order.id, "SHIPPED")}
          type="button"
        >
          {updatingOrderId === order.id
            ? "Updating..."
            : "Proceed to Shipping"}
        </button>
      );
    }

    if (status === "SHIPPED") {
      return (
        <button
          className="admin-order-action secondary"
          disabled={updatingOrderId === order.id}
          onClick={() => updateOrderStatus(order.id, "DELIVERED")}
          type="button"
        >
          {updatingOrderId === order.id
            ? "Updating..."
            : "Mark Delivered"}
        </button>
      );
    }

    return <span className="admin-order-complete">Completed</span>;
  };

  return (
    <div className="admin-orders-page">
      <div className="admin-orders-header">
        <div>
          <h1>Orders</h1>
          <p>Review customer orders and update fulfilment status.</p>
        </div>
      </div>

      {loading ? (
        <div className="admin-orders-message">Loading orders...</div>
      ) : orders.length === 0 ? (
        <div className="admin-orders-message">No orders found</div>
      ) : (
        <div className="admin-orders-list">
          {orders.map((order) => {
            const items = getOrderItems(order);
            const status = normalizeOrderStatus(order.status);

            return (
              <div
                className="admin-order-card"
                key={order.id}
              >
                <button
                  className="admin-order-main"
                  onClick={() => setSelectedOrder(order)}
                  type="button"
                >
                  <div>
                    <h2>Order #{order.id}</h2>
                    <p>
                      {order.orderDate
                        ? new Date(order.orderDate).toLocaleString()
                        : "Date unavailable"}
                    </p>
                    <p>
                      Customer:{" "}
                      {order.customer?.name ||
                        order.user?.name ||
                        order.customerName ||
                        order.customer?.email ||
                        order.user?.email ||
                        "Customer"}
                    </p>
                  </div>

                  <div>
                    <strong>{status}</strong>
                    <p>{items.length} item{items.length === 1 ? "" : "s"}</p>
                    <h3>{formatPrice(getOrderTotal(order))}</h3>
                  </div>
                </button>

                <AdminOrderTracker status={status} />

                <div className="admin-order-footer">
                  <p>Payment: {order.paymentMethod || "Not available"}</p>
                  {renderAction(order)}
                </div>
              </div>
            );
          })}
        </div>
      )}

      {selectedOrder && (
        <div className="admin-order-overlay">
          <div className="admin-order-panel">
            <button
              className="admin-order-close"
              onClick={() => setSelectedOrder(null)}
              type="button"
            >
              X
            </button>

            <div className="admin-order-panel-header">
              <div>
                <h2>Order #{selectedOrder.id}</h2>
                <p>
                  {selectedOrder.orderDate
                    ? new Date(selectedOrder.orderDate).toLocaleString()
                    : "Date unavailable"}
                </p>
              </div>

              <div>
                <strong>{normalizeOrderStatus(selectedOrder.status)}</strong>
                <h3>{formatPrice(getOrderTotal(selectedOrder))}</h3>
              </div>
            </div>

            <AdminOrderTracker status={selectedOrder.status} />

            <div className="admin-order-meta">
              <p>
                <strong>Customer:</strong>{" "}
                {selectedOrder.user?.name ||
                  selectedOrder.customer?.name ||
                  selectedOrder.customerName ||
                  selectedOrder.customer?.email ||
                  selectedOrder.user?.email ||
                  "Customer"}
              </p>
              <p>
                <strong>Payment:</strong>{" "}
                {selectedOrder.paymentMethod || "Not available"}
              </p>
              <p>
                <strong>Address:</strong>{" "}
                {selectedOrder.address || "Not available"}
              </p>
            </div>

            <div className="admin-order-products">
              {getOrderItems(selectedOrder).map((item) => {
                const product = getProductFromOrderItem(item);

                return (
                  <div
                    className="admin-order-product"
                    key={item.id || product.id}
                  >
                    <img
                      src={getOrderProductImage(product)}
                      alt={product.name || "Product"}
                    />

                    <div>
                      <h3>{product.name || "Product"}</h3>
                      <p>Quantity: {item.quantity || 1}</p>
                      <p>
                        Price: {formatPrice(item.price || product.price)}
                      </p>
                    </div>
                  </div>
                );
              })}
            </div>

            <div className="admin-order-panel-actions">
              {renderAction(selectedOrder)}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default AdminOrders;
