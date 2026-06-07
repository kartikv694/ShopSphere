import { useState } from "react";
import { Link } from "react-router-dom";
import { toast } from "react-toastify";
import "./Customer.css";

function RecentlyViewed() {

  const [products] = useState(
    () => JSON.parse(localStorage.getItem("recentlyViewed")) || []
  );

  // ADD TO CART
  const handleAddToCart = (product) => {

    let cart =
      JSON.parse(localStorage.getItem("cart")) || [];

    const existingProductIndex =
      cart.findIndex(
        (item) => item.id === product.id
      );

    if (existingProductIndex !== -1) {

      cart[existingProductIndex].quantity += 1;

    } else {

      cart.push({
        ...product,
        quantity: 1
      });

    }

    localStorage.setItem(
      "cart",
      JSON.stringify(cart)
    );

    window.dispatchEvent(
      new Event("cartUpdated")
    );

    toast.success(
      "Product added to cart 🛒"
    );

  };

  if (products.length === 0) {

    return null;

  }

  return (

    <div className="products-section">

      <h2 className="section-title">
        Recently Viewed
      </h2>

      <div className="products-grid">

        {products.map((product) => (

          <div
            className="product-card"
            key={product.id}
          >

            <Link
              to={`/customer/products/${product.id}`}
              style={{
                textDecoration: "none"
              }}
            >

              <img
                src={
                  product.imageUrls?.[0] ||
                  "https://via.placeholder.com/300"
                }
                alt={product.name}
                className="product-image"
              />

            </Link>

            <div className="product-info">

              <Link
                to={`/customer/products/${product.id}`}
                style={{
                  textDecoration: "none"
                }}
              >

                <h3 className="product-name">
                  {product.name}
                </h3>

              </Link>

              <p className="product-price">
                ₹{product.price}
              </p>

              <button
                className="product-btn"
                onClick={() =>
                  handleAddToCart(product)
                }
              >
                Add To Cart
              </button>

            </div>

          </div>

        ))}

      </div>

    </div>

  );

}

export default RecentlyViewed;
