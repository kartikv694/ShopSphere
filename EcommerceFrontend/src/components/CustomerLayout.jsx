import Navbar from "../components/Navbar";

const CustomerLayout = ({ children }) => {
  return (
    <div>
      <Navbar />   {/* Customer Navbar */}
      {children}
    </div>
  );
};

export default CustomerLayout;