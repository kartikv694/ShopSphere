import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./AddProduct.css";
import axios from "axios";
import { toast } from "react-toastify";
import { API_BASE_URL } from "../utils/auth";

function AddProduct() {
  const navigate = useNavigate();
  const [images, setImages] = useState([null, null, null, null]);
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [price, setPrice] = useState("");
  const [category, setCategory] = useState("");

  // 🔥 Handle image selection
  // 🔥 Submit form
  const handleSubmit = async (e) => {
    e.preventDefault();

    const token = localStorage.getItem("token"); // ✅ IMPORTANT

    const validImages = images.filter((img) => img !== null);

    if (validImages.length === 0) {
      toast.error("Please upload at least one image ❌");
      return;
    }

    if (!name || !description || !price || !category) {
      toast.error("Please fill all fields ❌");
      return;
    }

    const formData = new FormData();
    formData.append("name", name);
    formData.append("description", description);
    formData.append("price", price);
    formData.append("category", category);

    validImages.forEach((img) => {
      formData.append("images", img);
    });

    const toastId = toast.loading("Uploading product...");

    try {
      await axios.post(
        `${API_BASE_URL}/api/products/add`,
        formData,
        {
          headers: {
            Authorization: `Bearer ${token}`, // ✅ FIX
            // ❗ Don't manually set Content-Type for FormData
          },
        }
      );

      toast.update(toastId, {
        render: "Product Added Successfully 🚀",
        type: "success",
        isLoading: false,
        autoClose: 2000,
      });

      // reset form
      setImages([null, null, null, null]);
      setName("");
      setDescription("");
      setPrice("");
      setCategory("");

      // navigate away from the add-product form back to the listing
      navigate("/admin/products");

    } catch (error) {
      console.error(error);

      toast.update(toastId, {
        render: "Error adding product ❌",
        type: "error",
        isLoading: false,
        autoClose: 2000,
      });
    }
  };

  return (
    <div className="add-product-container">
      <h2>Upload Images</h2>

      {/* 🔥 IMAGE GRID */}
      <div className="image-grid">
        {images.map((img, index) => (
          <label key={index} className="upload-box">

            {img ? (
              <img src={URL.createObjectURL(img)} alt="preview" />
            ) : (
              <span>+</span>
            )}

            <input
              type="file"
              multiple
              className="file-input"
              onChange={(e) => {
                const files = Array.from(e.target.files);

                const newImages = [...images];

                files.forEach((file, i) => {
                  if (index + i < 4) {
                    newImages[index + i] = file;
                  }
                });

                setImages(newImages);
              }}
            />
          </label>
        ))}
      </div>

      {/* 🔥 FORM */}
      <form onSubmit={handleSubmit} className="form">

        <input
          type="text"
          placeholder="Product Name"
          value={name}
          onChange={(e) => setName(e.target.value)}
        />

        <textarea
          placeholder="Product Description"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />

        {/* CATEGORY + PRICE */}
        <div className="row">

          <div className="input-group">
            <select
              value={category}
              onChange={(e) => setCategory(e.target.value)}
            >
              <option value="" disabled>Select Category</option>
              <option value="electronics">Electronics</option>
              <option value="foods">Foods</option>
              <option value="beauty">Beauty</option>
              <option value="toys">Toys</option>
              <option value="kids">Kids</option>
            </select>
          </div>

          <div className="input-group">
            <input
              type="number"
              placeholder="Product Price"
              value={price}
              onChange={(e) => setPrice(e.target.value)}
            />
          </div>

        </div>

        <button type="submit">ADD</button>
      </form>
    </div>
  );
}

export default AddProduct;
