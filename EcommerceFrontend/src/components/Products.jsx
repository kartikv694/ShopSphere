import { useEffect, useState, useContext } from "react";
import "./Products.css";
import { useNavigate } from "react-router-dom";
import { SearchContext } from "./SearchContext";

function Products() {
  const [products, setProducts] = useState([]);
  const { search, category } = useContext(SearchContext);

  const navigate = useNavigate();
  const role = localStorage.getItem("role");

  useEffect(() => {
    fetch("http://localhost:8080/api/products")
      .then(res => res.json())
      .then(data => setProducts(data));
  }, []);

  const filteredProducts = products.filter(product =>
    product.name.toLowerCase().includes(search.toLowerCase()) &&
    (category === "" || product.category === category)
  );

  return (
    <div className="container">
      <h2 className="title">Products</h2>

      {role === "ADMIN" && (
        <button
          className="add-btn"
          onClick={() => navigate("/admin/add-product")}
        >
          + Add Product
        </button>
      )}

      <div className="grid">
        {filteredProducts.map(product => (
          <div
            className="card"
            key={product.id}
            onClick={() => navigate(`/products/${product.id}`)}
          >
            {product.imageUrls?.length > 0 ? (
              <img src={product.imageUrls[0]} alt={product.name} />
            ) : (
              <div>No Image</div>
            )}

            <h3>{product.name}</h3>
            <p className="price">₹{product.price}</p>
          </div>
        ))}
      </div>
    </div>
  );
}

export default Products;