import { useEffect, useMemo, useState } from "react";
import axios from "axios";
import { Link } from "react-router-dom";
import { toast } from "react-toastify";
import { API_BASE_URL } from "../../utils/auth";
import { addToCart as addToCartApi } from "../../utils/cartApi";
import "./Customer.css";

const formatPrice = (value) =>
  new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 0
  }).format(Number(value || 0));

function ProductCollectionPage({ type }) {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);

  const isNewReleases = type === "new-releases";

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const response = await axios.get(
          `${API_BASE_URL}/api/products/all`
        );

        const productList = Array.isArray(response.data)
          ? response.data
          : response.data?.data || [];

        setProducts(productList);
      } catch (error) {
        console.log(error);
        toast.error("Unable to load products");
      } finally {
        setLoading(false);
      }
    };

    fetchProducts();
  }, []);

  const visibleProducts = useMemo(() => {
    const sortedProducts = [...products];

    if (isNewReleases) {
      return sortedProducts
        .sort((first, second) => {
          const firstDate = new Date(
            first.createdAt || first.updatedAt || 0
          ).getTime();
          const secondDate = new Date(
            second.createdAt || second.updatedAt || 0
          ).getTime();

          if (firstDate || secondDate) {
            return secondDate - firstDate;
          }

          return Number(second.id || 0) - Number(first.id || 0);
        })
        .slice(0, 12);
    }

    return sortedProducts
      .sort((first, second) => {
        const firstScore =
          Number(first.salesCount || first.orderCount || first.rating || 0) ||
          Number(first.stock || 0);
        const secondScore =
          Number(second.salesCount || second.orderCount || second.rating || 0) ||
          Number(second.stock || 0);

        if (firstScore !== secondScore) {
          return secondScore - firstScore;
        }

        return Number(second.id || 0) - Number(first.id || 0);
      })
      .slice(0, 12);
  }, [isNewReleases, products]);

  const handleAddToCart = async (product) => {
    try {
      await addToCartApi(product, 1);
      toast.success("Product added to cart");
    } catch (error) {
      console.log(error);
      toast.error("Please login to add items to your cart");
    }
  };

  return (
    <div className="collection-page">
      <div className="collection-header">
        <div>
          <h1>
            {isNewReleases ? "New Releases" : "Bestsellers"}
          </h1>
          <p>
            {isNewReleases
              ? "Fresh products recently added to ShopSphere."
              : "Popular picks customers keep coming back for."}
          </p>
        </div>
      </div>

      {loading ? (
        <div className="orders-message">Loading products...</div>
      ) : visibleProducts.length === 0 ? (
        <div className="orders-message">No products found</div>
      ) : (
        <div className="products-grid">
          {visibleProducts.map((product) => (
            <div
              className="product-card"
              key={product.id}
            >
              <Link
                to={`/customer/products/${product.id}`}
                style={{ textDecoration: "none" }}
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
                  style={{ textDecoration: "none" }}
                >
                  <h3 className="product-name">{product.name}</h3>
                </Link>

                <p className="collection-category">
                  {product.category || "Product"}
                </p>

                <p className="product-price">
                  {formatPrice(product.price)}
                </p>

                <button
                  className="product-btn"
                  onClick={() => handleAddToCart(product)}
                >
                  Add To Cart
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default ProductCollectionPage;
