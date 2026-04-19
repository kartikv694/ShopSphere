import { useState } from "react";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

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

   toast.error("Server Error ⚠");

  }

 };

 return(

  <div>

   <h2>Register</h2>

   <form onSubmit={handleSubmit}>

    <input
     name="name"
     placeholder="Enter Name"
     value={user.name}
     onChange={handleChange}
     required
    />

    <br/><br/>

    <input
     name="email"
     placeholder="Enter Email"
     value={user.email}
     onChange={handleChange}
     required
    />

    <br/><br/>

    <input
     name="password"
     type="password"
     placeholder="Enter Password"
     value={user.password}
     onChange={handleChange}
     required
    />

    <br/><br/>

    <label>Select Role:</label>

    <br/>

    <select
     name="role"
     value={user.role}
     onChange={handleChange}
    >

     <option value="CUSTOMER">Customer</option>

     <option value="SELLER">Seller</option>

    </select>

    <br/><br/>

    <button>Register</button>

   </form>

   <ToastContainer/>

  </div>

 );

}

export default Register;