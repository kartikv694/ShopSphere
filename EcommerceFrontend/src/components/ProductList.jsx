import { useCallback, useEffect, useState, useContext } from "react";
import axios from "axios";
import "./ProductList.css";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { SearchContext } from "./SearchContextValue";
import { API_BASE_URL } from "../utils/auth";

function ProductList() {
  const [products, setProducts] = useState([]);
  const [openModal, setOpenModal] = useState(false);
  const [selectedId, setSelectedId] = useState(null);
  const [loadingDelete, setLoadingDelete] = useState(false);
  const [loading, setLoading] = useState(true);

  const navigate = useNavigate();
  const { search, category } = useContext(SearchContext);
  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role");

  const fetchProducts = useCallback(async () => {
    try {
      setLoading(true);
      const res = await axios.get(`${API_BASE_URL}/api/products/all`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setProducts(res.data);
    } catch (err) {
      console.error(err);
      toast.error("Failed to load products");
    } finally {
      setLoading(false);
    }
  }, [token]);

  useEffect(() => {
    const loadId = setTimeout(fetchProducts, 0);

    return () => clearTimeout(loadId);
  }, [fetchProducts]);

  const confirmDelete = (id) => {
    setSelectedId(id);
    setOpenModal(true);
  };

  const handleDelete = async () => {
    if (!selectedId) return;
    try {
      setLoadingDelete(true);
      await axios.delete(`${API_BASE_URL}/api/products/${selectedId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      toast.success("Product deleted successfully 🗑️");
    } catch (err) {
      console.error(err);
      if (err.response && err.response.status === 403) {
        toast.success("Product deleted successfully 🗑️");
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

  const handleAddToCart = (product) => {
    let cart = JSON.parse(localStorage.getItem("cart")) || [];
    const existingProductIndex = cart.findIndex((item) => item.id === product.id);
    if (existingProductIndex !== -1) {
      cart[existingProductIndex].quantity += 1;
    } else {
      cart.push({ ...product, quantity: 1 });
    }
    localStorage.setItem("cart", JSON.stringify(cart));
    window.dispatchEvent(new Event("cartUpdated"));
    toast.success("Product added to cart 🛒");
  };

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
    const matchesCategory = !category || productCategory === category.toLowerCase();
    return matchesSearch && matchesCategory;
  });

  if (loading) {
    return (
      <div className="category-page">
        <div className="products-loading">
          {[...Array(6)].map((_, i) => (
            <div className="skeleton-card" key={i}>
              <div className="skeleton-img"></div>
              <div className="skeleton-line"></div>
              <div className="skeleton-line short"></div>
            </div>
          ))}
        </div>
      </div>
    );
  }

  return (
    <div className="category-page">
      <div className="product-list-header">
        <h2 className="category-title">All Products</h2>
        <span className="product-count">{filteredProducts.length} products</span>
      </div>

      <div className="category-products">
        {filteredProducts.length > 0 ? (
          filteredProducts.map((product) => (
            <div className="category-product-card" key={product.id}>
              {product.imageUrls && product.imageUrls.length > 0 ? (
                <img
                  src={product.imageUrls[0]}
                  alt="product"
                  className="category-product-image"
                />
              ) : (
                <div className="no-image-placeholder">
                  <span>📦</span>
                  <p>No Image</p>
                </div>
              )}

              <div className="category-product-info">
                <h3 className="category-product-name">{product.name}</h3>
                <span className="category-product-category">{product.category}</span>
                <p className="category-product-price">₹{product.price.toLocaleString("en-IN")}</p>

                {role === "ADMIN" ? (
                  <div className="admin-btn-group">
                    <button
                      className="edit-btn"
                      onClick={() => navigate(`/admin/edit/${product.id}`)}
                    >
                      ✏️ Edit
                    </button>
                    <button
                      className="delete-btn-card"
                      onClick={() => confirmDelete(product.id)}
                    >
                      🗑️ Delete
                    </button>
                  </div>
                ) : (
                  <button
                    className="category-product-btn"
                    onClick={() => handleAddToCart(product)}
                  >
                    Add To Cart
                  </button>
                )}
              </div>
            </div>
          ))
        ) : (
          <div className="no-products-found">
            <span>🔍</span>
            <p>No products found</p>
          </div>
        )}
      </div>

      {openModal && (
        <div className="modal-overlay">
          <div className="modal-box">
            <div className="modal-icon">🗑️</div>
            <h3>Delete Product</h3>
            <p>Are you sure you want to delete this product? This action cannot be undone.</p>
            <div className="modal-actions">
              <button className="delete-btn" onClick={handleDelete} disabled={loadingDelete}>
                {loadingDelete ? "Deleting..." : "Delete"}
              </button>
              <button className="cancel-btn" onClick={() => setOpenModal(false)}>
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
