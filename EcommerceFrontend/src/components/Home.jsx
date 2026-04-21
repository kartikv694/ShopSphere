import { Link } from "react-router-dom";
import "./home.css";

function Home(){

 return (

  <div className="home-container">

   <div className="home-box">

    <h1>Welcome to ShopSphere 🛍</h1>

    <p>Your one stop ecommerce platform</p>

    <div className="home-buttons">

     <Link to="/login">
      <button className="login-btn">Login</button>
     </Link>

     <Link to="/register">
      <button className="register-btn">Register</button>
     </Link>

    </div>

   </div>

  </div>

 );

}

export default Home;