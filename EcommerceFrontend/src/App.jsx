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
import EditProduct from "./components/EditProduct"; 
import { SearchContext, SearchProvider } from "./components/SearchContext";
import PrivateRoute from "./components/PrivateRoute";
import CustomerRoute from "./components/CustomerRoute";
import CartPage from "./components/CartPage";
import "./App.css";

/* Layouts */
import AdminLayout from "./components/AdminLayout";
import CustomerLayout from "./components/CustomerLayout";
import CustomerOrders from "./components/customer/CustomerOrders";

function App() {
  return (
    <SearchProvider>
    <BrowserRouter>
      <Routes>

        {/* NO NAVBAR */}
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        {/* CUSTOMER */}
        <Route path="/" element={<CustomerLayout><Home /></CustomerLayout>} />
        <Route path="/customer/products" element={<CustomerLayout><Products /></CustomerLayout>} />
        <Route path="/customer/products/:id" element={<CustomerLayout><ProductDetails /></CustomerLayout>} />

          <Route
            path="/customer/dashboard"
            element={
              <CustomerRoute>
                <CustomerLayout>
                  <CustomerDashboard />
                </CustomerLayout>
              </CustomerRoute>
            }
          />

          <Route
            path="/customer/category/:category"
            element={
              <CustomerRoute>
                <CustomerLayout>
                  <CustomerDashboard />
                </CustomerLayout>
              </CustomerRoute>
            }
          />

          <Route
            path="/customer/cart"
            element={
              <CustomerRoute>
                <CustomerLayout>
                  <CartPage />
                </CustomerLayout>
              </CustomerRoute>
            }
          />

          <Route
            path="/customer/my-orders"
            element={<CustomerOrders />}
          />

        {/* ADMIN */}
        <Route
          path="/admin/dashboard"
          element={
            <PrivateRoute>
              <AdminLayout>
                <AdminDashboard />
              </AdminLayout>
            </PrivateRoute>
          }
        />

        <Route
          path="/admin/products"
          element={
            <PrivateRoute>
              <AdminLayout>
                <ProductList />
              </AdminLayout>
            </PrivateRoute>
          }
        />

        <Route
          path="/admin/add-product"
          element={
            <PrivateRoute>
              <AdminLayout>
                <AddProduct />
              </AdminLayout>
            </PrivateRoute>
          }
        />

        {/* ✅ NEW EDIT ROUTE */}
        <Route
          path="/admin/edit/:id"
          element={
            <PrivateRoute>
              <AdminLayout>
                <EditProduct />
              </AdminLayout>
            </PrivateRoute>
          }
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
    </SearchProvider>
  );
}

export default App;