import Navbar from "./Navbar";
import { useState } from "react";


const CustomerLayout = ({ children }) => {
  return (
    <div>
      <Navbar />
      {children}
    </div>
  );
};

export default CustomerLayout;