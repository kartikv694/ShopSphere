import Navbar from "../components/Navbar";
import Sidebar from "../components/Sidebar";
import "./AdminLayout.css";

const AdminLayout = ({ children }) => {
  return (
    <div className="admin-layout">
      
      {/* Top Navbar */}
      <Navbar />

      {/* Main Layout */}
      <div className="admin-body">
        
        {/* Sidebar */}
        <Sidebar />

        {/* Content Area */}
        <div className="admin-content">
          {children}
        </div>

      </div>
    </div>
  );
};

export default AdminLayout;