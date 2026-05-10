import axios from "axios";
import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { toast } from "react-toastify";

import HeroSection from "./customer/HeroSection";
import TrendingCategories from "./customer/TrendingCategories";
import TrendingProducts from "./customer/TrendingProducts";
import Recommendations from "./customer/Recommendations";
import RecentlyViewed from "./customer/RecentlyViewed";
import BackToTop from "./customer/BackToTop";
import Footer from "./customer/Footer";

function CustomerDashboard() {

  const { category } = useParams();

  const [products, setProducts] = useState([]);

  // FETCH PRODUCTS
  useEffect(() => {

    // ONLY FETCH FOR CATEGORY PAGE
    if (category) {

      axios
        .get(
          "http://localhost:8080/api/products/all"
        )
        .then((response) => {

          let allProducts = [];

          if (Array.isArray(response.data)) {

            allProducts = response.data;

          } else if (
            Array.isArray(response.data.data)
          ) {

            allProducts = response.data.data;

          }

          // SHOW ALL PRODUCTS
          if (category.toLowerCase() === "all") {

            setProducts(allProducts);
            return;

          }

          // SMART FILTER
          const filteredProducts =
            allProducts.filter((product) => {

              const productName =
                product.name?.toLowerCase() || "";

              const searchCategory =
                category.toLowerCase();

              // CATEGORY KEYWORDS
              const categoryKeywords = {

                laptops: [
                  "laptop",
                  "macbook",
                  "dell",
                  "hp",
                  "lenovo",
                  "asus"
                ],

                mobiles: [
                  "iphone",
                  "samsung",
                  "mobile",
                  "redmi",
                  "oneplus",
                ],

                accessories: [
                  "headphone",
                  "earbuds",
                  "watch",
                  "mouse",
                  "keyboard",
                  "speaker"
                ],

                electronics: [
                  "laptop",
                  "iphone",
                  "mobile",
                  "electronics",
                  "headphone",
                  "macbook",
                  "dell"
                ]

              };

              const keywords =
                categoryKeywords[searchCategory] || [];

              return keywords.some((keyword) =>
                productName.includes(keyword)
              );

            });

          setProducts(filteredProducts);

        })
        .catch((error) => {

          console.log(error);

          setProducts([]);

        });

    }

  }, [category]);

  // ADD TO CART
  const handleAddToCart = (product) => {

    let cart =
      JSON.parse(localStorage.getItem("cart")) || [];

    const existingProductIndex =
      cart.findIndex(
        (item) => item.id === product.id
      );

    if (existingProductIndex !== -1) {

      cart[existingProductIndex].quantity += 1;

    } else {

      cart.push({
        ...product,
        quantity: 1
      });

    }

    localStorage.setItem(
      "cart",
      JSON.stringify(cart)
    );

    window.dispatchEvent(
      new Event("cartUpdated")
    );

    toast.success(
      "Product added to cart 🛒"
    );

  };

  // ================= DASHBOARD PAGE =================
  if (!category) {

    return (

      <div
        style={{
          background: "#eaeded",
          minHeight: "100vh",
          paddingBottom: "50px"
        }}
      >

        <HeroSection />

        <TrendingCategories />

        <TrendingProducts />

        <Recommendations/>

        <RecentlyViewed />

        <BackToTop />

        <Footer />

      </div>

    );

  }

  // ================= CATEGORY PRODUCTS PAGE =================
  return (

    <div
      style={{
        background: "#ffffff",
        minHeight: "100vh",
        width: "100%",
        padding: "0px 25px",
        boxSizing: "border-box"
      }}
    >

      {/* PAGE TITLE */}
      <h1
        style={{
          color: "black",
          fontSize: "32px",
          fontWeight: "600",
          marginTop: "10px",
          marginBottom: "25px",
          textTransform: "capitalize"
        }}
      >
        {category}
      </h1>

      {/* NO PRODUCTS */}
      {products.length === 0 ? (

        <h2
          style={{
            color: "black",
            fontWeight: "400"
          }}
        >
          No Products Found
        </h2>

      ) : (

        products.map((product) => (

          <div
            key={product.id}
            style={{
              display: "flex",
              alignItems: "flex-start",
              gap: "30px",
              width: "100%",
              padding: "20px 0",
              borderBottom: "1px solid #ddd"
            }}
          >

            {/* PRODUCT IMAGE */}
            <div
              style={{
                width: "220px",
                display: "flex",
                justifyContent: "center"
              }}
            >

              <Link
                to={`/customer/products/${product.id}`}
              >

                <img
                  src={
                    product.imageUrls?.[0] ||
                    "https://via.placeholder.com/250"
                  }
                  alt={product.name}
                  style={{
                    width: "180px",
                    height: "180px",
                    objectFit: "contain",
                    cursor: "pointer"
                  }}
                />

              </Link>

            </div>

            {/* PRODUCT DETAILS */}
            <div
              style={{
                flex: 1,
                paddingTop: "10px"
              }}
            >

              <Link
                to={`/customer/products/${product.id}`}
                style={{
                  textDecoration: "none"
                }}
              >

                <h2
                  style={{
                    color: "black",
                    fontSize: "18px",
                    fontWeight: "500",
                    marginBottom: "8px",
                    lineHeight: "28px",
                    cursor: "pointer"
                  }}
                >
                  {product.name}
                </h2>

              </Link>

              <p
                style={{
                  color: "#565959",
                  fontSize: "14px",
                  marginBottom: "10px",
                  textTransform: "capitalize"
                }}
              >
                {product.category}
              </p>

              <h3
                style={{
                  color: "black",
                  fontSize: "22px",
                  fontWeight: "600"
                }}
              >
                ₹{product.price}
              </h3>

              <button
                onClick={() =>
                  handleAddToCart(product)
                }
                style={{
                  marginTop: "18px",
                  padding: "10px 22px",
                  background: "#FFD814",
                  border: "none",
                  borderRadius: "25px",
                  cursor: "pointer",
                  fontWeight: "600",
                  fontSize: "15px"
                }}
              >
                Add To Cart
              </button>

            </div>

          </div>

        ))

      )}

    </div>

  );

}

export default CustomerDashboard;