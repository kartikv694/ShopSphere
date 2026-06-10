import { useEffect, useState, useContext } from "react";
import "./Products.css";
import { useNavigate } from "react-router-dom";
import { SearchContext } from "./SearchContextValue";
import { toast } from "react-toastify";
import {
  FaChevronLeft,
  FaChevronRight
} from "react-icons/fa";
import { API_BASE_URL } from "../utils/auth";

function Products() {

  const [products, setProducts] = useState([]);

  const [imageIndexes, setImageIndexes] =
    useState({});

  const { search, category } =
    useContext(SearchContext);

  const navigate = useNavigate();

  const role = localStorage.getItem("role");

  useEffect(() => {

    fetch(`${API_BASE_URL}/api/products/all`)
      .then((res) => res.json())
      .then((data) =>
        setProducts(
          Array.isArray(data)
            ? data
            : data?.data || []
        )
      );

  }, []);

  const filteredProducts = products.filter((product) => {
    const searchTerm = search.toLowerCase();
    const productName = product.name?.toLowerCase() || "";
    const productCategory = product.category?.toLowerCase() || "";
    const productDescription = product.description?.toLowerCase() || "";

    const matchesSearch =
      !searchTerm ||
      productName.includes(searchTerm) ||
      productCategory.includes(searchTerm) ||
      productDescription.includes(searchTerm);

    const matchesCategory =
      !category ||
      productCategory === category.toLowerCase();

    return matchesSearch && matchesCategory;
  });

  // NEXT IMAGE
  const nextImage = (
    productId,
    totalImages,
    e
  ) => {

    e.preventDefault();
    e.stopPropagation();

    setImageIndexes((prev) => ({

      ...prev,

      [productId]:
        (
          (prev[productId] || 0) + 1
        ) % totalImages

    }));

  };

  // PREVIOUS IMAGE
  const prevImage = (
    productId,
    totalImages,
    e
  ) => {

    e.preventDefault();
    e.stopPropagation();

    setImageIndexes((prev) => ({

      ...prev,

      [productId]:
        (
          (prev[productId] || 0) - 1 +
          totalImages
        ) % totalImages

    }));

  };

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

    <div className="container">

      <h2 className="title">
        {search ? `Search results for "${search}"` : "Products"}
      </h2>

      {role === "ADMIN" && (

        <button
          className="add-btn"
          onClick={() =>
            navigate("/admin/add-product")
          }
        >
          + Add Product
        </button>

      )}

      {filteredProducts.length === 0 ? (
        <div className="no-products-found">
          <span>No products found</span>
          <p>Try another search term or browse all categories.</p>
        </div>
      ) : (
        <div className="grid">

        {filteredProducts.map((product) => {

          const currentIndex =
            imageIndexes[product.id] || 0;

          return (

            <div
              className="card"
              key={product.id}
              onClick={() =>
                navigate(`/customer/products/${product.id}`)
              }
            >

              {/* IMAGE SECTION */}

              <div
                style={{
                  position: "relative",
                  width: "100%",
                  height: "260px",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  background: "white"
                }}
              >

                {/* LEFT ARROW */}

                {product.imageUrls?.length > 1 && (

                  <button
                    onClick={(e) =>
                      prevImage(
                        product.id,
                        product.imageUrls.length,
                        e
                      )
                    }
                    style={{
                      position: "absolute",
                      left: "8px",
                      top: "45%",
                      transform: "translateY(-45%)",
                      width: "34px",
                      height: "34px",
                      minWidth: "34px",
                      borderRadius: "50%",
                      border: "none",
                      background: "white",
                      boxShadow:
                        "0 2px 8px rgba(0,0,0,0.2)",
                      cursor: "pointer",
                      zIndex: 2,
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center"
                    }}
                  >
                    <FaChevronLeft />
                  </button>

                )}

                {/* PRODUCT IMAGE */}

                <img
                  src={
                    product.imageUrls?.[
                      currentIndex
                    ]
                  }
                  alt={product.name}
                  style={{
                    width: "100%",
                    height: "260px",
                    objectFit: "cover"
                  }}
                />

                {/* RIGHT ARROW */}

                {product.imageUrls?.length > 1 && (

                  <button
                    onClick={(e) =>
                      nextImage(
                        product.id,
                        product.imageUrls.length,
                        e
                      )
                    }
                    style={{
                      position: "absolute",
                      right: "8px",
                      top: "45%",
                      transform: "translateY(-45%)",
                      width: "34px",
                      height: "34px",
                      minWidth: "34px",
                      borderRadius: "50%",
                      border: "none",
                      background: "white",
                      boxShadow:
                        "0 2px 8px rgba(0,0,0,0.2)",
                      cursor: "pointer",
                      zIndex: 2,
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center"
                    }}
                  >
                    <FaChevronRight />
                  </button>

                )}

              </div>

              {/* PRODUCT INFO */}

              <h3>{product.name}</h3>

              <p
                style={{
                  color: "#666",
                  padding: "0 20px",
                  marginBottom: "14px",
                  textTransform: "capitalize"
                }}
              >
                {product.category}
              </p>

              <p className="price">
                ₹{product.price}
              </p>

              {role !== "ADMIN" && (

                <button
                  className="cart-btn"
                  onClick={(e) => {

                    e.stopPropagation();

                    handleAddToCart(product);

                  }}
                >
                  Add To Cart
                </button>

              )}

            </div>

          );

        })}

        </div>
      )}

    </div>

  );

}

export default Products;
