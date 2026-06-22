import { useEffect, useState } from "react";
import axios from "axios";
import { toast } from "react-toastify";
import { useNavigate } from "react-router-dom";
import {
  FaCreditCard,
  FaMobileAlt,
  FaTimes
} from "react-icons/fa";
import { API_BASE_URL, getAuthHeaders, getStoredUser } from "../utils/auth";
import { syncCartFromServer, clearCart as clearCartApi } from "../utils/cartApi";
import {
  saveLocation as saveLocationApi,
  syncSavedLocationFromServer,
  getCachedLocation
} from "../utils/userPreferencesApi";
import "./Checkout.css";

const formatPrice = (value) =>
  new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 0
  }).format(Number(value || 0));

const loadScript = (src) =>
  new Promise((resolve, reject) => {
    const existingScript = document.querySelector(`script[src="${src}"]`);

    if (existingScript) {
      resolve();
      return;
    }

    const script = document.createElement("script");
    script.src = src;
    script.onload = resolve;
    script.onerror = reject;
    document.body.appendChild(script);
  });

function Checkout() {
  const navigate = useNavigate();
  const user = getStoredUser();
  const initialSavedLocation = getCachedLocation();

  const [cartItems, setCartItems] = useState(
    () => JSON.parse(localStorage.getItem("cart")) || []
  );
  const [savedLocation, setSavedLocation] = useState(initialSavedLocation);
  const [placingOrder, setPlacingOrder] = useState(false);
  const [paymentScreen, setPaymentScreen] = useState(null);
  const [selectedGateway, setSelectedGateway] = useState("Razorpay");

  // Reconcile cart from server before checkout begins.
  useEffect(() => {
    syncCartFromServer().then((items) => setCartItems(items));
  }, []);

  // Pull the authoritative saved address from the server so a location
  // chosen in another browser/device shows up in the checkout form here.
  useEffect(() => {
    syncSavedLocationFromServer().then((loc) => {
      if (loc) {
        setSavedLocation(loc);
        // Pre-fill form fields if still empty (user hasn't typed anything yet)
        setFormData((prev) => ({
          ...prev,
          fullName: prev.fullName || loc.fullName || user?.name || "",
          phone: prev.phone || loc.phone || "",
          address: prev.address || loc.fullAddress || loc.address || "",
          city: prev.city || loc.city || "",
          state: prev.state || loc.state || "",
          pincode: prev.pincode || loc.pincode || ""
        }));
      }
    });
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  const [formData, setFormData] = useState({
    fullName: initialSavedLocation?.fullName || user?.name || "",
    phone: initialSavedLocation?.phone || "",
    address:
      initialSavedLocation?.fullAddress ||
      initialSavedLocation?.address ||
      "",
    city: initialSavedLocation?.city || "",
    state: initialSavedLocation?.state || "",
    pincode: initialSavedLocation?.pincode || "",
    paymentMethod: "Cash On Delivery"
  });

  const totalPrice = cartItems.reduce(
    (total, item) => total + item.price * item.quantity,
    0
  );

  const gatewayOptions = [
    {
      name: "Razorpay",
      title: "Razorpay Checkout",
      support: "UPI, cards, wallets, net banking, EMI",
      recommended: true
    },
    {
      name: "Paytm",
      title: "Paytm Payment Gateway",
      support: "Paytm wallet, UPI, cards, net banking"
    }
  ];

  const handleChange = (event) => {
    setFormData({
      ...formData,
      [event.target.name]: event.target.value
    });
  };

  const handleSaveAddress = () => {
    const locationData = {
      fullName: formData.fullName,
      phone: formData.phone,
      address: formData.address,
      city: formData.city,
      state: formData.state,
      pincode: formData.pincode
    };

    // Persist to server so the same address appears on any other
    // browser/device the user opens next time.
    saveLocationApi(locationData).catch(console.log);

    setSavedLocation(locationData);
    toast.success("Address saved successfully");
    window.dispatchEvent(new Event("locationUpdated"));
  };

  const applySavedLocation = () => {
    if (!savedLocation) {
      toast.info("No saved address found. Please set your location first.");
      return;
    }

    setFormData((prev) => ({
      ...prev,
      fullName: savedLocation.fullName || user?.name || prev.fullName || "",
      phone: savedLocation.phone || user?.phone || prev.phone || "",
      address: savedLocation.fullAddress || savedLocation.address || "",
      city: savedLocation.city || "",
      state: savedLocation.state || "",
      pincode: savedLocation.pincode || ""
    }));
    toast.success("Address applied!");
  };

  const validateCheckout = () => {
    if (cartItems.length === 0) {
      toast.error("Your cart is empty");
      return false;
    }

    if (!formData.address.trim()) {
      toast.error("Please enter delivery address");
      return false;
    }

    return true;
  };

  const getOrderItems = () =>
    cartItems.map((item) => ({
      productId: item.id,
      quantity: item.quantity
    }));

  const completeOrder = async (paymentMethod, paymentDetails = {}) => {
    const payload = {
      address: formData.address,
      paymentMethod,
      paymentDetails,
      items: getOrderItems()
    };

    await axios.post(`${API_BASE_URL}/api/orders/checkout`, payload, {
      headers: getAuthHeaders()
    });

    toast.success("Order placed successfully");

    try {
      await clearCartApi();
    } catch (error) {
      console.log(error);
      // Non-fatal — the order already went through, so just fall back to
      // clearing the local mirror so the UI doesn't show stale items.
      localStorage.removeItem("cart");
      window.dispatchEvent(new Event("cartUpdated"));
    }

    setTimeout(() => {
      navigate("/customer/my-orders");
    }, 1200);
  };

  const createPaymentPayload = (gateway, method) => ({
    gateway,
    amount: totalPrice,
    currency: "INR",
    paymentMethod: method,
    customer: {
      name: formData.fullName,
      email: user?.email || "",
      phone: formData.phone
    },
    address: formData.address,
    items: getOrderItems()
  });

  const startRazorpayCheckout = async (method) => {
    setPlacingOrder(true);

    try {
      const paymentOrder = await axios.post(
        `${API_BASE_URL}/api/payments/razorpay/order`,
        createPaymentPayload("Razorpay", method),
        {
          headers: getAuthHeaders()
        }
      );

      await loadScript("https://checkout.razorpay.com/v1/checkout.js");

      const data = paymentOrder.data;

      const options = {
        key: data.key || data.keyId,
        amount: data.amount,
        currency: data.currency || "INR",
        name: "ShopSphere",
        description: "ShopSphere order payment",
        order_id: data.orderId || data.id,
        method: {
          upi: method === "UPI",
          card: method === "Card",
          netbanking: true,
          wallet: true
        },
        prefill: {
          name: formData.fullName,
          email: user?.email || "",
          contact: formData.phone
        },
        theme: {
          color: "#007185"
        },
        handler: async (response) => {
          try {
            await axios.post(
              `${API_BASE_URL}/api/payments/razorpay/verify`,
              response,
              {
                headers: getAuthHeaders()
              }
            );

            await completeOrder(
              method === "UPI" ? "UPI - Razorpay" : "Card - Razorpay",
              response
            );
          } catch (error) {
            console.log(error);
            toast.error("Payment verification failed");
          } finally {
            setPlacingOrder(false);
          }
        },
        modal: {
          ondismiss: () => {
            setPlacingOrder(false);
          }
        }
      };

      if (!window.Razorpay) {
        throw new Error("Razorpay checkout failed to load");
      }

      const razorpay = new window.Razorpay(options);
      razorpay.open();
    } catch (error) {
      console.log(error);
      toast.error("Unable to open payment gateway");
      setPlacingOrder(false);
    }
  };

  const startPaytmCheckout = async () => {
    // Paytm Business Payments API & JS Checkout SDK has been discontinued for new integrations.
    // Showing a clear message and falling back to Razorpay.
    toast.warning(
      "Paytm gateway is currently unavailable. Please use Razorpay instead.",
      { autoClose: 4000 }
    );
    setSelectedGateway("Razorpay");
    // Automatically start Razorpay for the same method
    startRazorpayCheckout(method);
  };

  const submitGatewayPayment = () => {
    const method = paymentScreen;

    if (selectedGateway === "Paytm") {
      startPaytmCheckout(method);
      return;
    }

    startRazorpayCheckout(method);
  };

  const handlePlaceOrder = () => {
    if (!validateCheckout()) {
      return;
    }

    if (formData.paymentMethod === "UPI") {
      setSelectedGateway("Razorpay");
      setPaymentScreen("UPI");
      return;
    }

    if (formData.paymentMethod === "Card") {
      setSelectedGateway("Razorpay");
      setPaymentScreen("Card");
      return;
    }

    setPlacingOrder(true);
    completeOrder("Cash On Delivery").finally(() => setPlacingOrder(false));
  };

  const closePaymentScreen = () => {
    if (!placingOrder) {
      setPaymentScreen(null);
    }
  };

  return (
    <div className="checkout-page">
      <div className="checkout-left">
        <h1 className="checkout-title">Checkout</h1>

        {savedLocation && (
          <div className="saved-location-box">
            <h3>Saved Location</h3>

            <p className="saved-location-text">
              {savedLocation.fullAddress || savedLocation.address}
            </p>

            <div className="location-buttons">
              <button
                className="use-location-btn"
                onClick={applySavedLocation}
              >
                Use Current Location
              </button>

              <button
                className="new-address-btn"
                onClick={() => {
                  setFormData({
                    fullName: "",
                    phone: "",
                    address: "",
                    city: "",
                    state: "",
                    pincode: "",
                    paymentMethod: "Cash On Delivery"
                  });
                }}
              >
                Add New Address
              </button>
            </div>
          </div>
        )}

        <div className="checkout-form">
          <input
            type="text"
            name="fullName"
            placeholder="Full Name"
            value={formData.fullName}
            onChange={handleChange}
          />

          <input
            type="text"
            name="phone"
            placeholder="Phone Number"
            value={formData.phone}
            onChange={handleChange}
          />

          <textarea
            name="address"
            placeholder="Address"
            value={formData.address}
            onChange={handleChange}
          />

          <input
            type="text"
            name="city"
            placeholder="City"
            value={formData.city}
            onChange={handleChange}
          />

          <input
            type="text"
            name="state"
            placeholder="State"
            value={formData.state}
            onChange={handleChange}
          />

          <input
            type="text"
            name="pincode"
            placeholder="Pincode"
            value={formData.pincode}
            onChange={handleChange}
          />

          <select
            name="paymentMethod"
            value={formData.paymentMethod}
            onChange={handleChange}
          >
            <option>Cash On Delivery</option>
            <option>UPI</option>
            <option>Card</option>
          </select>

          <button
            type="button"
            className="save-address-btn"
            onClick={handleSaveAddress}
          >
            Save Address
          </button>
        </div>
      </div>

      <div className="checkout-right">
        <h2>Order Summary</h2>

        {cartItems.map((item) => (
          <div
            className="summary-item"
            key={item.id}
          >
            <img
              src={item.imageUrls?.[0]}
              alt={item.name}
            />

            <div>
              <h4>{item.name}</h4>
              <p>Qty: {item.quantity}</p>
            </div>

            <h4>{formatPrice(item.price * item.quantity)}</h4>
          </div>
        ))}

        <h1 className="total-price">
          Total: {formatPrice(totalPrice)}
        </h1>

        <button
          className="place-order-btn"
          onClick={handlePlaceOrder}
          disabled={placingOrder || cartItems.length === 0}
        >
          {placingOrder ? "Processing..." : "Place Order"}
        </button>
      </div>

      {paymentScreen && (
        <div className="payment-overlay">
          <div className="payment-panel">
            <button
              className="payment-close"
              onClick={closePaymentScreen}
              disabled={placingOrder}
              type="button"
            >
              <FaTimes />
            </button>

            <div className="payment-heading">
              <div className="payment-heading-icon">
                {paymentScreen === "UPI" ? <FaMobileAlt /> : <FaCreditCard />}
              </div>

              <div>
                <h2>
                  {paymentScreen === "UPI"
                    ? "Choose UPI Payment"
                    : "Choose Card Payment"}
                </h2>
                <p>Total payable: {formatPrice(totalPrice)}</p>
              </div>
            </div>

            <div className="gateway-grid">
              {gatewayOptions.map((gateway) => (
                <button
                  type="button"
                  className={
                    selectedGateway === gateway.name
                      ? "gateway-card selected"
                      : "gateway-card"
                  }
                  key={gateway.name}
                  onClick={() => setSelectedGateway(gateway.name)}
                >
                  <strong>{gateway.title}</strong>
                  <span>{gateway.support}</span>
                </button>
              ))}
            </div>

            <button
              className="pay-now-btn"
              onClick={submitGatewayPayment}
              disabled={placingOrder}
              type="button"
            >
              {placingOrder
                ? "Opening Payment Gateway..."
                : `Continue With ${selectedGateway}`}
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

export default Checkout;
