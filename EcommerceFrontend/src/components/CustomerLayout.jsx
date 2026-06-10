import React, { useState } from "react";

import CustomerNavbar from "./CustomerNavbar";
import CustomerSubNavbar from "./CustomerSubNavbar";
import BackToTop from "./customer/BackToTop";
import Footer  from "./customer/Footer";


function CustomerLayout({ children }) {

  const [
    selectedCategory,
    setSelectedCategory
  ] = useState("All");

  return (

    <div
      style={{
        minHeight: "100vh",
        display: "flex",
        flexDirection: "column"
      }}
    >

      {/* NAVBAR */}

      <CustomerNavbar />

      {/* SUB NAVBAR */}

      <CustomerSubNavbar
        setSelectedCategory={
          setSelectedCategory
        }
      />

      {/* PAGE CONTENT */}

      <div
        style={{
          width: "100%",
          marginTop: "20px",
          flex: 1
        }}
      >

        {
          React.cloneElement(
            children,
            {
              selectedCategory
            }
          )
        }

      </div>

      {/* BACK TO TOP */}

      <BackToTop />

      {/* FOOTER */}

      <Footer />

    </div>

  );

}

export default CustomerLayout;