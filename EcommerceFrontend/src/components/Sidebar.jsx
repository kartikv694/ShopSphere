import { Link, useLocation } from "react-router-dom";
import "./Sidebar.css";

const Sidebar = () => {
  const location = useLocation();

  const getClass = (path) =>
    `sidebar-item ${location.pathname === path ? "active" : ""}`;

  return (
    <div className="sidebar">

      <Link to="/admin/dashboard" className={getClass("/admin/dashboard")}>
        📊 Dashboard
      </Link>

      <Link to="/admin/add-product" className={getClass("/admin/add-product")}>
        ➕ Add Items
      </Link>

      <Link to="/admin/products" className={getClass("/admin/products")}>
        📋 List Items
      </Link>

      <Link to="/admin/orders" className={getClass("/admin/orders")}>
        🧾 Orders
      </Link>

    </div>
  );
};

export default Sidebar;