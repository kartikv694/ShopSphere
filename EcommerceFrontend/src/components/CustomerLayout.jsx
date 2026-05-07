import React, { useState } from "react";
import CustomerNavbar from "./CustomerNavbar";
import CustomerSubNavbar from "./CustomerSubNavbar";

function CustomerLayout({ children }) {

  const [selectedCategory, setSelectedCategory] = useState("All");

  return (

    <div>

      <CustomerNavbar />

      <CustomerSubNavbar
        setSelectedCategory={setSelectedCategory}
      />

      <div
        style={{
          width: "100%",
          marginTop: "20px"

        }}
      >

        {React.cloneElement(children, { selectedCategory })}

      </div>

    </div>

  );
}

export default CustomerLayout;