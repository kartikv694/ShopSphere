import { useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import "./Navbar.css";
import { SearchContext } from "./SearchContext";

function Navbar() {
  const [input, setInput] = useState("");
  const navigate = useNavigate();

  // ✅ get from context (IMPORTANT FIX)
  const { setSearch, setCategory } = useContext(SearchContext);

  // 🔍 search button click
  const handleSearchClick = () => {
    setSearch(input);
  };

  // typing
  const handleChange = (e) => {
    setInput(e.target.value);
  };

  // category filter
  const handleCategory = (e) => {
    setCategory(e.target.value);
  };

  // logout
  const handleLogout = () => {
    localStorage.clear();
    navigate("/");
  };

  return (
    <div className="navbar">
      {/* LOGO */}
      <h2 className="logo">ShopSphere</h2>

      {/* SEARCH BAR */}
      <div className="search-container">
        <input
          type="text"
          placeholder="Search products..."
          value={input}
          onChange={handleChange}
          className="search-input"
        />

        {/* 🔍 ICON BUTTON */}
        <button className="search-btn" onClick={handleSearchClick}>
          🔍
        </button>

        {/* CATEGORY */}
        <select className="category-select" onChange={handleCategory}>
          <option value="">All</option>
          <option value="electronics">electronics</option>
          <option value="foods">foods</option>
          <option value="beauty">beauty</option>
          <option value="toys">toys</option>
        </select>
      </div>

      {/* RIGHT SIDE */}
      <div className="nav-right">
        <span className="profile">👤</span>
        <button className="logout-btn" onClick={handleLogout}>
          Logout
        </button>
      </div>
    </div>
  );
}

export default Navbar;