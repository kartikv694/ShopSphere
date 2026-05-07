import { useEffect, useState } from "react";
import { Modal } from "flowbite-react";

function CartPage() {

  const [cartItems, setCartItems] = useState([]);

  const [openDeleteModal, setOpenDeleteModal] =
    useState(false);

  const [selectedProductId, setSelectedProductId] =
    useState(null);

  // LOAD CART
  useEffect(() => {

    const cart =
      JSON.parse(localStorage.getItem("cart")) || [];

    setCartItems(cart);

  }, []);

  // UPDATE CART
  const updateCart = (updatedCart) => {

    setCartItems(updatedCart);

    localStorage.setItem(
      "cart",
      JSON.stringify(updatedCart)
    );

    window.dispatchEvent(
      new Event("cartUpdated")
    );

  };

  // INCREASE QUANTITY
  const increaseQuantity = (id) => {

    const updatedCart = cartItems.map((item) => {

      if (item.id === id) {

        return {
          ...item,
          quantity: item.quantity + 1
        };

      }

      return item;

    });

    updateCart(updatedCart);

  };

  // DECREASE QUANTITY
  const decreaseQuantity = (id) => {

    const product = cartItems.find(
      (item) => item.id === id
    );

    // IF ONLY 1 QUANTITY LEFT
    if (product.quantity === 1) {

      setSelectedProductId(id);

      setOpenDeleteModal(true);

      return;

    }

    const updatedCart = cartItems.map((item) => {

      if (item.id === id) {

        return {
          ...item,
          quantity: item.quantity - 1
        };

      }

      return item;

    });

    updateCart(updatedCart);

  };

  // REMOVE PRODUCT
  const removeProduct = (id) => {

    const updatedCart = cartItems.filter(
      (item) => item.id !== id
    );

    updateCart(updatedCart);

    setOpenDeleteModal(false);

  };

  // TOTAL PRICE
  const totalPrice = cartItems.reduce(

    (total, item) =>

      total + (item.price * item.quantity),

    0

  );

  return (

    <>

      <div
        style={{
          background: "#EAEDED",
          minHeight: "100vh",
          padding: "30px"
        }}
      >

        <h1
          style={{
            marginBottom: "30px",
            fontSize: "32px"
          }}
        >
          Shopping Cart
        </h1>

        {cartItems.length === 0 ? (

          <div
            style={{
              background: "white",
              padding: "30px",
              borderRadius: "10px"
            }}
          >

            <h2>
              Your Cart is Empty
            </h2>

          </div>

        ) : (

          <div
            style={{
              display: "flex",
              gap: "30px"
            }}
          >

            {/* LEFT SIDE */}
            <div
              style={{
                flex: 3,
                background: "white",
                padding: "20px",
                borderRadius: "10px"
              }}
            >

              {cartItems.map((item) => (

                <div
                  key={item.id}
                  style={{
                    display: "flex",
                    gap: "30px",
                    padding: "20px 0",
                    borderBottom: "1px solid #ddd"
                  }}
                >

                  {/* IMAGE */}
                  <img
                    src={
                      item.imageUrls?.[0] ||
                      "https://via.placeholder.com/200"
                    }
                    alt={item.name}
                    style={{
                      width: "180px",
                      height: "180px",
                      objectFit: "contain"
                    }}
                  />

                  {/* DETAILS */}
                  <div
                    style={{
                      flex: 1
                    }}
                  >

                    <h2
                      style={{
                        fontSize: "22px",
                        marginBottom: "10px"
                      }}
                    >
                      {item.name}
                    </h2>

                    <p
                      style={{
                        color: "#565959",
                        marginBottom: "10px"
                      }}
                    >
                      {item.category}
                    </p>

                    <h3
                      style={{
                        marginBottom: "20px",
                        color: "#B12704"
                      }}
                    >
                      ₹{item.price}
                    </h3>

                    {/* QUANTITY */}
                    <div
                      style={{
                        display: "flex",
                        alignItems: "center",
                        gap: "15px"
                      }}
                    >

                      <button
                        onClick={() =>
                          decreaseQuantity(item.id)
                        }
                        style={styles.qtyBtn}
                      >
                        -
                      </button>

                      <h3>
                        {item.quantity}
                      </h3>

                      <button
                        onClick={() =>
                          increaseQuantity(item.id)
                        }
                        style={styles.qtyBtn}
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
                      style={styles.removeBtn}
                    >
                      Remove
                    </button>

                  </div>

                </div>

              ))}

            </div>

            {/* RIGHT SIDE */}
            <div
              style={{
                flex: 1,
                background: "white",
                padding: "20px",
                borderRadius: "10px",
                height: "fit-content"
              }}
            >

              <h2
                style={{
                  marginBottom: "20px"
                }}
              >
                Order Summary
              </h2>

              <h3>
                Total:
                <span
                  style={{
                    marginLeft: "10px",
                    color: "#B12704"
                  }}
                >
                  ₹{totalPrice}
                </span>
              </h3>

              <button
                style={styles.checkoutBtn}
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

              <div
                  style={{
                      padding: "35px",
                      backgroundColor: "white",
                      borderRadius: "16px",
                      width: "500px",
                      maxWidth: "90vw"
                  }}
              >

                  <h2
                      style={{
                          fontSize: "30px",
                          fontWeight: "700",
                          marginBottom: "18px",
                          color: "#111827"
                      }}
                  >
                      Remove Product
                  </h2>

                  <p
                      style={{
                          fontSize: "17px",
                          color: "#4b5563",
                          lineHeight: "30px",
                          marginBottom: "30px"
                      }}
                  >
                      Are you sure you want to remove this product from your cart?
                  </p>

                  <div
                      style={{
                          display: "flex",
                          gap: "18px"
                      }}
                  >

                      {/* YES REMOVE */}
                      <button
                          onClick={() =>
                              removeProduct(selectedProductId)
                          }
                          onMouseEnter={(e) =>
                              e.target.style.background = "#b91c1c"
                          }
                          onMouseLeave={(e) =>
                              e.target.style.background = "#dc2626"
                          }
                          style={{
                              padding: "12px 26px",
                              background: "#dc2626",
                              color: "white",
                              border: "none",
                              borderRadius: "8px",
                              cursor: "pointer",
                              fontWeight: "600",
                              fontSize: "16px",
                              transition: "0.3s"
                          }}
                      >
                          Yes Remove
                      </button>

                      {/* CANCEL */}
                      <button
                          onClick={() =>
                              setOpenDeleteModal(false)
                          }
                          onMouseEnter={(e) =>
                              e.target.style.background = "#d1d5db"
                          }
                          onMouseLeave={(e) =>
                              e.target.style.background = "#e5e7eb"
                          }
                          style={{
                              padding: "12px 26px",
                              background: "#e5e7eb",
                              color: "#111827",
                              border: "none",
                              borderRadius: "8px",
                              cursor: "pointer",
                              fontWeight: "600",
                              fontSize: "16px",
                              transition: "0.3s"
                          }}
                      >
                          Cancel
                      </button>

                  </div>

              </div>

          </Modal>

    </>

  );

}

const styles = {

  qtyBtn: {
    width: "35px",
    height: "35px",
    borderRadius: "50%",
    border: "1px solid #ccc",
    background: "#f0f2f2",
    cursor: "pointer",
    fontSize: "18px",
    fontWeight: "bold"
  },

  removeBtn: {
    marginTop: "20px",
    padding: "10px 18px",
    border: "none",
    background: "#f44336",
    color: "white",
    borderRadius: "6px",
    cursor: "pointer"
  },

  checkoutBtn: {
    marginTop: "30px",
    width: "100%",
    padding: "12px",
    border: "none",
    background: "#FFD814",
    borderRadius: "25px",
    fontSize: "16px",
    fontWeight: "600",
    cursor: "pointer"
  }

};

export default CartPage;