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

import {
  SearchProvider
} from "./components/SearchContext";

import PrivateRoute from "./components/PrivateRoute";

import CustomerRoute from "./components/CustomerRoute";

import CartPage from "./components/CartPage";

import Checkout from "./components/Checkout";

import CustomerOrders from "./components/customer/CustomerOrders";

import "./App.css";

/* LAYOUTS */

import AdminLayout from "./components/AdminLayout";

import CustomerLayout from "./components/CustomerLayout";

function App() {

  return (

    <SearchProvider>

      <BrowserRouter>

        <Routes>

          {/* AUTH */}

          <Route
            path="/login"
            element={<Login />}
          />

          <Route
            path="/register"
            element={<Register />}
          />

          {/* CUSTOMER PUBLIC */}

          <Route
            path="/"
            element={
              <CustomerLayout>

                <Home />

              </CustomerLayout>
            }
          />

          <Route
            path="/customer/products"
            element={
              <CustomerLayout>

                <Products />

              </CustomerLayout>
            }
          />

          <Route
            path="/customer/products/:id"
            element={
              <CustomerLayout>

                <ProductDetails />

              </CustomerLayout>
            }
          />

          {/* CUSTOMER PROTECTED */}

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
            path="/checkout"
            element={
              <CustomerRoute>

                <CustomerLayout>

                  <Checkout />

                </CustomerLayout>

              </CustomerRoute>
            }
          />

          <Route
            path="/customer/my-orders"
            element={
              <CustomerRoute>

                <CustomerLayout>

                  <CustomerOrders />

                </CustomerLayout>

              </CustomerRoute>
            }
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

        {/* TOAST */}

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