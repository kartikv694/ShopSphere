import CustomerNavbar from "./CustomerNavbar";
import CustomerSubNavbar from "./CustomerSubNavbar";

const CustomerLayout = ({ children }) => {
  return (
    <>
      <CustomerNavbar />
      <CustomerSubNavbar/>
      {children}
    </>
  );
};

export default CustomerLayout;