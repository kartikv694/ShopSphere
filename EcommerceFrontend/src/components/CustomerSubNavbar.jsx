import { useState } from "react";
import "./Navbar.css";
import { FaBars } from "react-icons/fa";
import { useNavigate } from "react-router-dom";
import { logout, getStoredUser } from "../utils/auth";

function CustomerSubNavbar() {

  const [openMenu, setOpenMenu] = useState(false);

  const navigate = useNavigate();

  // USER
  const user = getStoredUser();

  // LOGOUT
  const handleLogout = async () => {

    await logout();

    navigate("/login");

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
          className="customer-sidebar-overlay"
          onClick={() => setOpenMenu(false)}
        >

          <div
            className="customer-sidebar"
            onClick={(e) => e.stopPropagation()}
          >

            {/* HEADER */}
            <div className="customer-sidebar-header">

              <span>
                Hello, {
                  user?.name ||
                  user?.email?.split("@")[0] ||
                  "Guest"
                }
              </span>

              <span
                className="customer-close-btn"
                onClick={() => setOpenMenu(false)}
              >
                ✕
              </span>

            </div>

            {/* TRENDING */}
            <div className="customer-sidebar-section">

              <h4>Trending</h4>

              <p
                onClick={() => {
                  navigate("/customer/bestsellers");
                  setOpenMenu(false);
                }}
              >
                Bestsellers
              </p>

              <p
                onClick={() => {
                  navigate("/customer/new-releases");
                  setOpenMenu(false);
                }}
              >
                New Releases
              </p>

            </div>

            {/* SHOP BY CATEGORY */}
            <div className="customer-sidebar-section">

              <h4>Shop by Category</h4>

              <p onClick={() => handleCategoryClick("all")}>
                All
              </p>

              <p onClick={() => handleCategoryClick("electronics")}>
                Electronics
              </p>

              <p onClick={() => handleCategoryClick("foods")}>
                Foods
              </p>

              <p onClick={() => handleCategoryClick("beauty")}>
                Beauty
              </p>

              <p onClick={() => handleCategoryClick("toys")}>
                Toys
              </p>

            </div>

            {/* HELP */}
            <div className="customer-sidebar-section">

              <h4>Help & Settings</h4>

              <p
                onClick={() => {
                  navigate("/customer/profile");
                  setOpenMenu(false);
                }}
              >
                Your Account
              </p>

              <p
                onClick={() => {
                  navigate("/customer/my-orders");
                  setOpenMenu(false);
                }}
              >
                Your Orders
              </p>

              <p
                onClick={() => {
                  navigate("/customer/service");
                  setOpenMenu(false);
                }}
              >
                Customer Service
              </p>

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
