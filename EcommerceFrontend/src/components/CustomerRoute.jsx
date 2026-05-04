import { Navigate } from "react-router-dom";
import { toast } from "react-toastify";

function CustomerRoute({ children }) {
  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role");

  // ❌ not logged in
  if (!token) {
    toast.error("User not logged in");

    setTimeout(() => {
      window.location.href = "/login";
    }, 1500);

    return null;
  }

  // ❌ not customer
  if (role !== "CUSTOMER") {
    return <Navigate to="/admin" />;
  }

  // ✅ customer allowed
  return children;
}

export default CustomerRoute;