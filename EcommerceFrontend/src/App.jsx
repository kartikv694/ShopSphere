import { BrowserRouter, Routes, Route } from "react-router-dom";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

import Register from "./components/Register";
import Login from "./components/Login";
import Home from "./components/home";
import Products from "./components/Products";
import ProductDetails from "./components/ProductsDetails";
import CustomerDashboard from "./components/CustomerDashboard";
import AdminDashboard from "./components/AdminDashboard";
import ProductList from "./components/ProductList";
import AddProduct from "./components/AddProduct";

import PrivateRoute from "./components/PrivateRoute";


import "./App.css";

{/* Layouts */}
import AdminLayout from "./components/AdminLayout";
import CustomerLayout from "./components/CustomerLayout";

function App() {
  return (
    <BrowserRouter>
      <Routes>

        {/* NO NAVBAR */}
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        {/* CUSTOMER */}
        <Route path="/" element={<CustomerLayout><Home /></CustomerLayout>} />
        <Route path="/products" element={<CustomerLayout><Products /></CustomerLayout>} />
        <Route path="/products/:id" element={<CustomerLayout><ProductDetails /></CustomerLayout>} />
        <Route path="/customer" element={<CustomerLayout><CustomerDashboard /></CustomerLayout>} />

        {/* ADMIN */}
        <Route
          path="/admin/dashboard"
          element={<PrivateRoute><AdminLayout><AdminDashboard /></AdminLayout></PrivateRoute>}
        />

        <Route
          path="/admin/products"
          element={<PrivateRoute><AdminLayout><ProductList /></AdminLayout></PrivateRoute>}
        />

        <Route
          path="/admin/add-product"
          element={<PrivateRoute><AdminLayout><AddProduct /></AdminLayout></PrivateRoute>}
        />

      </Routes>

      {/* 🔥 TOAST CONTAINER */}
      <ToastContainer
        position="top-right"
        autoClose={2000}
        theme="dark"
        pauseOnHover
      />
    </BrowserRouter>
  );
}

export default App;