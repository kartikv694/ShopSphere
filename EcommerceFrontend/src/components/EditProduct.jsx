import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import { toast } from "react-toastify";

function EditProduct() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [images, setImages] = useState([null, null, null, null]);
  const [oldImages, setOldImages] = useState([]);

  const [product, setProduct] = useState({
    name: "",
    description: "",
    price: "",
    category: "",
  });

  useEffect(() => {
    fetchProduct();
  }, []);

  const fetchProduct = async () => {
    const res = await axios.get(`http://localhost:8080/api/products/${id}`);
    setProduct(res.data);
    setOldImages(res.data.imageUrls || []);
  };

  // ✅ Handle image change
  const handleImageChange = (index, file) => {
    const newImages = [...images];
    newImages[index] = file;
    setImages(newImages);
  };

  // ✅ Delete old image (UI only)
  const handleDeleteImage = (index) => {
    const updated = [...oldImages];
    updated.splice(index, 1);
    setOldImages(updated);
  };

  // ✅ UPDATE PRODUCT (FIXED)
  const handleUpdate = async (e) => {
  e.preventDefault();

  const formData = new FormData();

  formData.append("name", product.name);
  formData.append("description", product.description);
  formData.append("price", product.price);
  formData.append("category", product.category);

  // send only real images
  images.forEach((img) => {
    if (img) {
      formData.append("images", img);
    }
  });

  try {
    await axios.put(
      `http://localhost:8080/api/products/${id}`,
      formData
    );

    // ✅ FIX: delay navigation
    toast.success("Updated successfully 🚀");

    setTimeout(() => {
      navigate("/admin/products");
    }, 1500);

  } catch (err) {
    console.error(err);
    toast.error("Update failed ❌");
  }
};

  return (
    <div className="add-product-container">
      <h2>Edit Product</h2>

      {/* OLD IMAGES */}
      <div style={{ display: "flex", gap: "10px", marginBottom: "15px" }}>
        {oldImages.map((img, index) => (
          <div
            key={index}
            style={{
              position: "relative",
              width: "80px",
              height: "80px",
            }}
          >
            <img
              src={img}
              alt="old"
              style={{
                width: "100%",
                height: "100%",
                objectFit: "cover",
                borderRadius: "6px",
              }}
            />

            {/* DELETE */}
            <span
              onClick={() => handleDeleteImage(index)}
              style={{
                position: "absolute",
                top: "-6px",
                right: "-6px",
                background: "red",
                color: "white",
                borderRadius: "50%",
                width: "20px",
                height: "20px",
                fontSize: "14px",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                cursor: "pointer",
                fontWeight: "bold",
              }}
            >
              ×
            </span>
          </div>
        ))}
      </div>

      {/* NEW IMAGES */}
      <div className="image-grid">
        {images.map((img, index) => (
          <label key={index} className="upload-box">
            {img ? (
              <img
                src={URL.createObjectURL(img)}
                alt="preview"
                className="preview-img"
              />
            ) : (
              <span className="plus">+</span>
            )}

            <input
              type="file"
              className="file-input"
              onChange={(e) =>
                handleImageChange(index, e.target.files[0])
              }
            />
          </label>
        ))}
      </div>

      {/* FORM */}
      <form onSubmit={handleUpdate} className="form">
        <input
          value={product.name}
          onChange={(e) =>
            setProduct({ ...product, name: e.target.value })
          }
        />

        <textarea
          value={product.description}
          onChange={(e) =>
            setProduct({ ...product, description: e.target.value })
          }
        />

        <input
          type="number"
          value={product.price}
          onChange={(e) =>
            setProduct({ ...product, price: e.target.value })
          }
        />

        <select
          value={product.category}
          onChange={(e) =>
            setProduct({ ...product, category: e.target.value })
          }
        >
          <option>Electronics</option>
          <option>Foods</option>
          <option>Fashion & Beauty</option>
          <option>Toys</option>
          <option>Kids</option>
        </select>

        <button type="submit">Update</button>
      </form>
    </div>
  );
}

export default EditProduct;