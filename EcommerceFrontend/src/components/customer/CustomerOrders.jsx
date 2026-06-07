import { useCallback, useEffect, useState } from "react";
import axios from "axios";
import { toast } from "react-toastify";
import { API_BASE_URL, getAuthHeaders } from "../../utils/auth";
import {
  ORDER_STEPS,
  formatPrice,
  getOrderItems,
  getOrderProductImage,
  getOrderTotal,
  getProductFromOrderItem,
  getStepIndex,
  normalizeOrderStatus
} from "../../utils/orders";
import "./Customer.css";

function OrderTracker({ status }) {
  const activeStep = getStepIndex(status);

  return (
    <div className="order-tracker">
      {ORDER_STEPS.map((step, index) => (
        <div
          className={
            index <= activeStep
              ? "tracker-step tracker-step-active"
              : "tracker-step"
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

function CustomerOrders() {
  const [orders, setOrders] = useState([]);
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [loading, setLoading] = useState(true);

  const fetchOrders = useCallback(async (showError = false) => {
    try {
      const response = await axios.get(
        `${API_BASE_URL}/api/orders/my-orders`,
        {
          headers: getAuthHeaders()
        }
      );

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
        toast.error("Unable to load orders");
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

  return (
    <div className="orders-page">
      <h1 className="orders-title">My Orders</h1>

      {loading ? (
        <div className="orders-message">Loading orders...</div>
      ) : orders.length === 0 ? (
        <div className="orders-message">No orders found</div>
      ) : (
        orders.map((order) => {
          const items = getOrderItems(order);
          const total = getOrderTotal(order);
          const status = normalizeOrderStatus(order.status);

          return (
            <button
              className="order-card order-card-clickable"
              key={order.id}
              onClick={() => setSelectedOrder(order)}
              type="button"
            >
              <div className="order-header">
                <div>
                  <h3>Order #{order.id}</h3>
                  <p>
                    {order.orderDate
                      ? new Date(order.orderDate).toLocaleString()
                      : "Date unavailable"}
                  </p>
                </div>

                <div>
                  <h3 className="order-status">{status}</h3>
                  <p className="order-payment-mode">
                    Payment: {order.paymentMethod || "Not available"}
                  </p>
                  <h2 className="order-total">
                    {formatPrice(total)}
                  </h2>
                </div>
              </div>

              <OrderTracker status={status} />

              <div className="order-summary-line">
                {items.length} item{items.length === 1 ? "" : "s"}
              </div>
            </button>
          );
        })
      )}

      {selectedOrder && (
        <div className="order-details-overlay">
          <div className="order-details-panel">
            <button
              className="order-details-close"
              onClick={() => setSelectedOrder(null)}
              type="button"
            >
              X
            </button>

            <div className="order-details-header">
              <div>
                <h2>Order #{selectedOrder.id}</h2>
                <p>
                  {selectedOrder.orderDate
                    ? new Date(selectedOrder.orderDate).toLocaleString()
                    : "Date unavailable"}
                </p>
              </div>

              <div>
                <h3>{normalizeOrderStatus(selectedOrder.status)}</h3>
                <p>{formatPrice(getOrderTotal(selectedOrder))}</p>
              </div>
            </div>

            <OrderTracker status={selectedOrder.status} />

            <div className="order-details-meta">
              <p>
                <strong>Payment:</strong>{" "}
                {selectedOrder.paymentMethod || "Not available"}
              </p>
              <p>
                <strong>Delivery Address:</strong>{" "}
                {selectedOrder.address || "Not available"}
              </p>
            </div>

            <div className="order-products">
              {getOrderItems(selectedOrder).map((item) => {
                const product = getProductFromOrderItem(item);

                return (
                  <div
                    className="order-product"
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
                      <p>
                        Subtotal:{" "}
                        {formatPrice(
                          item.subtotal ||
                            (item.price || product.price) *
                              (item.quantity || 1)
                        )}
                      </p>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default CustomerOrders;
