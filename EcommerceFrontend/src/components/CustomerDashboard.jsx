import { useEffect, useState } from "react";

function CustomerDashboard() {
  const [location, setLocation] = useState(null);

  useEffect(() => {
    const savedLocation = localStorage.getItem("location");

    if (savedLocation) {
      setLocation(JSON.parse(savedLocation));
    }
  }, []);

  // 🚨 IF NO LOCATION → SHOW SETUP SCREEN
  if (!location) {
    return (
      <div style={{ padding: "20px", color: "white" }}>
        <h2>Set Your Delivery Location</h2>
        <button onClick={() => {
          navigator.geolocation.getCurrentPosition((pos) => {
            const loc = {
              lat: pos.coords.latitude,
              lng: pos.coords.longitude,
              address: "Auto detected"
            };
            localStorage.setItem("location", JSON.stringify(loc));
            window.location.reload();
          });
        }}>
          Use Current Location
        </button>
      </div>
    );
  }

  // ✅ NORMAL DASHBOARD
  return (
    <div>
      {/* products will come here later */}
    </div>
  );
}

export default CustomerDashboard;