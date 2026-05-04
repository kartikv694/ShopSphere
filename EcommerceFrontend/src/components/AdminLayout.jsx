import Navbar from "./Navbar";
import Sidebar from "./Sidebar";
import { useState } from "react";
import React from "react";
import "./AdminLayout.css";

const AdminLayout = ({ children }) => {
  const [search, setSearch] = useState("");
  const [category, setCategory] = useState("");

  return (
    <div className="admin-layout">
      <Navbar setSearch={setSearch} setCategory={setCategory} />

      <div className="admin-body">
        <Sidebar />

        <div className="admin-content">
          {React.isValidElement(children)
            ? React.cloneElement(children, { search, category })
            : children}
        </div>
      </div>
    </div>
  );
};

export default AdminLayout;