import { useState } from "react";
import { ToastContainer, toast } from "react-toastify";
import { Link, useNavigate } from "react-router-dom";
import "./auth.css";
import "react-toastify/dist/ReactToastify.css";
import { API_BASE_URL, setSession } from "../utils/auth";
import { syncCartFromServer } from "../utils/cartApi";
import {
  syncSavedLocationFromServer,
  syncRecentlyViewedFromServer
} from "../utils/userPreferencesApi";

function Login() {
  const [loginData, setLoginData] = useState({
    email: "",
    password: ""
  });

  const navigate = useNavigate();

  const handleChange = (event) => {
    setLoginData({
      ...loginData,
      [event.target.name]: event.target.value
    });
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    try {
      const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(loginData)
      });

      if (response.ok) {
        const data = await response.json();

        setSession(data);
        toast.success("Login successful");

        // Admins don't have a cart, location, or recently-viewed list.
        if (data.role !== "ADMIN") {
          syncCartFromServer();
          syncSavedLocationFromServer();
          syncRecentlyViewedFromServer();
        }

        setLoginData({
          email: "",
          password: ""
        });

        setTimeout(() => {
          if (data.role === "ADMIN") {
            navigate("/admin/dashboard");
          } else {
            navigate("/customer/dashboard");
          }
        }, 700);
      } else {
        toast.error("Invalid email or password");
      }
    } catch (error) {
      console.log(error);
      toast.error("Server error");
    }
  };

  return (
    <>
      <div className="auth-container">
        <div className="auth-box">
          <h2>Login</h2>

          <form onSubmit={handleSubmit}>
            <input
              name="email"
              placeholder="Enter Email"
              value={loginData.email}
              onChange={handleChange}
              required
            />

            <input
              name="password"
              type="password"
              placeholder="Enter Password"
              value={loginData.password}
              onChange={handleChange}
              required
            />

            <button>Login</button>
          </form>

          <p>
            Don't have an account?
            <Link to="/register"> Register</Link>
          </p>
        </div>
      </div>

      <ToastContainer
        position="top-right"
        autoClose={4000}
        newestOnTop
        closeOnClick
        pauseOnHover
      />
    </>
  );
}

export default Login;
