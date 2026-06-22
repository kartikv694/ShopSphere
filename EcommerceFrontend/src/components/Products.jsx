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
import { addToCart } from "../utils/cartApi";

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
  const handleAddToCart = async (product) => {

    if (!localStorage.getItem("token")) {

      toast.info("Please login to add items to your cart");
      navigate("/login");
      return;

    }

    try {

      await addToCart(product, 1);

      toast.success(
        "Product added to cart 🛒"
      );

    } catch (error) {

      console.log(error);

      toast.error(
        "Could not add item to cart"
      );

    }

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

              <div className="image-wrapper">

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
                    className="slider-btn left-btn"
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
                    className="slider-btn right-btn"
                  >
                    <FaChevronRight />
                  </button>

                )}

              </div>

              {/* PRODUCT INFO */}

              <h3>{product.name}</h3>

              <p className="product-category">
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
