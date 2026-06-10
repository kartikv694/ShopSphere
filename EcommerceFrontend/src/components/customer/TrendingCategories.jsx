import { useNavigate } from "react-router-dom";
import "./Customer.css";

function TrendingCategories() {

  const navigate = useNavigate();

  const categories = [

      {
      name: "Electronics",
      slug: "electronics"
    },

    {
      name: "Foods",
      slug: "foods"
    },

    {
      name: "Beauty",
      slug: "beauty"
    },

    {
      name: "Toys",
      slug: "toys"
    }

  ];

  return (

    <div className="trending-wrapper">

      <h2 className="section-title">
        Trending Categories
      </h2>

      <div className="trending-grid">

        {categories.map((category, index) => (

          <div
            className="trending-card"
            key={index}
            onClick={() =>
              navigate(
                `/customer/category/${category.name.toLowerCase()}`
              )
            }
          >

            <div className="trending-overlay">

              <h3 className="trending-name">
                {category.name}
              </h3>

            </div>

          </div>

        ))}

      </div>

    </div>

  );
}

export default TrendingCategories;