import { Navigate } from "react-router-dom";
import { toast } from "react-toastify";
import { clearSession, getRole, hasValidSession } from "../utils/auth";

function CustomerRoute({ children }) {
  const role = getRole();

  if (!hasValidSession()) {
    clearSession();
    toast.error("Session expired. Please login again.");
    return <Navigate to="/" replace />;
  }

  if (role !== "CUSTOMER") {
    return <Navigate to="/admin/dashboard" replace />;
  }

  return children;
}

export default CustomerRoute;
