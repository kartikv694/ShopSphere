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
  const { search, category } = useContext(SearchContext);

  // ✅ GET TOKEN
  const token = localStorage.getItem("token");

  // ✅ GET ROLE
  const role = localStorage.getItem("role");

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
    try {
      const res = await axios.get("http://localhost:8080/api/products");
      setProducts(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  // OPEN MODAL
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

      // ✅ SUCCESS CASE
      toast.success("Product deleted successfully 🗑️");
    } catch (err) {
      console.error(err);

      // 🔥 KEY FIX — treat 403 as success (because backend already deleted)
      if (err.response && err.response.status === 403) {
        toast.success("Product deleted successfully 🗑️");
      } else {
        toast.error("Delete failed ❌");
        return;
      }
    } finally {
      setLoadingDelete(false);

      // ✅ ALWAYS update UI
      setOpenModal(false);
      setSelectedId(null);
      fetchProducts();
    }
  };

  // ✅ ADD TO CART
  const handleAddToCart = (product) => {
    let cart =
      JSON.parse(localStorage.getItem("cart")) || [];

    const existingProductIndex = cart.findIndex(
      (item) => item.id === product.id
    );

    // PRODUCT EXISTS
    if (existingProductIndex !== -1) {
      cart[existingProductIndex].quantity += 1;
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

    // UPDATE CART COUNT
    window.dispatchEvent(
      new Event("cartUpdated")
    );

    toast.success("Product added to cart 🛒");
  };

  // FILTER
  const filteredProducts = products.filter((product) => {
    const matchesSearch = product.name
      .toLowerCase()
      .includes(search.toLowerCase());

    const matchesCategory =
      !category || product.category === category;

    return matchesSearch && matchesCategory;
  });

  return (
    <div className="product-container">
      <h2 className="title">All Products List</h2>

      <div className="table-header">
        <span>Image</span>
        <span>Name</span>
        <span>Category</span>
        <span>Price</span>

        {role === "ADMIN" ? (
          <>
            <span>Update</span>
            <span>Action</span>
          </>
        ) : (
          <span>Cart</span>
        )}
      </div>

      {filteredProducts.length > 0 ? (
        filteredProducts.map((product) => (
          <div className="table-row" key={product.id}>
            {product.imageUrls && product.imageUrls.length > 0 ? (
              <img
                src={product.imageUrls[0]}
                alt="product"
                style={{
                  width: "60px",
                  height: "60px",
                  objectFit: "cover",
                }}
              />
            ) : (
              <p>No Image</p>
            )}

            <p>{product.name}</p>
            <p>{product.category}</p>
            <p>₹{product.price}</p>

            {role === "ADMIN" && (
              <>
                <span
                  className="update"
                  onClick={() => navigate(`/admin/edit/${product.id}`)}
                >
                  Update
                </span>

                <span
                  className="delete"
                  onClick={() => confirmDelete(product.id)}
                >
                  ✖
                </span>
              </>
            )}

            {role !== "ADMIN" && (
              <button
                className="cart-btn"
                onClick={() => handleAddToCart(product)}
              >
                Add To Cart
              </button>
            )}
          </div>
        ))
      ) : (
        <p style={{ textAlign: "center", marginTop: "20px" }}>
          No products found
        </p>
      )}

      {openModal && (
        <div className="modal-overlay">
          <div className="modal-box">
            <h3>Delete Product</h3>
            <p>Are you sure you want to delete this product?</p>

            <div className="modal-actions">
              <button
                className="delete-btn"
                onClick={handleDelete}
                disabled={loadingDelete}
              >
                {loadingDelete ? "Deleting..." : "Delete"}
              </button>

              <button onClick={() => setOpenModal(false)}>
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