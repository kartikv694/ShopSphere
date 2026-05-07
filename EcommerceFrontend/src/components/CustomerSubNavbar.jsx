import { useState } from "react";
import "./Navbar.css";
import { FaBars } from "react-icons/fa";
import { useNavigate } from "react-router-dom";

function CustomerSubNavbar() {

  const [openMenu, setOpenMenu] = useState(false);

  const navigate = useNavigate();

  // USER
  const user = JSON.parse(localStorage.getItem("user"));

  // LOGOUT
  const handleLogout = () => {

    localStorage.removeItem("user");

    window.location.href = "/login";

  };

  // CATEGORY CLICK
  const handleCategoryClick = (category) => {

    navigate(`/customer/category/${category}`);

    setOpenMenu(false);

  };

  return (
    <>

      {/* TOP STRIP */}
      <div className="sub-navbar">

        <div
          className="menu-btn"
          onClick={() => setOpenMenu(true)}
        >

          <FaBars />

          <span>All</span>

        </div>

      </div>

      {/* SIDEBAR */}
      {openMenu && (

        <div
          className="sidebar-overlay"
          onClick={() => setOpenMenu(false)}
        >

          <div
            className="sidebar"
            onClick={(e) => e.stopPropagation()}
          >

            {/* HEADER */}
            <div className="sidebar-header">

              <span>
                Hello, {
                  user?.name ||
                  user?.email?.split("@")[0] ||
                  "Guest"
                }
              </span>

              <span
                className="close-btn"
                onClick={() => setOpenMenu(false)}
              >
                ✕
              </span>

            </div>

            {/* TRENDING */}
            <div className="sidebar-section">

              <h4>Trending</h4>

              <p>Bestsellers</p>

              <p>New Releases</p>

            </div>

            {/* SHOP BY CATEGORY */}
            <div className="sidebar-section">

              <h4>Shop by Category</h4>

              <p onClick={() => handleCategoryClick("all")}>
                All
              </p>

              <p onClick={() => handleCategoryClick("electronics")}>
                Electronics
              </p>

              <p onClick={() => handleCategoryClick("food")}>
                Food
              </p>

              <p onClick={() => handleCategoryClick("fashion")}>
                Fashion and Beauty
              </p>

              <p onClick={() => handleCategoryClick("toys")}>
                Toys
              </p>

            </div>

            {/* HELP */}
            <div className="sidebar-section">

              <h4>Help & Settings</h4>

              <p>Your Account</p>

              <p>Customer Service</p>

              <p
                onClick={handleLogout}
                style={{ color: "red" }}
              >
                Sign Out
              </p>

            </div>

          </div>

        </div>

      )}

    </>
  );
}

export default CustomerSubNavbar;