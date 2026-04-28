import { useState } from "react";
import { ToastContainer, toast } from "react-toastify";
import { Link, useNavigate } from "react-router-dom";
import "./Auth.css";
import "react-toastify/dist/ReactToastify.css";

function Login(){

 const [loginData,setLoginData] = useState({
  email:"",
  password:""
 });

 const navigate = useNavigate();

 const handleChange = (e) =>{

  setLoginData({
   ...loginData,
   [e.target.name]: e.target.value
  });

 };

 const handleSubmit = async (e) => {
  e.preventDefault();

  try {
    const response = await fetch(
      "http://localhost:8080/api/auth/login",
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(loginData)
      }
    );

    if (response.ok) {

     const data = await response.json();

      // store token + role
      localStorage.setItem("token", data.token);
      localStorage.setItem("role", data.role);

      toast.success("Login Successful 🎉");

      setLoginData({
        email: "",
        password: ""
      });

      console.log("ROLE FROM BACKEND:", data.role);

      // role-based redirect (temporary)
      setTimeout(() => {
        if (data.role === "ADMIN") {
          navigate("/admin-dashboard");
        } else {
          navigate("/customer"); 
        }
      }, 1000);

    } else {
      toast.error("Invalid email or password ❌");
    }

  } catch (error) {
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