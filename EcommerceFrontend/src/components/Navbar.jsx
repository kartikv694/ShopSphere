import { useState } from "react";

function Navbar({ setSearch }) {
  const [input, setInput] = useState("");

  const handleSearch = (e) => {
    setInput(e.target.value);
    setSearch(e.target.value);
  };

  return (
    <div style={styles.nav}>
      <h2 style={styles.logo}>ShopSphere</h2>

      <input
        type="text"
        placeholder="Search products..."
        value={input}
        onChange={handleSearch}
        style={styles.input}
      />
    </div>
  );
}

const styles = {
  nav: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    padding: "15px 20px",
    background: "#2874f0",
    color: "#fff"
  },
  logo: {
    margin: 0
  },
  input: {
    padding: "8px",
    width: "250px",
    borderRadius: "5px",
    border: "none"
  }
};

export default Navbar;