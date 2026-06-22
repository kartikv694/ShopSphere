import axios from "axios";
import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { toast } from "react-toastify";

import HeroSection from "./customer/HeroSection";
import TrendingCategories from "./customer/TrendingCategories";
import TrendingProducts from "./customer/TrendingProducts";
import Recommendations from "./customer/Recommendations";
import RecentlyViewed from "./customer/RecentlyViewed";
import { API_BASE_URL } from "../utils/auth";
import { addToCart as addToCartApi } from "../utils/cartApi";
import "./CustomerDashboard.css";

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
          `${API_BASE_URL}/api/products/all`
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

              const productCategory =
                product.category?.toLowerCase() || "";  

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
                ],

                foods: [
                  "food",
                  "foods",
                  "grocery",
                  "snack",
                  "drink",
                  "beverage"
                ],

                beauty: [
                  "beauty",
                  "cosmetic",
                  "skincare",
                  "makeup",
                  "cream",
                  "lotion"
                ],

                toys: [
                  "toy",
                  "toys",
                  "game",
                  "kids"
                ]

              };

              const keywords =
                categoryKeywords[
                  searchCategory
                ] || [];

                return (
                productCategory === searchCategory ||
                keywords.some(
                (keyword) =>
                  productName.includes(
                    keyword
                  ) ||
                  productCategory.includes(
                    keyword
                  )
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
  const handleAddToCart = async (
    product
  ) => {

    try {

      await addToCartApi(product, 1);

      toast.success(
        "Product added to cart 🛒"
      );

    } catch (error) {

      console.log(error);

      toast.error(
        "Please login to add items to your cart"
      );

    }

  };

  // ================= DASHBOARD PAGE =================
  if (!category) {

    return (

      <div className="cd-dashboard-home">

        <HeroSection />

        <TrendingCategories />

        <TrendingProducts />

        <Recommendations />

        <RecentlyViewed />

      </div>

    );

  }

  // ================= CATEGORY PRODUCTS PAGE =================
  return (

    <div className="cd-category-page">

      {/* PAGE TITLE */}
      <h1 className="cd-category-title">
        {category}
      </h1>

      {/* NO PRODUCTS */}
      {products.length === 0 ? (

        <h2 className="cd-no-products">
          No Products Found
        </h2>

      ) : (

        <div className="cd-products-grid">

          {products.map((product) => (

            <div
              key={product.id}
              className="cd-product-card"
            >

              {/* PRODUCT IMAGE */}
              <Link
                to={`/customer/products/${product.id}`}
                className="cd-product-link"
              >

                <div className="cd-product-image-wrap">

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
                    className="cd-product-image"
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
                        className="cd-slider-btn cd-slider-left"
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
                        className="cd-slider-btn cd-slider-right"
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
                className="cd-product-link"
              >

                <h2 className="cd-product-name">
                  {product.name}
                </h2>

              </Link>

              <p className="cd-product-category">
                {product.category}
              </p>

              <h3 className="cd-product-price">
                ₹{product.price}
              </h3>

              <button
                onClick={() =>
                  handleAddToCart(product)
                }
                className="cd-add-to-cart-btn"
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
