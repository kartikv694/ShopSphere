import { Link, useLocation, useNavigate } from "react-router-dom";
import "./Sidebar.css";
import { logout } from "../utils/auth";

const Sidebar = () => {
  const location = useLocation();
  const navigate = useNavigate();

  const getClass = (path) =>
    `sidebar-item ${location.pathname === path ? "active" : ""}`;

  const handleLogout = async () => {
    await logout();
    navigate("/");
  };

  const navItems = [
    { path: "/admin/dashboard", icon: "📊", label: "Dashboard" },
    { path: "/admin/add-product", icon: "➕", label: "Add Product" },
    { path: "/admin/products", icon: "📋", label: "Products" },
    { path: "/admin/orders", icon: "🧾", label: "Orders" },
  ];

  return (
    <div className="sidebar">
      <div className="sidebar-nav">
        {navItems.map((item) => (
          <Link key={item.path} to={item.path} className={getClass(item.path)}>
            <span className="sidebar-icon">{item.icon}</span>
            <span className="sidebar-label">{item.label}</span>
          </Link>
        ))}
      </div>
      <button className="sidebar-logout" onClick={handleLogout}>
        <span>🚪</span>
        <span>Logout</span>
      </button>
    </div>
  );
};

export default Sidebar;
