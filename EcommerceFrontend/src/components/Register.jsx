import { useState } from "react";
import { ToastContainer, toast } from "react-toastify";
import { Link } from "react-router-dom";   
import "react-toastify/dist/ReactToastify.css";
import "./auth.css";

function Register(){

 const [user,setUser] = useState({
  name:"",
  email:"",
  password:"",
  role:"CUSTOMER"
 });

 const handleChange = (e) =>{

  setUser({
   ...user,
   [e.target.name]: e.target.value
  });

 };

 const handleSubmit = async (e) =>{

  e.preventDefault();

  try{

   const response = await fetch(
    "http://localhost:8080/api/auth/register",
    {
     method:"POST",
     headers:{
      "Content-Type":"application/json"
     },
     body: JSON.stringify(user)
    }
   );

   if(response.ok){

    toast.success("Registered Successfully 🎉");

    setUser({
     name:"",
     email:"",
     password:"",
     role:"CUSTOMER"
    });

   }
   else{

    toast.error("Registration Failed ❌");

   }

  }
  catch(error){

   console.log(error);
   toast.error("Server Error ⚠");

  }

 };

 return (

  <>

  <div className="auth-container">

    <div className="auth-box">

      <h2>Register</h2>

      <form onSubmit={handleSubmit}>

        <input
         name="name"
         placeholder="Enter Name"
         value={user.name}
         onChange={handleChange}
         required
        />

        <input
         name="email"
         placeholder="Enter Email"
         value={user.email}
         onChange={handleChange}
         required
        />

        <input
         name="password"
         type="password"
         placeholder="Enter Password"
         value={user.password}
         onChange={handleChange}
         required
        />

        <select
         name="role"
         value={user.role}
         onChange={handleChange}
        >
          <option value="CUSTOMER">Customer</option>
          <option value="SELLER">Seller</option>
        </select>

        <button>Register</button>

      </form>

      <p>
        Already have an account?
        <Link to="/login"> Login</Link>
      </p>

    </div>

  </div>

  {/* Toast outside container */}
  <ToastContainer
   position="top-right"
   autoClose={2000}
   newestOnTop
   closeOnClick
   pauseOnHover
  />

  </>

 );

}

export default Register;