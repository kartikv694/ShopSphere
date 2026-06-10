import { useContext, useState } from "react";
import { FaSearch } from "react-icons/fa";
import "./Navbar.css";
import { SearchContext } from "./SearchContextValue";
import { getStoredUser } from "../utils/auth";

function Navbar() {
  const [input, setInput] = useState("");
  const { setSearch, setCategory } = useContext(SearchContext);
  const user = getStoredUser();

  const handleSearchClick = () => setSearch(input.trim());

  const handleInputChange = (event) => {
    const value = event.target.value;
    setInput(value);
    setSearch(value.trim());
  };

  const handleKeyDown = (event) => {
    if (event.key === "Enter") {
      setSearch(input.trim());
    }
  };

  const handleCategory = (event) => setCategory(event.target.value);

  return (
    <div className="navbar">
      <div className="navbar-brand">
        <h2 className="logo">ShopSphere</h2>
        <span className="admin-badge">Admin</span>
      </div>

      <div className="search-container">
        <input
          type="text"
          placeholder="Search products..."
          value={input}
          onChange={handleInputChange}
          onKeyDown={handleKeyDown}
          className="search-input"
        />
        <button
          className="search-btn"
          onClick={handleSearchClick}
          type="button"
          aria-label="Search products"
        >
          <FaSearch />
        </button>
        <select
          className="category-select"
          onChange={handleCategory}
        >
          <option value="">All Categories</option>
          <option value="electronics">Electronics</option>
          <option value="laptops">Laptops</option>
          <option value="mobiles">Mobiles</option>
          <option value="accessories">Accessories</option>
          <option value="foods">Foods</option>
          <option value="beauty">Beauty</option>
          <option value="toys">Toys</option>
        </select>
      </div>

      <div className="nav-right">
        <div className="admin-user">
          <div className="admin-avatar">
            {user?.name ? user.name.charAt(0).toUpperCase() : "A"}
          </div>
          <span className="admin-name">{user?.name || "Admin"}</span>
        </div>
      </div>
    </div>
  );
}

export default Navbar;
