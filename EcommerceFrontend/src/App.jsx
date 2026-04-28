import { BrowserRouter, Routes, Route } from "react-router-dom";
import Register from "./components/Register";
import Login from "./components/Login";
import "./App.css";
import Home from "./components/home";
import PrivateRoute from "./components/PrivateRoute";
import Products from "./components/Products";
import ProductDetails from "./components/ProductsDetails";
import Navbar from "./components/Navbar"; 
import CustomerDashboard from "./components/CustomerDashboard";
import AdminDashboard from "./components/AdminDashboard";

function App() {

  return (

    <BrowserRouter>

      <Navbar />

      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<Login />} />
        <Route path="/products" element={<Products />} />
        <Route path="/products/:id" element={<ProductDetails />} />
        <Route path="/customer" element={<CustomerDashboard />} />
        <Route path="/admin-dashboard" element={<AdminDashboard />} />
     
      {/* Protect Routes */}
      
      <Route
          path="/dashboard"
          element={
            <PrivateRoute>
            </PrivateRoute>
          }
        />

      </Routes>

    </BrowserRouter>

  );

}

export default App;