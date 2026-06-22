import { useEffect, useState } from "react";
import {
  useLocation,
  useNavigate,
  useParams
} from "react-router-dom";

import { Modal } from "flowbite-react";
import { toast } from "react-toastify";
import { API_BASE_URL } from "../utils/auth";
import { addToCart } from "../utils/cartApi";
import { recordRecentlyViewed } from "../utils/userPreferencesApi";
import "./ProductsDetails.css";

function ProductDetails() {

  const { id } = useParams();

  const [product, setProduct] = useState(null);

  const [quantity, setQuantity] = useState(1);

  const [openModal, setOpenModal] = useState(false);

  const [selectedImage, setSelectedImage] =
    useState(0);

  const navigate = useNavigate();

  const location = useLocation();

  useEffect(() => {

    fetch(`${API_BASE_URL}/api/products/${id}`)
      .then((res) => res.json())
      .then((data) => {

        setProduct(data);

        // RECORD THIS VIEW — syncs to the server so "Recently Viewed"
        // follows the account into any other browser/device, and keeps
        // the local mirror in sync for components reading it directly.
        recordRecentlyViewed(data);

      });

  }, [id]);

  // ADD TO CART
  const handleAddToCart = async () => {

    try {

      await addToCart(product, quantity);

      // SHOW MODAL
      setOpenModal(true);

    } catch (error) {

      console.log(error);

      toast.error(
        "Please login to add items to your cart"
      );

    }

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

    return <h2 className="pd-loading">Loading...</h2>;

  }

  return (

    <>

      <div className="pd-container">

        {/* LEFT SIDE */}
        <div className="pd-left">

          {/* BACK BUTTON */}
          <button
            onClick={handleBack}
            className="pd-back-btn"
          >
            ← Back
          </button>

          {/* IMAGE SECTION */}
          <div className="pd-image-section">

            {/* THUMBNAILS */}
            <div className="pd-thumbnails">

              {product.imageUrls?.map(
                (image, index) => (

                  <img
                    key={index}
                    src={image}
                    alt=""
                    onClick={() =>
                      setSelectedImage(index)
                    }
                    className={
                      "pd-thumbnail" +
                      (selectedImage === index
                        ? " pd-thumbnail-active"
                        : "")
                    }
                  />

                )
              )}

            </div>

            {/* MAIN IMAGE */}
            <img
              src={
                product.imageUrls?.[
                  selectedImage
                ] ||
                "https://via.placeholder.com/300"
              }
              alt={product.name}
              className="pd-main-image"
            />

          </div>

        </div>

        {/* DETAILS */}
        <div className="pd-details">

          <h1 className="pd-name">{product.name}</h1>

          <p className="pd-category">
            {product.category}
          </p>

          <p className="pd-description">
            {product.description}
          </p>

          <h2 className="pd-price">
            ₹{product.price}
          </h2>

          {/* QUANTITY */}
          <div className="pd-qty-row">

            <button
              onClick={() =>
                quantity > 1 &&
                setQuantity(quantity - 1)
              }
              className="pd-qty-btn"
            >
              -
            </button>

            <h3 className="pd-qty-value">{quantity}</h3>

            <button
              onClick={() =>
                setQuantity(quantity + 1)
              }
              className="pd-qty-btn"
            >
              +
            </button>

          </div>

          {/* ADD TO CART */}
          <button
            className="pd-add-to-cart-btn"
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

        <div className="pd-modal-box">

          <h2 className="pd-modal-title">
            Product Added 🛒
          </h2>

          <p className="pd-modal-text">
            Product added to cart successfully.
          </p>

          <div className="pd-modal-actions">

            <button
              onClick={() => setOpenModal(false)}
              className="pd-modal-continue-btn"
            >
              Continue Shopping
            </button>

            <button
              onClick={() =>
                navigate("/customer/cart")
              }
              className="pd-modal-cart-btn"
            >
              Go To Cart
            </button>

          </div>

        </div>

      </Modal>

    </>

  );

}

export default ProductDetails;
