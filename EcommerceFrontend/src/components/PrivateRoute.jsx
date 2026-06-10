import { Navigate } from "react-router-dom";
import { clearSession, getRole, hasValidSession } from "../utils/auth";

function PrivateRoute({ children }) {
  const role = getRole();

  if (!hasValidSession()) {
    clearSession();
    return <Navigate to="/" replace />;
  }

  if (role !== "ADMIN") {
    return <Navigate to="/customer/dashboard" replace />;
  }

  return children;
}

export default PrivateRoute;
