import { useEffect, useState } from "react";
import axios from "axios";
import "./Dashboard.css";

function AdminDashboard() {
  const [totalUsers, setTotalUsers] = useState(0);
  const [totalProducts, setTotalProducts] = useState(0);

  useEffect(() => {
    fetchDashboard();
  }, []);

  const fetchDashboard = async () => {
    try {
      const token = localStorage.getItem("token");

      const res = await axios.get(
        "http://localhost:8080/api/dashboard",
        {
          headers: {
            Authorization: `Bearer ${token}`, // ✅ FIX
          },
        }
      );

      setTotalUsers(res.data.totalUsers);
      setTotalProducts(res.data.totalProducts);
    } catch (error) {
      console.error("Error fetching dashboard data:", error);
    }
  };

  return (
    <div className="dashboard-container">
      <h1 className="dashboard-title">Welcome to Admin Panel 👋</h1>

      <p className="dashboard-subtitle">
        Manage your products and users easily
      </p>

      <div className="cards">
        <div className="card">
          <h2>👥 Users</h2>
          <p>{totalUsers}</p>
        </div>

        <div className="card">
          <h2>📦 Products</h2>
          <p>{totalProducts}</p>
        </div>
      </div>
    </div>
  );
}

export default AdminDashboard;