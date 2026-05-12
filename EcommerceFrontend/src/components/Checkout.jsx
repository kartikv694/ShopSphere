import { useEffect, useState } from "react";
import axios from "axios";
import { toast } from "react-toastify";
import "./Checkout.css";

function Checkout() {

  const [cartItems, setCartItems] =
    useState([]);

  const [useSavedAddress, setUseSavedAddress] =
    useState(true);

  const [savedLocation, setSavedLocation] =
    useState(null);

  const [formData, setFormData] =
    useState({

      fullName: "",

      phone: "",

      address: "",

      city: "",

      state: "",

      pincode: "",

      paymentMethod:
        "Cash On Delivery"

    });

  /* LOAD CART */

  useEffect(() => {

    const cart =
      JSON.parse(
        localStorage.getItem("cart")
      ) || [];

    setCartItems(cart);

  }, []);

  /* LOAD SAVED LOCATION */

  useEffect(() => {

    const location =
      JSON.parse(
        localStorage.getItem(
          "selectedLocation"
        )
      );

    if (location) {

      setSavedLocation(location);

    }

  }, []);

  /* AUTO FILL SAVED ADDRESS */

  useEffect(() => {

    if (
      useSavedAddress &&
      savedLocation
    ) {

      setFormData((prev) => ({

        ...prev,

        fullName:
          savedLocation.fullName || "",

        phone:
          savedLocation.phone || "",

          address:
              savedLocation.address || "", address:
              savedLocation.fullAddress ||
              savedLocation.address ||
              "",
        city:
          savedLocation.city || "",

        state:
          savedLocation.state || "",

        pincode:
          savedLocation.pincode || ""

      }));

    }

  }, [useSavedAddress, savedLocation]);

  /* TOTAL */

  const totalPrice =
    cartItems.reduce(
      (total, item) =>
        total +
        item.price * item.quantity,
      0
    );

  /* HANDLE INPUT */

  const handleChange = (e) => {

    setFormData({

      ...formData,

      [e.target.name]:
        e.target.value

    });

  };

  /* SAVE ADDRESS */

  const handleSaveAddress = () => {

    const locationData = {

      fullName:
        formData.fullName,

      phone:
        formData.phone,

      address:
        formData.address,

      city:
        formData.city,

      state:
        formData.state,

      pincode:
        formData.pincode

    };

    localStorage.setItem(
      "selectedLocation",
      JSON.stringify(locationData)
    );

    setSavedLocation(locationData);

    toast.success(
      "Address Saved Successfully 📍"
    );

    window.dispatchEvent(
      new Event("locationUpdated")
    );

  };

  /* PLACE ORDER */

  const handlePlaceOrder = async () => {

    const token =
      localStorage.getItem("token");

    const payload = {

      address:
        formData.address,

      paymentMethod:
        formData.paymentMethod,

      items: cartItems.map(
        (item) => ({

          productId: item.id,

          quantity: item.quantity

        })
      )

    };

    try {

      await axios.post(
        "http://localhost:8080/api/orders/checkout",
        payload,
        {

          headers: {

            Authorization:
              "Bearer " + token

          }

        }
      );

      toast.success(
        "Order Placed Successfully 🎉"
      );

      localStorage.removeItem(
        "cart"
      );

      setTimeout(() => {

        window.location.href =
          "/customer/my-orders";

      }, 2000);

    } catch (error) {

      console.log(error);

      toast.error(
        "Failed To Place Order ❌"
      );

    }

  };

  return (

    <div className="checkout-page">

      {/* LEFT */}

      <div className="checkout-left">

        <h1 className="checkout-title">
          Checkout
        </h1>

        {/* SAVED LOCATION */}

        {
          savedLocation && (

            <div className="saved-location-box">

              <h3>
                Saved Location
              </h3>

                          <p className="saved-location-text">

                              {
                                  savedLocation.fullAddress ||
                                  savedLocation.address
                              }

                          </p>

              <div className="location-buttons">

                <button
                  className="use-location-btn"
                  onClick={() =>
                    setUseSavedAddress(true)
                  }
                >
                  Use Current Location
                </button>

                <button
                  className="new-address-btn"
                  onClick={() => {

                    setUseSavedAddress(false);

                    setFormData({

                      fullName: "",

                      phone: "",

                      address: "",

                      city: "",

                      state: "",

                      pincode: "",

                      paymentMethod:
                        "Cash On Delivery"

                    });

                  }}
                >
                  Add New Address
                </button>

              </div>

            </div>

          )
        }

        {/* FORM */}

        <div className="checkout-form">

          <input
            type="text"
            name="fullName"
            placeholder="Full Name"
            value={
              formData.fullName
            }
            onChange={handleChange}
          />

          <input
            type="text"
            name="phone"
            placeholder="Phone Number"
            value={
              formData.phone
            }
            onChange={handleChange}
          />

          <textarea
            name="address"
            placeholder="Address"
            value={
              formData.address
            }
            onChange={handleChange}
          />

          <input
            type="text"
            name="city"
            placeholder="City"
            value={
              formData.city
            }
            onChange={handleChange}
          />

          <input
            type="text"
            name="state"
            placeholder="State"
            value={
              formData.state
            }
            onChange={handleChange}
          />

          <input
            type="text"
            name="pincode"
            placeholder="Pincode"
            value={
              formData.pincode
            }
            onChange={handleChange}
          />

          <select
            name="paymentMethod"
            value={
              formData.paymentMethod
            }
            onChange={handleChange}
          >

            <option>
              Cash On Delivery
            </option>

            <option>
              UPI
            </option>

            <option>
              Card
            </option>

          </select>

          <button
            type="button"
            className="save-address-btn"
            onClick={
              handleSaveAddress
            }
          >
            Save Address
          </button>

        </div>

      </div>

      {/* RIGHT */}

      <div className="checkout-right">

        <h2>
          Order Summary
        </h2>

        {
          cartItems.map((item) => (

            <div
              className="summary-item"
              key={item.id}
            >

              <img
                src={
                  item.imageUrls?.[0]
                }
                alt={item.name}
              />

              <div>

                <h4>
                  {item.name}
                </h4>

                <p>
                  Qty:
                  {" "}
                  {item.quantity}
                </p>

              </div>

              <h4>

                ₹
                {
                  item.price *
                  item.quantity
                }

              </h4>

            </div>

          ))
        }

        <h1 className="total-price">

          Total:
          {" "}
          ₹{totalPrice}

        </h1>

        <button
          className="place-order-btn"
          onClick={
            handlePlaceOrder
          }
        >
          Place Order
        </button>

      </div>

    </div>

  );

}

export default Checkout;