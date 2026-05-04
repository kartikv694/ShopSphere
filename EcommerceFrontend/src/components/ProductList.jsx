import { useEffect, useState, useContext } from "react";
import axios from "axios";
import "./ProductList.css";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { SearchContext } from "./SearchContext"; // ✅ IMPORTANT

function ProductList() {
  const [products, setProducts] = useState([]);
  const [openModal, setOpenModal] = useState(false);
  const [selectedId, setSelectedId] = useState(null);
  const [loadingDelete, setLoadingDelete] = useState(false);

  const navigate = useNavigate();

  // ✅ GET search + category from context
  const { search, category } = useContext(SearchContext);

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
        `http://localhost:8080/api/products/${selectedId}`
      );

      toast.success("Product deleted successfully 🗑️");

      setOpenModal(false);
      setSelectedId(null);
      fetchProducts();
    } catch (err) {
      console.error(err);
      toast.error("Delete failed ❌");
    } finally {
      setLoadingDelete(false);
    }
  };

  // ✅ FILTER LOGIC (MAIN PART)
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
        <span>Update</span>
        <span>Action</span>
      </div>

      {/* ✅ USE FILTERED PRODUCTS */}
      {filteredProducts.length > 0 ? (
        filteredProducts.map((product) => (
          <div className="table-row" key={product.id}>
            {/* IMAGE */}
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

            <span
              className="update"
              onClick={() => navigate(`/admin/edit/${product.id}`)}
            >
              Update
            </span>

            {/* DELETE */}
            <span
              className="delete"
              onClick={() => confirmDelete(product.id)}
            >
              ✖
            </span>
          </div>
        ))
      ) : (
        <p style={{ textAlign: "center", marginTop: "20px" }}>
          No products found
        </p>
      )}

      {/* MODAL */}
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