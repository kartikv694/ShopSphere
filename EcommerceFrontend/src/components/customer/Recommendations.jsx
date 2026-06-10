import { useEffect, useState } from "react";
import axios from "axios";
import { toast } from "react-toastify";
import { Link } from "react-router-dom";
import "./Customer.css";
import { API_BASE_URL } from "../../utils/auth";

function Recommendations() {

  const [products, setProducts] = useState([]);

  useEffect(() => {

    axios
      .get(`${API_BASE_URL}/api/products/all`)
      .then((response) => {

        if (Array.isArray(response.data)) {

          // RANDOM PRODUCTS
          const shuffled =
            response.data.sort(
              () => 0.5 - Math.random()
            );

          setProducts(shuffled.slice(0, 6));

        } else if (
          Array.isArray(response.data.data)
        ) {

          const shuffled =
            response.data.data.sort(
              () => 0.5 - Math.random()
            );

          setProducts(shuffled.slice(0, 6));

        }

      })
      .catch((error) => {

        console.log(error);

      });

  }, []);

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

  return (

    <div className="products-section">

      <h2 className="section-title">
        Recommended Products
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

export default Recommendations;
