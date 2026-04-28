import { Navigate } from "react-router-dom";

function PrivateRoute({ children }) {

  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role");

  // not logged in
  if (!token) {
    return <Navigate to="/login" />;
  }

  // not admin
  if (role !== "ADMIN") {
    return <Navigate to="/products" />;
  }

  // admin allowed
  return children;
}

export default PrivateRoute;