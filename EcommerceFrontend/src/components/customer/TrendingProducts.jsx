import { useEffect, useState } from "react";
import axios from "axios";
import { toast } from "react-toastify";
import { Link } from "react-router-dom";
import "./Customer.css";
import { API_BASE_URL } from "../../utils/auth";
import { addToCart as addToCartApi } from "../../utils/cartApi";

function TrendingProducts() {

  const [products, setProducts] = useState([]);

  // FETCH PRODUCTS
  useEffect(() => {

    axios
      .get(`${API_BASE_URL}/api/products/all`)
      .then((response) => {

        if (Array.isArray(response.data)) {

          setProducts(response.data.slice(0, 6));

        } else if (
          Array.isArray(response.data.data)
        ) {

          setProducts(
            response.data.data.slice(0, 6)
          );

        }

      })
      .catch((error) => {

        console.log(error);

      });

  }, []);

  // ADD TO CART
  const handleAddToCart = async (product) => {

    try {

      await addToCartApi(product, 1);

      toast.success(
        "Product added to cart 🛒"
      );

    } catch (error) {

      console.log(error);

      toast.error(
        "Please login to add items to your cart"
      );

    }

  };

  return (

    <div className="products-section">

      <h2 className="section-title">
        Trending Products
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

export default TrendingProducts;
