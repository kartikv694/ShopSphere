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

  // IMAGE SLIDER STATE
  const [imageIndexes, setImageIndexes] =
    useState({});

  // FETCH PRODUCTS
  useEffect(() => {

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

          // CATEGORY FILTER
          const filteredProducts =
            allProducts.filter((product) => {

              const productName =
                product.name?.toLowerCase() || "";

              const searchCategory =
                category.toLowerCase();

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
                categoryKeywords[
                  searchCategory
                ] || [];

              return keywords.some(
                (keyword) =>
                  productName.includes(
                    keyword
                  )
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

  // NEXT IMAGE
  const nextImage = (
    productId,
    totalImages,
    e
  ) => {

    e.preventDefault();

    setImageIndexes((prev) => ({

      ...prev,

      [productId]:
        (
          (prev[productId] || 0) + 1
        ) % totalImages

    }));

  };

  // PREVIOUS IMAGE
  const prevImage = (
    productId,
    totalImages,
    e
  ) => {

    e.preventDefault();

    setImageIndexes((prev) => ({

      ...prev,

      [productId]:
        (
          (prev[productId] || 0) - 1 +
          totalImages
        ) % totalImages

    }));

  };

  // ADD TO CART
  const handleAddToCart = (
    product
  ) => {

    let cart =
      JSON.parse(
        localStorage.getItem("cart")
      ) || [];

    const existingProductIndex =
      cart.findIndex(
        (item) =>
          item.id === product.id
      );

    if (
      existingProductIndex !== -1
    ) {

      cart[
        existingProductIndex
      ].quantity += 1;

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

        <Recommendations />

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
        background: "#f5f5f5",
        minHeight: "100vh",
        width: "100%",
        padding: "30px",
        boxSizing: "border-box"
      }}
    >

      {/* PAGE TITLE */}
      <h1
        style={{
          color: "black",
          fontSize: "42px",
          fontWeight: "700",
          marginBottom: "30px",
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

        <div
          style={{
            display: "grid",
            gridTemplateColumns:
              "repeat(3, 1fr)",
            gap: "28px",
            width: "100%"
          }}
        >

          {products.map((product) => (

            <div
              key={product.id}
              style={{
                background: "white",
                borderRadius: "18px",
                overflow: "hidden",
                boxShadow:
                  "0 4px 14px rgba(0,0,0,0.08)",
                transition: "0.3s",
                display: "flex",
                flexDirection: "column",
                padding: "20px"
              }}
            >

              {/* PRODUCT IMAGE */}
              <Link
                to={`/customer/products/${product.id}`}
                style={{
                  textDecoration: "none"
                }}
              >

                <div
                  style={{
                    position: "relative",
                    width: "100%",
                    height: "220px",
                    marginBottom: "15px",
                    background: "#f7f7f7",
                    borderRadius: "12px",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center"
                  }}
                >

                  <img
                    src={
                      product.imageUrls?.[
                        imageIndexes[
                          product.id
                        ] || 0
                      ] ||
                      "https://via.placeholder.com/250"
                    }
                    alt={product.name}
                    style={{
                      width: "100%",
                      height: "100%",
                      objectFit: "contain",
                      cursor: "pointer"
                    }}
                  />

                  {product.imageUrls
                    ?.length > 1 && (

                    <>

                      <button
                        onClick={(e) =>
                          prevImage(
                            product.id,
                            product.imageUrls
                              .length,
                            e
                          )
                        }
                        style={{
                          position: "absolute",
                          left: "6px",
                          padding: "0",
                          top: "50%",
                          transform: "translateY(-50%)",
                          width: "34px",
                          height: "34px",
                          minWidth: "34px",
                          borderRadius: "50%",
                          border: "none",
                          background: "white",
                          cursor: "pointer",
                          fontSize: "22px",
                          fontWeight: "bold",
                          display: "flex",
                          alignItems: "center",
                          justifyContent: "center",
                          boxShadow: "0 2px 8px rgba(0,0,0,0.15)"
                        }}
                      >
                        &#10094;
                      </button>

                      <button
                        onClick={(e) =>
                          nextImage(
                            product.id,
                            product.imageUrls
                              .length,
                            e
                          )
                        }
                        style={{
                          position: "absolute",
                          right: "6px",
                          right: "6px",
                          padding: "0",
                          top: "50%",
                          transform: "translateY(-50%)",
                          width: "34px",
                          height: "34px",
                          minWidth: "34px",
                          borderRadius: "50%",
                          border: "none",
                          background: "white",
                          cursor: "pointer",
                          fontSize: "22px",
                          fontWeight: "bold",
                          display: "flex",
                          alignItems: "center",
                          justifyContent: "center",
                          boxShadow: "0 2px 8px rgba(0,0,0,0.15)"
                        }}
                      >
                        &#10095;
                      </button>

                    </>

                  )}

                </div>

              </Link>

              {/* PRODUCT DETAILS */}
              <Link
                to={`/customer/products/${product.id}`}
                style={{
                  textDecoration: "none"
                }}
              >

                <h2
                  style={{
                    color: "black",
                    fontSize: "20px",
                    fontWeight: "600",
                    lineHeight: "30px",
                    marginBottom: "10px",
                    minHeight: "70px"
                  }}
                >
                  {product.name}
                </h2>

              </Link>

              <p
                style={{
                  color: "#666",
                  fontSize: "15px",
                  marginBottom: "14px",
                  textTransform: "capitalize"
                }}
              >
                {product.category}
              </p>

              <h3
                style={{
                  color: "black",
                  fontSize: "30px",
                  fontWeight: "700",
                  marginBottom: "20px"
                }}
              >
                ₹{product.price}
              </h3>

              <button
                onClick={() =>
                  handleAddToCart(product)
                }
                style={{
                  marginTop: "auto",
                  padding: "14px",
                  background: "#FFD814",
                  border: "none",
                  borderRadius: "30px",
                  cursor: "pointer",
                  fontWeight: "600",
                  fontSize: "16px",
                  width: "100%"
                }}
              >
                Add To Cart
              </button>

            </div>

          ))}

        </div>

      )}

    </div>

  );

}

export default CustomerDashboard;