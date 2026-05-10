import { useNavigate } from "react-router-dom";
import "./customer.css";

function HeroSection() {

  const navigate = useNavigate();

  const categories = [

    {
      title: "Electronics",
      route: "electronics",
      image:
        "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9"
    },

    {
      title: "Laptops",
      route: "laptops",
      image:
        "https://images.unsplash.com/photo-1496181133206-80ce9b88a853"
    },

    {
      title: "Mobiles",
      route: "mobiles",
      image:
        "https://images.unsplash.com/photo-1512941937669-90a1b58e7e9c"
    },

    {
      title: "Accessories",
      route: "accessories",
      image:
        "https://images.unsplash.com/photo-1505740420928-5e560c06d30e"
    }

  ];

  return (

    <div className="hero-wrapper">

      {/* HERO BANNER */}
      <div className="hero-banner">

        <div className="hero-content">

          <h1>
            Welcome to ShopSphere
          </h1>

          <p>
            Discover trending products,
            unbeatable prices and
            premium shopping experience.
          </p>

          <button
            onClick={() =>
              navigate("/customer/category/all")
            }
          >
            Shop Now
          </button>

        </div>

      </div>

      {/* CATEGORY SECTION */}
      <div className="hero-category-section">

        {categories.map((category, index) => (

          <div
            className="hero-category-card"
            key={index}
            onClick={() =>
              navigate(
                `/customer/category/${category.route}`
              )
            }
          >

            <img
              src={category.image}
              alt={category.title}
            />

            <h3>
              {category.title}
            </h3>

          </div>

        ))}

      </div>

    </div>

  );
}

export default HeroSection;