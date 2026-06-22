import { useContext, useEffect, useState } from "react";
import "./Navbar.css";
import { useNavigate } from "react-router-dom";
import { logout, getStoredUser } from "../utils/auth";
import { SearchContext } from "./SearchContextValue";
import {
  saveLocation as saveLocationApi,
  syncSavedLocationFromServer,
  getCachedLocation
} from "../utils/userPreferencesApi";

import {
  FaSearch,
  FaUser,
  FaShoppingBag,
  FaMapMarkerAlt,
  FaSignOutAlt,
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

function LocationPicker({
  setCoords,
  getAddressFromCoords
}) {

  useMapEvents({

    click(e) {

      const { lat, lng } =
        e.latlng;

      setCoords(e.latlng);

      getAddressFromCoords(
        lat,
        lng
      );

    },

  });

  return null;

}

function CustomerNavbar() {

  const navigate =
    useNavigate();
  const { setCategory, setSearch } = useContext(SearchContext);
  const [searchText, setSearchText] = useState("");

  const [showLocation,
    setShowLocation] =
      useState(false);

  const [placeName,
    setPlaceName] =
      useState("");

  const [address,
    setAddress] =
      useState("");

  const [coords,
    setCoords] =
      useState(null);

  const [cartCount,
    setCartCount] =
      useState(0);

  const [savedLocation,
    setSavedLocation] =
      useState(getCachedLocation());

  const [showUserMenu,
    setShowUserMenu] =
      useState(false);

  const [user,
    setUser] =
      useState(getStoredUser());

  useEffect(() => {

    const updateUser =
      () => setUser(getStoredUser());

    window.addEventListener(
      "userUpdated",
      updateUser
    );

    return () => {

      window.removeEventListener(
        "userUpdated",
        updateUser
      );

    };

  }, []);

  /* ================= UPDATE LOCATION ================= */

  useEffect(() => {

    const updateLocation =
      () => {

        const latestLocation =
          JSON.parse(

            localStorage.getItem(
              "selectedLocation"
            )

          );

        setSavedLocation(
          latestLocation
        );

      };

    // PULL THE AUTHORITATIVE LOCATION FROM THE SERVER ON MOUNT —
    // this is what makes a location set in another browser appear here.
    syncSavedLocationFromServer().then((loc) => {
      if (loc) setSavedLocation(loc);
    });

    window.addEventListener(
      "locationUpdated",
      updateLocation
    );

    return () => {

      window.removeEventListener(
        "locationUpdated",
        updateLocation
      );

    };

  }, []);

  /* ================= CART COUNT ================= */

  useEffect(() => {

    const updateCartCount =
      () => {

        const cart =
          JSON.parse(

            localStorage.getItem(
              "cart"
            )

          ) || [];

        let totalQuantity = 0;

        cart.forEach((item) => {

          totalQuantity +=
            item.quantity || 1;

        });

        setCartCount(
          totalQuantity
        );

      };

    updateCartCount();

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

  /* ================= GET ADDRESS ================= */

  const getAddressFromCoords =
    async (lat, lon) => {

      try {

        const res =
          await fetch(

            `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lon}`

          );

        const data =
          await res.json();

        setAddress(

          data.display_name ||
          "Address not found"

        );

      } catch (err) {

        console.error(err);

      }

    };

  /* ================= CURRENT LOCATION ================= */

  const handleUseCurrentLocation =
    () => {

      navigator.geolocation.getCurrentPosition(

        (pos) => {

          const lat =
            pos.coords.latitude;

          const lng =
            pos.coords.longitude;

          setCoords({
            lat,
            lng
          });

          getAddressFromCoords(
            lat,
            lng
          );

        }

      );

    };

  /* ================= SAVE LOCATION ================= */

  const handleSave = () => {

  const addressParts =
    address.split(",");

  const city =
    addressParts[
      addressParts.length - 4
    ]?.trim() || "";

  const state =
    addressParts[
      addressParts.length - 3
    ]?.trim() || "";

  const pincode =
    address.match(/\d{6}/)?.[0] || "";

  const loc = {

    fullName:
      placeName ||
      "Current Location",

    fullAddress:
      address || "",

    address:
      address || "",

    city:
      city || "",

    state:
      state || "",

    pincode:
      pincode || "",

    lat:
      coords?.lat || "",

    lng:
      coords?.lng || ""

  };

  // SAVE TO SERVER (cross-device sync) and mirror in localStorage
  saveLocationApi(loc).catch(console.log);

  setSavedLocation(loc);

  setShowLocation(false);

  window.dispatchEvent(

    new Event(
      "locationUpdated"
    )

  );

};

  const handleLogout = async () => {

    await logout();
    setShowUserMenu(false);
    navigate("/");

  };

  const handleSearch = () => {

    setCategory("");
    setSearch(searchText.trim());
    navigate("/customer/products");

  };

  const handleSearchKeyDown = (event) => {

    if (event.key === "Enter") {
      handleSearch();
    }

  };

  return (

    <>

      <div className="navbar customer-navbar">

        {/* LEFT */}

        <div className="nav-left">

          <h2
            className="logo"
            onClick={() =>
              navigate(
                "/customer/dashboard"
              )
            }
            style={{
              cursor: "pointer"
            }}
          >

            ShopSphere

          </h2>

          <div
            className="address-box"
            onClick={() =>
              setShowLocation(true)
            }
          >

            <FaMapMarkerAlt
              className="location-icon"
            />

            <div className="address">

              <span className="deliver">

                Deliver to

              </span>

              <span className="name">

                {

                  savedLocation?.fullName ||

                  "Set Location"

                }

              </span>

              <p className="address-text">

                {

                  savedLocation?.fullAddress ||

                  savedLocation?.address ||

                  ""

                }

              </p>

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
              value={searchText}
              onChange={(event) =>
                setSearchText(event.target.value)
              }
              onKeyDown={handleSearchKeyDown}
            />

            <button
              className="search-btn"
              onClick={handleSearch}
              type="button"
              aria-label="Search products"
            >

              <FaSearch />

            </button>

          </div>

        </div>

        {/* RIGHT */}

        <div className="nav-right">

          <div className="user-menu-wrapper">

            <button
              type="button"
              className="user-menu-btn"
              onClick={() =>
                setShowUserMenu(
                  (current) => !current
                )
              }
            >
              <FaUser className="icon" />
            </button>

            {
              showUserMenu && (

                <div className="user-menu">

                  <p className="user-menu-name">
                    {
                      user?.name ||
                      user?.email?.split("@")[0] ||
                      "Customer"
                    }
                  </p>

                  <button
                    type="button"
                    onClick={() => {

                      setShowUserMenu(false);
                      navigate("/customer/profile");

                    }}
                  >
                    My Profile
                  </button>

                  <button
                    type="button"
                    onClick={() => {

                      setShowUserMenu(false);
                      navigate("/customer/my-orders");

                    }}
                  >
                    My Orders
                  </button>

                  <button
                    type="button"
                    className="user-menu-logout"
                    onClick={handleLogout}
                  >
                    <FaSignOutAlt />
                    Logout
                  </button>

                </div>

              )
            }

          </div>

          {/* CART */}

          <div
            className="bag-container"
            onClick={() =>
              navigate(
                "/customer/cart"
              )
            }
            style={{
              cursor: "pointer"
            }}
          >

            <FaShoppingBag
              className="bag-icon"
            />

            <span className="bag-count">

              {cartCount}

            </span>

          </div>

        </div>

      </div>

      {/* ================= LOCATION MODAL ================= */}

      {

        showLocation && (

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
                    setPlaceName(
                      e.target.value
                    )
                  }
                />

                <input
                  type="text"
                  placeholder="Full Address"
                  value={address}
                  onChange={(e) =>
                    setAddress(
                      e.target.value
                    )
                  }
                />

                <button
                  onClick={
                    handleUseCurrentLocation
                  }
                >

                  Use Current Location

                </button>

                <button
                  onClick={handleSave}
                >

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

                  {

                    coords && (

                      <Marker
                        position={coords}
                      />

                    )

                  }

                  <LocationPicker
                    setCoords={
                      setCoords
                    }
                    getAddressFromCoords={
                      getAddressFromCoords
                    }
                  />

                </MapContainer>

              </div>

            </div>

          </div>

        )

      }

    </>

  );

}

export default CustomerNavbar;
