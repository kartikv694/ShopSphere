import { useEffect, useState } from "react";
import {
  useLocation,
  useNavigate,
  useParams
} from "react-router-dom";

import { Modal } from "flowbite-react";

function ProductDetails() {

  const { id } = useParams();

  const [product, setProduct] = useState(null);

  const [quantity, setQuantity] = useState(1);

  const [openModal, setOpenModal] = useState(false);

  const navigate = useNavigate();

  const location = useLocation();

  useEffect(() => {

    fetch(`http://localhost:8080/api/products/${id}`)
      .then((res) => res.json())
      .then((data) => {

        setProduct(data);

        // GET OLD RECENTLY VIEWED PRODUCTS
        let recentlyViewed =
          JSON.parse(
            localStorage.getItem("recentlyViewed")
          ) || [];

        // REMOVE SAME PRODUCT IF EXISTS
        recentlyViewed =
          recentlyViewed.filter(
            (item) => item.id !== data.id
          );

        // ADD CURRENT PRODUCT AT TOP
        recentlyViewed.unshift(data);

        // KEEP ONLY LAST 6 PRODUCTS
        recentlyViewed =
          recentlyViewed.slice(0, 6);

        // SAVE
        localStorage.setItem(
          "recentlyViewed",
          JSON.stringify(recentlyViewed)
        );

      });

  }, [id]);

  // ADD TO CART
  const handleAddToCart = () => {

    let cart =
      JSON.parse(localStorage.getItem("cart")) || [];

    const existingProductIndex = cart.findIndex(
      (item) => item.id === product.id
    );

    // PRODUCT EXISTS
    if (existingProductIndex !== -1) {

      cart[existingProductIndex].quantity += quantity;

    }

    // NEW PRODUCT
    else {

      cart.push({
        ...product,
        quantity: quantity
      });

    }

    localStorage.setItem(
      "cart",
      JSON.stringify(cart)
    );

    // UPDATE CART COUNT
    window.dispatchEvent(
      new Event("cartUpdated")
    );

    // SHOW MODAL
    setOpenModal(true);

  };

  // BACK BUTTON
  const handleBack = () => {

    if (location.state?.category) {

      navigate(
        `/customer/category/${location.state.category}`
      );

    } else {

      navigate("/customer/category/all");

    }

  };

  if (!product) {

    return <h2>Loading...</h2>;

  }

  return (

    <>

      <div style={styles.container}>

        {/* LEFT SIDE */}
        <div>

          {/* BACK BUTTON */}
          <button
            onClick={handleBack}
            style={styles.backButton}
          >
            ← Back
          </button>

          {/* IMAGE */}
          <img
            src={
              product.imageUrls?.[0] ||
              "https://via.placeholder.com/300"
            }
            alt={product.name}
            style={styles.image}
          />

        </div>

        {/* DETAILS */}
        <div style={styles.details}>

          <h1>{product.name}</h1>

          <p style={styles.category}>
            {product.category}
          </p>

          <p style={styles.description}>
            {product.description}
          </p>

          <h2 style={styles.price}>
            ₹{product.price}
          </h2>

          {/* QUANTITY */}
          <div
            style={{
              display: "flex",
              alignItems: "center",
              gap: "15px",
              marginTop: "25px"
            }}
          >

            <button
              onClick={() =>
                quantity > 1 &&
                setQuantity(quantity - 1)
              }
              style={styles.qtyBtn}
            >
              -
            </button>

            <h3>{quantity}</h3>

            <button
              onClick={() =>
                setQuantity(quantity + 1)
              }
              style={styles.qtyBtn}
            >
              +
            </button>

          </div>

          {/* ADD TO CART */}
          <button
            style={styles.button}
            onClick={handleAddToCart}
          >
            Add to Cart
          </button>

        </div>

      </div>

      {/* FLOWBITE MODAL */}
      <Modal
        show={openModal}
        onClose={() => setOpenModal(false)}
      >

        <div
          style={{
            background: "white",
            padding: "30px",
            borderRadius: "14px",
            width: "450px",
            maxWidth: "90vw"
          }}
        >

          <h2
            style={{
              fontSize: "28px",
              fontWeight: "700",
              marginBottom: "20px"
            }}
          >
            Product Added 🛒
          </h2>

          <p
            style={{
              fontSize: "17px",
              marginBottom: "30px",
              color: "#4B5563"
            }}
          >
            Product added to cart successfully.
          </p>

          <div
            style={{
              display: "flex",
              gap: "15px"
            }}
          >

            <button
              onClick={() => setOpenModal(false)}
              style={{
                padding: "12px 24px",
                background: "#2563EB",
                color: "white",
                border: "none",
                borderRadius: "8px",
                cursor: "pointer",
                fontWeight: "600"
              }}
            >
              Continue Shopping
            </button>

            <button
              onClick={() =>
                navigate("/customer/cart")
              }
              style={{
                padding: "12px 24px",
                background: "green",
                color: "white",
                border: "none",
                borderRadius: "8px",
                cursor: "pointer",
                fontWeight: "600"
              }}
            >
              Go To Cart
            </button>

          </div>

        </div>

      </Modal>

    </>

  );

}

const styles = {

  container: {
    display: "flex",
    gap: "50px",
    padding: "40px",
    paddingTop: "90px",
    background: "#fff",
    minHeight: "100vh"
  },

  backButton: {
    marginBottom: "20px",
    padding: "10px 20px",
    background: "#232F3E",
    color: "white",
    border: "none",
    borderRadius: "6px",
    cursor: "pointer",
    fontSize: "15px",
    fontWeight: "500"
  },

  image: {
    width: "400px",
    height: "400px",
    objectFit: "contain"
  },

  details: {
    flex: 1,
    marginTop: "45px"
  },

  category: {
    color: "#565959",
    marginTop: "10px",
    textTransform: "capitalize"
  },

  description: {
    marginTop: "20px",
    fontSize: "16px",
    lineHeight: "28px"
  },

  price: {
    marginTop: "25px",
    color: "#B12704",
    fontSize: "32px"
  },

  button: {
    marginTop: "30px",
    padding: "12px 30px",
    background: "#FFD814",
    border: "none",
    color: "black",
    fontSize: "16px",
    cursor: "pointer",
    borderRadius: "25px",
    fontWeight: "600"
  },

  qtyBtn: {
    width: "35px",
    height: "35px",
    borderRadius: "50%",
    border: "1px solid #ccc",
    background: "#f0f2f2",
    cursor: "pointer",
    fontSize: "18px",
    fontWeight: "bold"
  }

};

export default ProductDetails;