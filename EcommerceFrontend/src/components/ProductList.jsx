import { useEffect, useState } from "react";
import axios from "axios";
import "./ProductList.css";

function ProductList() {
  const [products, setProducts] = useState([]);

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
    try {
      const res = await axios.get("http://localhost:8080/api/products");
      setProducts(res.data);
    } catch (err) {
      console.error("Error fetching products", err);
    }
  };

  const deleteProduct = async (id) => {
    try {
      await axios.delete(`http://localhost:8080/api/products/${id}`);
      fetchProducts();
    } catch (err) {
      console.error("Delete failed", err);
    }
  };

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

      {products.map((product) => (
        <div className="table-row" key={product.id}>

          {/* ✅ FIX HERE */}
          {product.imageUrl ? (
            <img
              src={product.imageUrl}
              alt="product"
              style={{ width: "60px", height: "60px", objectFit: "cover" }}
            />
          ) : (
            <p>No Image</p>
          )}

          <p>{product.name}</p>
          <p>{product.category}</p>
          <p>₹{product.price}</p>

          <span className="update">Update</span>

          <span
            className="delete"
            onClick={() => deleteProduct(product.id)}
          >
            ✖
          </span>
        </div>
      ))}
    </div>
  );
}

export default ProductList;