import { useEffect, useState } from "react";
import { Modal } from "flowbite-react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import {
  syncCartFromServer,
  updateCartQuantity,
  removeFromCart
} from "../utils/cartApi";
import "./CartPage.css";

function CartPage() {

  const [cartItems, setCartItems] = useState(
    () => JSON.parse(localStorage.getItem("cart")) || []
  );

  const [loading, setLoading] = useState(true);

  const [openDeleteModal, setOpenDeleteModal] =
    useState(false);

  const [selectedProductId, setSelectedProductId] =
    useState(null);

  const navigate = useNavigate();

  // LOAD THE AUTHORITATIVE CART FROM THE SERVER ON MOUNT
  useEffect(() => {

    let isMounted = true;

    syncCartFromServer().then((items) => {

      if (isMounted) {

        setCartItems(items);
        setLoading(false);

      }

    });

    return () => {
      isMounted = false;
    };

  }, []);

  // INCREASE QUANTITY
  const increaseQuantity = async (id) => {

    const product = cartItems.find(
      (item) => item.id === id
    );

    if (!product) return;

    const nextQuantity = product.quantity + 1;

    // OPTIMISTIC UPDATE
    setCartItems((prev) =>
      prev.map((item) =>
        item.id === id
          ? { ...item, quantity: nextQuantity }
          : item
      )
    );

    try {

      const updated = await updateCartQuantity(id, nextQuantity);
      setCartItems(updated);

    } catch (error) {

      console.log(error);
      toast.error("Could not update quantity");

      // ROLL BACK ON FAILURE
      const refreshed = await syncCartFromServer();
      setCartItems(refreshed);

    }

  };

  // DECREASE QUANTITY
  const decreaseQuantity = async (id) => {

    const product = cartItems.find(
      (item) => item.id === id
    );

    if (!product) return;

    // IF ONLY 1 QUANTITY LEFT
    if (product.quantity === 1) {

      setSelectedProductId(id);

      setOpenDeleteModal(true);

      return;

    }

    const nextQuantity = product.quantity - 1;

    // OPTIMISTIC UPDATE
    setCartItems((prev) =>
      prev.map((item) =>
        item.id === id
          ? { ...item, quantity: nextQuantity }
          : item
      )
    );

    try {

      const updated = await updateCartQuantity(id, nextQuantity);
      setCartItems(updated);

    } catch (error) {

      console.log(error);
      toast.error("Could not update quantity");

      const refreshed = await syncCartFromServer();
      setCartItems(refreshed);

    }

  };

  // REMOVE PRODUCT
  const removeProduct = async (id) => {

    // OPTIMISTIC UPDATE
    setCartItems((prev) =>
      prev.filter((item) => item.id !== id)
    );

    setOpenDeleteModal(false);

    try {

      const updated = await removeFromCart(id);
      setCartItems(updated);

    } catch (error) {

      console.log(error);
      toast.error("Could not remove item");

      const refreshed = await syncCartFromServer();
      setCartItems(refreshed);

    }

  };

  // TOTAL PRICE
  const totalPrice = cartItems.reduce(

    (total, item) =>

      total + (item.price * item.quantity),

    0

  );

  return (

    <>

      <div className="cart-page">

        <h1 className="cart-page-title">
          Shopping Cart
        </h1>

        {loading ? (

          <div className="cart-empty-box">
            <h2>Loading your cart...</h2>
          </div>

        ) : cartItems.length === 0 ? (

          <div className="cart-empty-box">

            <h2>
              Your Cart is Empty
            </h2>

            <button
              className="cart-continue-btn"
              onClick={() => navigate("/customer/category/all")}
            >
              Continue Shopping
            </button>

          </div>

        ) : (

          <div className="cart-layout">

            {/* LEFT SIDE */}
            <div className="cart-items-panel">

              {cartItems.map((item) => (

                <div
                  key={item.id}
                  className="cart-item-row"
                >

                  {/* IMAGE */}
                  <img
                    src={
                      item.imageUrls?.[0] ||
                      "https://via.placeholder.com/200"
                    }
                    alt={item.name}
                    className="cart-item-image"
                  />

                  {/* DETAILS */}
                  <div className="cart-item-details">

                    <h2 className="cart-item-name">
                      {item.name}
                    </h2>

                    <p className="cart-item-category">
                      {item.category}
                    </p>

                    <h3 className="cart-item-price">
                      ₹{item.price}
                    </h3>

                    {/* QUANTITY */}
                    <div className="cart-qty-row">

                      <button
                        onClick={() =>
                          decreaseQuantity(item.id)
                        }
                        className="cart-qty-btn"
                      >
                        -
                      </button>

                      <h3 className="cart-qty-value">
                        {item.quantity}
                      </h3>

                      <button
                        onClick={() =>
                          increaseQuantity(item.id)
                        }
                        className="cart-qty-btn"
                      >
                        +
                      </button>

                    </div>

                    {/* REMOVE BUTTON */}
                    <button
                      onClick={() => {

                        setSelectedProductId(item.id);

                        setOpenDeleteModal(true);

                      }}
                      className="cart-remove-btn"
                    >
                      Remove
                    </button>

                  </div>

                </div>

              ))}

            </div>

            {/* RIGHT SIDE */}
            <div className="cart-summary-panel">

              <h2 className="cart-summary-title">
                Order Summary
              </h2>

              <h3 className="cart-summary-total">
                Total:
                <span className="cart-summary-total-value">
                  ₹{totalPrice}
                </span>
              </h3>

              <button
                className="cart-checkout-btn"
                onClick={() => navigate("/checkout")}
              >
                Proceed to Checkout
              </button>

            </div>

          </div>

        )}

      </div>

      {/* DELETE CONFIRMATION MODAL */}
      <Modal
        show={openDeleteModal}
        onClose={() => setOpenDeleteModal(false)}
      >

        <div className="cart-modal-box">

          <h2 className="cart-modal-title">
            Remove Product
          </h2>

          <p className="cart-modal-text">
            Are you sure you want to remove this product from your cart?
          </p>

          <div className="cart-modal-actions">

            {/* YES REMOVE */}
            <button
              onClick={() =>
                removeProduct(selectedProductId)
              }
              className="cart-modal-confirm-btn"
            >
              Yes Remove
            </button>

            {/* CANCEL */}
            <button
              onClick={() =>
                setOpenDeleteModal(false)
              }
              className="cart-modal-cancel-btn"
            >
              Cancel
            </button>

          </div>

        </div>

      </Modal>

    </>

  );

}

export default CartPage;
