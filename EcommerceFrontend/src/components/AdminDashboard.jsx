import { useEffect, useState } from "react";
import "./Dashboard.css";

function AdminDashboard() {
  const [totalUsers, setTotalUsers] = useState(0);
  const [totalProducts, setTotalProducts] = useState(0);

  useEffect(() => {
    // TEMP dummy values (we connect backend next step)
    setTotalUsers(5);
    setTotalProducts(2);
  }, []);

  return (
    <div className="dashboard-container">
      <h1>Admin Dashboard 📊</h1>

      <div className="cards">

        <div className="card">
          <h2>Users</h2>
          <p>{totalUsers}</p>
        </div>

        <div className="card">
          <h2>Products</h2>
          <p>{totalProducts}</p>
        </div>

      </div>
    </div>
  );
}

export default AdminDashboard;