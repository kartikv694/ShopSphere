import { BrowserRouter, Routes, Route } from "react-router-dom";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

import Register from "./components/Register";
import Login from "./components/Login";
import Home from "./components/Home";
import Products from "./components/Products";
import ProductDetails from "./components/ProductsDetails";
import CustomerDashboard from "./components/CustomerDashboard";
import AdminDashboard from "./components/AdminDashboard";
import ProductList from "./components/ProductList";
import AdminOrders from "./components/AdminOrders";
import AddProduct from "./components/AddProduct";
import EditProduct from "./components/EditProduct";
import { SearchProvider } from "./components/SearchContext";
import PrivateRoute from "./components/PrivateRoute";
import CustomerRoute from "./components/CustomerRoute";
import CartPage from "./components/CartPage";
import Checkout from "./components/Checkout";
import CustomerOrders from "./components/customer/CustomerOrders";
import CustomerProfile from "./components/customer/CustomerProfile";
import ProductCollectionPage from "./components/customer/ProductCollectionPage";
import CustomerService from "./components/customer/CustomerService";
import FooterPage from "./components/customer/FooterPage";
import AdminLayout from "./components/AdminLayout";
import CustomerLayout from "./components/CustomerLayout";
import "./App.css";

function App() {
  return (
    <SearchProvider>
      <BrowserRouter>
        <Routes>
          {/* HOME - standalone (has its own login/register) */}
          <Route path="/" element={<Home />} />

          {/* STANDALONE AUTH PAGES (kept for direct navigation) */}
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />

          {/* CUSTOMER PUBLIC */}
          <Route path="/customer/products" element={<CustomerLayout><Products /></CustomerLayout>} />
          <Route path="/customer/products/:id" element={<CustomerLayout><ProductDetails /></CustomerLayout>} />
          <Route path="/customer/bestsellers" element={<CustomerLayout><ProductCollectionPage type="bestsellers" /></CustomerLayout>} />
          <Route path="/customer/new-releases" element={<CustomerLayout><ProductCollectionPage type="new-releases" /></CustomerLayout>} />
          <Route path="/customer/service" element={<CustomerLayout><CustomerService /></CustomerLayout>} />
          <Route path="/customer/info/:slug" element={<CustomerLayout><FooterPage /></CustomerLayout>} />

          {/* CUSTOMER PROTECTED */}
          <Route path="/customer/dashboard" element={<CustomerRoute><CustomerLayout><CustomerDashboard /></CustomerLayout></CustomerRoute>} />
          <Route path="/customer/category/:category" element={<CustomerRoute><CustomerLayout><CustomerDashboard /></CustomerLayout></CustomerRoute>} />
          <Route path="/customer/cart" element={<CustomerRoute><CustomerLayout><CartPage /></CustomerLayout></CustomerRoute>} />
          <Route path="/checkout" element={<CustomerRoute><CustomerLayout><Checkout /></CustomerLayout></CustomerRoute>} />
          <Route path="/customer/my-orders" element={<CustomerRoute><CustomerLayout><CustomerOrders /></CustomerLayout></CustomerRoute>} />
          <Route path="/customer/profile" element={<CustomerRoute><CustomerLayout><CustomerProfile /></CustomerLayout></CustomerRoute>} />

          {/* ADMIN */}
          <Route path="/admin/dashboard" element={<PrivateRoute><AdminLayout><AdminDashboard /></AdminLayout></PrivateRoute>} />
          <Route path="/admin/products" element={<PrivateRoute><AdminLayout><ProductList /></AdminLayout></PrivateRoute>} />
          <Route path="/admin/orders" element={<PrivateRoute><AdminLayout><AdminOrders /></AdminLayout></PrivateRoute>} />
          <Route path="/admin/add-product" element={<PrivateRoute><AdminLayout><AddProduct /></AdminLayout></PrivateRoute>} />
          <Route path="/admin/edit/:id" element={<PrivateRoute><AdminLayout><EditProduct /></AdminLayout></PrivateRoute>} />
        </Routes>

        <ToastContainer position="top-right" autoClose={2000} theme="dark" pauseOnHover />
      </BrowserRouter>
    </SearchProvider>
  );
}

export default App;
