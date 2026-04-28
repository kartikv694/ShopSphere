import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

function ProductDetails() {
  const { id } = useParams();
  const [product, setProduct] = useState(null);

  useEffect(() => {
    fetch(`http://localhost:8080/api/products/${id}`)
      .then(res => res.json())
      .then(data => setProduct(data));
  }, [id]);

  const handleAddToCart = () => {
    alert("Product added to cart 🛒");
  };

  if (!product) return <h2>Loading...</h2>;

  return (
    <div style={styles.container}>
      <img src={product.imageUrl} alt={product.name} style={styles.image} />

      <div>
        <h2>{product.name}</h2>
        <p>{product.description}</p>
        <h3 style={{ color: "green" }}>₹{product.price}</h3>

        {/* ✅ ADD BUTTON */}
        <button style={styles.button} onClick={handleAddToCart}>
          Add to Cart
        </button>
      </div>
    </div>
  );
}

const styles = {
  container: {
    display: "flex",
    gap: "30px",
    padding: "30px"
  },
  image: {
    width: "300px",
    objectFit: "contain"
  },
  button: {
    marginTop: "20px",
    padding: "10px 20px",
    background: "#ff9f00",
    border: "none",
    color: "#fff",
    fontSize: "16px",
    cursor: "pointer",
    borderRadius: "5px"
  }
};

export default ProductDetails;