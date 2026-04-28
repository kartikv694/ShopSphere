import { useEffect, useState } from "react";
import "./Products.css";
import { useNavigate } from "react-router-dom";

function Products() {
  const [products, setProducts] = useState([]);
  const [search, setSearch] = useState("");

  const navigate = useNavigate();

  const role = localStorage.getItem("role");

  useEffect(() => {
    fetch("http://localhost:8080/api/products")
      .then(res => res.json())
      .then(data => setProducts(data));
  }, []);

  const filteredProducts = products.filter(product =>
    product.name.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <>
      <div className="container">
        <h2 className="title">Products</h2>

        {/* ADMIN BUTTON */}
        {role === "ADMIN" && (
          <button
            className="add-btn"
            onClick={() => navigate("/add-product")}
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
              style={{ cursor: "pointer" }}
            >
              <img src={product.imageUrl} alt={product.name} />
              <h3>{product.name}</h3>
              <p className="price">₹{product.price}</p>
            </div>
          ))}
        </div>
      </div>
    </>
  );
}

export default Products;