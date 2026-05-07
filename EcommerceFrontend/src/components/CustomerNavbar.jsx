import { useState, useEffect } from "react";
import "./Navbar.css";
import axios from "axios";
import { useNavigate } from "react-router-dom";

import {
  FaSearch,
  FaUser,
  FaShoppingBag,
  FaMapMarkerAlt,
} from "react-icons/fa";

/* MAP */
import {
  MapContainer,
  TileLayer,
  Marker,
  useMapEvents,
} from "react-leaflet";

import "leaflet/dist/leaflet.css";

/* ================= MAP CLICK ================= */
function LocationPicker({ setCoords, getAddressFromCoords }) {

  useMapEvents({

    click(e) {

      const { lat, lng } = e.latlng;

      setCoords(e.latlng);

      getAddressFromCoords(lat, lng);

    },

  });

  return null;
}

function CustomerNavbar({ setProducts }) {

  const navigate = useNavigate();

  const [showLocation, setShowLocation] = useState(false);

  const [placeName, setPlaceName] = useState("");

  const [address, setAddress] = useState("");

  const [coords, setCoords] = useState(null);

  const [cartCount, setCartCount] = useState(0);

  const savedLocation =
    JSON.parse(localStorage.getItem("location"));

  /* ================= CART COUNT ================= */
  useEffect(() => {

    const updateCartCount = () => {

      const cart =
        JSON.parse(localStorage.getItem("cart")) || [];

      let totalQuantity = 0;

      cart.forEach((item) => {

        totalQuantity += item.quantity || 1;

      });

      setCartCount(totalQuantity);

    };

    // INITIAL LOAD
    updateCartCount();

    // EVENT LISTENER
    window.addEventListener(
      "cartUpdated",
      updateCartCount
    );

    return () => {

      window.removeEventListener(
        "cartUpdated",
        updateCartCount
      );

    };

  }, []);

  /* ================= CATEGORY FILTER ================= */
  const handleCategoryClick = async (category) => {

    try {

      const res = await axios.get(
        `http://localhost:8080/api/products/category/${category}`
      );

      console.log(res.data);

      setProducts(res.data);

    } catch (err) {

      console.log(err);

    }

  };

  /* ================= GET ADDRESS ================= */
  const getAddressFromCoords = async (lat, lon) => {

    try {

      const res = await fetch(
        `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lon}`
      );

      const data = await res.json();

      setAddress(
        data.display_name || "Address not found"
      );

    } catch (err) {

      console.error(err);

    }

  };

  /* ================= CURRENT LOCATION ================= */
  const handleUseCurrentLocation = () => {

    navigator.geolocation.getCurrentPosition((pos) => {

      const lat = pos.coords.latitude;

      const lng = pos.coords.longitude;

      setCoords({ lat, lng });

      getAddressFromCoords(lat, lng);

    });

  };

  /* ================= SAVE LOCATION ================= */
  const handleSave = () => {

    const loc = {

      name: placeName || "Current Location",

      address: address,

      lat: coords?.lat,

      lng: coords?.lng,

    };

    localStorage.setItem(
      "location",
      JSON.stringify(loc)
    );

    setShowLocation(false);

    window.location.reload();

  };

  return (
    <>
      <div className="navbar customer-navbar">

        {/* LEFT */}
        <div className="nav-left">

          <h2 className="logo">
            ShopSphere
          </h2>

          <div
            className="address-box"
            onClick={() => setShowLocation(true)}
          >

            <FaMapMarkerAlt className="location-icon" />

            <div className="address">

              <span className="deliver">
                Deliver to
              </span>

              <span className="name">
                {savedLocation?.name ||
                  "Set Location"}
              </span>

            </div>

          </div>

        </div>

        {/* CENTER */}
        <div className="nav-center">

          <div className="search-bar">

            <input
              type="text"
              placeholder="Search products..."
              className="search-input"
            />

            <button className="search-btn">

              <FaSearch />

            </button>

          </div>

        </div>

        {/* RIGHT */}
        <div className="nav-right">

          <FaUser className="icon" />

          {/* CART */}
          <div
            className="bag-container"
            onClick={() => navigate("/customer/cart")}
            style={{ cursor: "pointer" }}
          >

            <FaShoppingBag className="bag-icon" />

            <span className="bag-count">
              {cartCount}
            </span>

          </div>

        </div>

      </div>

      {/* ================= LOCATION MODAL ================= */}
      {showLocation && (

        <div className="location-modal">

          <div className="location-box">

            {/* LEFT FORM */}
            <div className="location-form">

              <h3>
                Set Delivery Location
              </h3>

              <input
                type="text"
                placeholder="Place name (Home, Office)"
                value={placeName}
                onChange={(e) =>
                  setPlaceName(e.target.value)
                }
              />

              <input
                type="text"
                placeholder="Full Address"
                value={address}
                onChange={(e) =>
                  setAddress(e.target.value)
                }
              />

              <button
                onClick={handleUseCurrentLocation}
              >
                Use Current Location
              </button>

              <button onClick={handleSave}>
                Save
              </button>

              <button
                onClick={() =>
                  setShowLocation(false)
                }
              >
                Cancel
              </button>

            </div>

            {/* RIGHT MAP */}
            <div className="map-container">

              <MapContainer
                center={[28.61, 77.23]}
                zoom={13}
                style={{
                  height: "100%",
                  width: "100%"
                }}
              >

                <TileLayer
                  url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                />

                {coords && (
                  <Marker position={coords} />
                )}

                <LocationPicker
                  setCoords={setCoords}
                  getAddressFromCoords={
                    getAddressFromCoords
                  }
                />

              </MapContainer>

            </div>

          </div>

        </div>

      )}
    </>
  );
}

export default CustomerNavbar;