import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { toast } from "react-toastify";
import "./Customer.css";
import { addToCart as addToCartApi } from "../../utils/cartApi";
import {
  syncRecentlyViewedFromServer,
  getCachedRecentlyViewed
} from "../../utils/userPreferencesApi";

function RecentlyViewed() {

  const [products, setProducts] = useState(
    () => getCachedRecentlyViewed()
  );

  // PULL THE AUTHORITATIVE LIST FROM THE SERVER ON MOUNT — this is what
  // surfaces products viewed in a different browser/device for this
  // account, and keeps re-rendering in sync if it changes elsewhere on
  // this page (e.g. right after viewing a product just now).
  useEffect(() => {

    let isMounted = true;

    syncRecentlyViewedFromServer().then((items) => {
      if (isMounted) setProducts(items);
    });

    const onUpdated = () => setProducts(getCachedRecentlyViewed());

    window.addEventListener("recentlyViewedUpdated", onUpdated);

    return () => {
      isMounted = false;
      window.removeEventListener("recentlyViewedUpdated", onUpdated);
    };

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
