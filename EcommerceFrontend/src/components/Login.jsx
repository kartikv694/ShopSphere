import { useState } from "react";
import { ToastContainer, toast } from "react-toastify";
import { Link } from "react-router-dom";
import "./Auth.css";
import "react-toastify/dist/ReactToastify.css";

function Login(){

 const [loginData,setLoginData] = useState({
  email:"",
  password:""
 });

 const handleChange = (e) =>{

  setLoginData({
   ...loginData,
   [e.target.name]: e.target.value
  });

 };

 const handleSubmit = async (e) =>{

  e.preventDefault();

  try{

   const response = await fetch(
    "http://localhost:8080/api/auth/login",
    {
     method:"POST",
     headers:{
      "Content-Type":"application/json"
     },
     body: JSON.stringify(loginData)
    }
   );

   if(response.ok){

    const token = await response.text();

    console.log("JWT Token:", token);

    localStorage.setItem("token", token);

    toast.success("Login Successful 🎉");

    setLoginData({
     email:"",
     password:""
    });

   }
   else{

    toast.error("Invalid email or password ❌");

   }

  }
  catch(error){

   console.log(error);
   toast.error("Server Error ⚠");

  }

 };

 return(

  <>
  
  <div className="auth-container">

   <div className="auth-box">

    <h2>Login</h2>

    <form onSubmit={handleSubmit}>

     <input
      name="email"
      placeholder="Enter Email"
      value={loginData.email}
      onChange={handleChange}
      required
     />

     <input
      name="password"
      type="password"
      placeholder="Enter Password"
      value={loginData.password}
      onChange={handleChange}
      required
     />

     <button>Login</button>

    </form>

    <p>
     Don't have an account?
     <Link to="/register"> Register</Link>
    </p>

   </div>

  </div>

  <ToastContainer
   position="top-right"
   autoClose={4000}
   newestOnTop
   closeOnClick
   pauseOnHover
  />

  </>

 );

}

export default Login;