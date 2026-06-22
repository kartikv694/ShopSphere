import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { API_BASE_URL, setSession, hasValidSession, getRole } from "../utils/auth";
import { syncCartFromServer } from "../utils/cartApi";
import {
  syncSavedLocationFromServer,
  syncRecentlyViewedFromServer
} from "../utils/userPreferencesApi";
import "./home.css";

function Home() {
  const [isLogin, setIsLogin] = useState(true);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const [loginData, setLoginData] = useState({ email: "", password: "" });
  const [registerData, setRegisterData] = useState({ name: "", email: "", password: "", role: "CUSTOMER" });

  // redirect if already logged in
  useEffect(() => {
    if (hasValidSession()) {
      const role = getRole();
      if (role === "ADMIN") navigate("/admin/dashboard");
      else navigate("/customer/dashboard");
    }
  }, [navigate]);

  const handleLoginChange = (e) => setLoginData({ ...loginData, [e.target.name]: e.target.value });
  const handleRegisterChange = (e) => setRegisterData({ ...registerData, [e.target.name]: e.target.value });

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const res = await fetch(`${API_BASE_URL}/api/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(loginData),
      });
      if (res.ok) {
        const data = await res.json();
        setSession(data);
        toast.success("Welcome back! 🎉");

        // Admins don't have a cart, location, or recently-viewed list.
        if (data.role !== "ADMIN") {
          syncCartFromServer();
          syncSavedLocationFromServer();
          syncRecentlyViewedFromServer();
        }
        setTimeout(() => {
          if (data.role === "ADMIN") navigate("/admin/dashboard");
          else navigate("/customer/dashboard");
        }, 700);
      } else {
        toast.error("Invalid email or password");
      }
    } catch {
      toast.error("Server error");
    } finally {
      setLoading(false);
    }
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const res = await fetch(`${API_BASE_URL}/api/auth/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(registerData),
      });
      if (res.ok) {
        toast.success("Account created! Please log in 🎉");
        setRegisterData({ name: "", email: "", password: "", role: "CUSTOMER" });
        setTimeout(() => setIsLogin(true), 1000);
      } else {
        toast.error("Registration failed. Email may already exist.");
      }
    } catch {
      toast.error("Server error");
    } finally {
      setLoading(false);
    }
  };

  const features = [
    { icon: "🚀", title: "Fast Delivery", desc: "Get your orders delivered within 24 hours" },
    { icon: "🔒", title: "Secure Payments", desc: "100% safe and encrypted transactions" },
    { icon: "💎", title: "Premium Quality", desc: "Curated products from top brands" },
    { icon: "🔄", title: "Easy Returns", desc: "Hassle-free 30-day return policy" },
  ];

  return (
    <div className="home-wrapper">
      {/* LEFT PANEL */}
      <div className="home-left">
        <div className="home-brand">
          <span className="brand-icon">🛍</span>
          <span className="brand-name">ShopSphere</span>
        </div>

        <div className="home-hero">
          <h1>Your Ultimate<br /><span>Shopping Destination</span></h1>
          <p>Discover thousands of products at unbeatable prices. Shop smart, shop easy.</p>
        </div>

        <div className="home-features">
          {features.map((f, i) => (
            <div className="feature-item" key={i}>
              <span className="feature-icon">{f.icon}</span>
              <div>
                <strong>{f.title}</strong>
                <p>{f.desc}</p>
              </div>
            </div>
          ))}
        </div>

        <div className="home-decoration">
          <div className="deco-circle c1"></div>
          <div className="deco-circle c2"></div>
          <div className="deco-circle c3"></div>
        </div>
      </div>

      {/* RIGHT PANEL - AUTH */}
      <div className="home-right">
        <div className="auth-card">
          {/* TAB TOGGLE */}
          <div className="auth-tabs">
            <button
              className={`auth-tab ${isLogin ? "active" : ""}`}
              onClick={() => setIsLogin(true)}
            >
              Login
            </button>
            <button
              className={`auth-tab ${!isLogin ? "active" : ""}`}
              onClick={() => setIsLogin(false)}
            >
              Register
            </button>
          </div>

          {/* LOGIN FORM */}
          <div className={`auth-form-wrapper ${isLogin ? "visible" : "hidden"}`}>
            <div className="auth-form-header">
              <h2>Welcome back 👋</h2>
              <p>Sign in to continue shopping</p>
            </div>
            <form onSubmit={handleLogin} className="auth-form">
              <div className="form-group">
                <label>Email Address</label>
                <input
                  name="email"
                  type="email"
                  placeholder="you@example.com"
                  value={loginData.email}
                  onChange={handleLoginChange}
                  required
                />
              </div>
              <div className="form-group">
                <label>Password</label>
                <input
                  name="password"
                  type="password"
                  placeholder="Enter your password"
                  value={loginData.password}
                  onChange={handleLoginChange}
                  required
                />
              </div>
              <button type="submit" className="auth-submit-btn" disabled={loading}>
                {loading ? <span className="btn-spinner"></span> : "Sign In"}
              </button>
            </form>
            <p className="auth-switch">
              Don't have an account?{" "}
              <button onClick={() => setIsLogin(false)}>Create one</button>
            </p>
          </div>

          {/* REGISTER FORM */}
          <div className={`auth-form-wrapper ${!isLogin ? "visible" : "hidden"}`}>
            <div className="auth-form-header">
              <h2>Join ShopSphere 🎉</h2>
              <p>Create your free account today</p>
            </div>
            <form onSubmit={handleRegister} className="auth-form">
              <div className="form-group">
                <label>Full Name</label>
                <input
                  name="name"
                  placeholder="Your full name"
                  value={registerData.name}
                  onChange={handleRegisterChange}
                  required
                />
              </div>
              <div className="form-group">
                <label>Email Address</label>
                <input
                  name="email"
                  type="email"
                  placeholder="you@example.com"
                  value={registerData.email}
                  onChange={handleRegisterChange}
                  required
                />
              </div>
              <div className="form-group">
                <label>Password</label>
                <input
                  name="password"
                  type="password"
                  placeholder="Min. 4 characters"
                  value={registerData.password}
                  onChange={handleRegisterChange}
                  required
                />
              </div>
              <div className="form-group">
                <label>Account Type</label>
                <select name="role" value={registerData.role} onChange={handleRegisterChange}>
                  <option value="CUSTOMER">Customer</option>
                  <option value="SELLER">Seller</option>
                </select>
              </div>
              <button type="submit" className="auth-submit-btn register" disabled={loading}>
                {loading ? <span className="btn-spinner"></span> : "Create Account"}
              </button>
            </form>
            <p className="auth-switch">
              Already have an account?{" "}
              <button onClick={() => setIsLogin(true)}>Sign in</button>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Home;
