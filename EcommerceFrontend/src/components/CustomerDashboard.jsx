import axios from "axios";
import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";

function CustomerDashboard() {

  const { category } = useParams();

  const [products, setProducts] = useState([]);

  // FETCH PRODUCTS
  useEffect(() => {

    let apiUrl = "";

    // ALL PRODUCTS
    if (
      !category ||
      category.toLowerCase() === "all"
    ) {

      apiUrl = "http://localhost:8080/api/products/all";

    }

    // CATEGORY PRODUCTS
    else {

      apiUrl =
        `http://localhost:8080/api/products/category/${category}`;

    }

    axios
      .get(apiUrl)
      .then((response) => {

        if (Array.isArray(response.data)) {

          setProducts(response.data);

        } else if (Array.isArray(response.data.data)) {

          setProducts(response.data.data);

        } else {

          setProducts([]);

        }

      })
      .catch((error) => {

        console.log("API ERROR:", error);

        setProducts([]);

      });

  }, [category]);

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
        {category === "all"
          ? "All Products"
          : category}
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

            </div>

          </div>

        ))

      )}

    </div>

  );
}

export default CustomerDashboard;