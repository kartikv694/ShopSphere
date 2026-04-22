import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

function Dashboard() {

  const [message, setMessage] = useState("");
  const navigate = useNavigate();

  useEffect(() => {

    const fetchProtectedData = async () => {
      try {

        const token = localStorage.getItem("token");

        const response = await fetch(
          "http://localhost:8080/api/welcome",
          {
            method: "GET",
            headers: {
              "Authorization": `Bearer ${token}`
            }
          }
        );

        if (response.ok) {
          const data = await response.text();
          setMessage(data);
        } else {
          // invalid token → redirect
          localStorage.removeItem("token");
          navigate("/login");
        }

      } catch (error) {
        console.log(error);
      }
    };

    fetchProtectedData();

  }, [navigate]);

  const handleLogout = () => {
    localStorage.removeItem("token");
    navigate("/login");
  };

  return (
    <div style={{ textAlign: "center", marginTop: "50px" }}>

      <h1>Dashboard 🔐</h1>

      <h3>{message}</h3>

      <button onClick={handleLogout}>Logout</button>

    </div>
  );
}

export default Dashboard;