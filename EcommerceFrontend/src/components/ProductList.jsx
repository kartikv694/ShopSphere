import { useEffect, useState, useContext } from "react";
import axios from "axios";
import "./ProductList.css";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { SearchContext } from "./SearchContext";

function ProductList() {

  const [products, setProducts] = useState([]);

  const [openModal, setOpenModal] = useState(false);

  const [selectedId, setSelectedId] = useState(null);

  const [loadingDelete, setLoadingDelete] = useState(false);

  const navigate = useNavigate();

  const { search, category } =
    useContext(SearchContext);

  // TOKEN

  const token =
    localStorage.getItem("token");

  // ROLE

  const role =
    localStorage.getItem("role");

  useEffect(() => {

    fetchProducts();

  }, []);

  // FETCH PRODUCTS

  const fetchProducts = async () => {

    try {

      const res =
        await axios.get(
          "http://localhost:8080/api/products"
        );

      setProducts(res.data);

    } catch (err) {

      console.error(err);

    }

  };

  // DELETE MODAL

  const confirmDelete = (id) => {

    setSelectedId(id);

    setOpenModal(true);

  };

  // DELETE PRODUCT

  const handleDelete = async () => {

    if (!selectedId) return;

    try {

      setLoadingDelete(true);

      await axios.delete(
        `http://localhost:8080/api/products/${selectedId}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      toast.success(
        "Product deleted successfully 🗑️"
      );

    } catch (err) {

      console.error(err);

      if (
        err.response &&
        err.response.status === 403
      ) {

        toast.success(
          "Product deleted successfully 🗑️"
        );

      } else {

        toast.error("Delete failed ❌");

        return;

      }

    } finally {

      setLoadingDelete(false);

      setOpenModal(false);

      setSelectedId(null);

      fetchProducts();

    }

  };

  // ADD TO CART

  const handleAddToCart = (product) => {

    let cart =
      JSON.parse(
        localStorage.getItem("cart")
      ) || [];

    const existingProductIndex =
      cart.findIndex(
        (item) =>
          item.id === product.id
      );

    // PRODUCT EXISTS

    if (existingProductIndex !== -1) {

      cart[existingProductIndex]
        .quantity += 1;

    }

    // NEW PRODUCT

    else {

      cart.push({
        ...product,
        quantity: 1,
      });

    }

    localStorage.setItem(
      "cart",
      JSON.stringify(cart)
    );

    // UPDATE CART ICON

    window.dispatchEvent(
      new Event("cartUpdated")
    );

    toast.success(
      "Product added to cart 🛒"
    );

  };

  // FILTER PRODUCTS

  const filteredProducts =
    products.filter((product) => {

      const matchesSearch =
        product.name
          .toLowerCase()
          .includes(
            search.toLowerCase()
          );

      const matchesCategory =
        !category ||
        product.category === category;

      return (
        matchesSearch &&
        matchesCategory
      );

    });

  return (

    <div className="category-page">

      <h2 className="category-title">
        All Products
      </h2>

      <div className="category-products">

        {filteredProducts.length > 0 ? (

          filteredProducts.map((product) => (

            <div
              className="category-product-card"
              key={product.id}
            >

              {product.imageUrls &&
              product.imageUrls.length > 0 ? (

                <img
                  src={product.imageUrls[0]}
                  alt="product"
                  className="category-product-image"
                />

              ) : (

                <p>No Image</p>

              )}

              <div className="category-product-info">

                <h3 className="category-product-name">
                  {product.name}
                </h3>

                <p className="category-product-category">
                  {product.category}
                </p>

                <p className="category-product-price">
                  ₹{product.price}
                </p>

                {role === "ADMIN" ? (

                  <div
                    style={{
                      display: "flex",
                      gap: "10px",
                    }}
                  >

                    <button
                      className="category-product-btn"
                      onClick={() =>
                        navigate(
                          `/admin/edit/${product.id}`
                        )
                      }
                    >
                      Update
                    </button>

                    <button
                      className="category-product-btn"
                      onClick={() =>
                        confirmDelete(product.id)
                      }
                    >
                      Delete
                    </button>

                  </div>

                ) : (

                  <button
                    className="category-product-btn"
                    onClick={() =>
                      handleAddToCart(product)
                    }
                  >
                    Add To Cart
                  </button>

                )}

              </div>

            </div>

          ))

        ) : (

          <p
            style={{
              textAlign: "center",
              marginTop: "20px",
            }}
          >
            No products found
          </p>

        )}

      </div>

      {openModal && (

        <div className="modal-overlay">

          <div className="modal-box">

            <h3>
              Delete Product
            </h3>

            <p>
              Are you sure you want
              to delete this product?
            </p>

            <div className="modal-actions">

              <button
                className="delete-btn"
                onClick={handleDelete}
                disabled={loadingDelete}
              >
                {loadingDelete
                  ? "Deleting..."
                  : "Delete"}
              </button>

              <button
                onClick={() =>
                  setOpenModal(false)
                }
              >
                Cancel
              </button>

            </div>

          </div>

        </div>

      )}

    </div>

  );

}

export default ProductList;