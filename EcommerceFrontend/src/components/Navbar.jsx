import { useState } from "react";
import "./Navbar.css";

function Navbar({ setSearch }) {
  const [input, setInput] = useState("");

  const handleSearch = (e) => {
    setInput(e.target.value);
    if (setSearch) setSearch(e.target.value);
  };

  return (
    <div className="navbar">
      {/* LEFT: Logo */}
      <h2 className="logo">ShopSphere</h2>

      {/* CENTER: Search */}
      <input
        type="text"
        placeholder="Search products..."
        value={input}
        onChange={handleSearch}
        className="search-input"
      />

      {/* RIGHT: Profile + Logout */}
      <div className="nav-right">
        <span className="profile">👤</span>
        <button className="logout-btn">Logout</button>
      </div>
    </div>
  );
}

export default Navbar;