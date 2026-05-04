import { useState } from "react";
import "./Navbar.css";
import { FaBars } from "react-icons/fa";

function CustomerSubNavbar() {
  const [openMenu, setOpenMenu] = useState(false);

  // ✅ Get user from localStorage
  const user = JSON.parse(localStorage.getItem("user"));

  const handleLogout = () => {
    localStorage.removeItem("user");
    window.location.href = "/login"; // redirect after logout
  };

  return (
    <>
      {/* TOP STRIP */}
      <div className="sub-navbar">
        <div className="menu-btn" onClick={() => setOpenMenu(true)}>
          <FaBars />
          <span>All</span>
        </div>
      </div>

      {/* SIDEBAR */}
      {openMenu && (
        <div className="sidebar-overlay" onClick={() => setOpenMenu(false)}>
          <div
            className="sidebar"
            onClick={(e) => e.stopPropagation()}
          >
            {/* ✅ DYNAMIC HEADER */}
            <div className="sidebar-header">
                          <span>
                              Hello, {user?.name || user?.email?.split("@")[0] || "Guest"}
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
              <p>Electronics</p>
              <p>Food</p>
              <p>Fashion and Beauty</p>
              <p>Toys</p>
            </div>

            {/* HELP */}
            <div className="sidebar-section">
              <h4>Help & Settings</h4>
              <p>Your Account</p>
              <p>Customer Service</p>

              {/* ✅ Logout clickable */}
              <p onClick={handleLogout} style={{ color: "red" }}>
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