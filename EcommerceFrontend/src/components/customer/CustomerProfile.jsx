import { useEffect, useState } from "react";
import axios from "axios";
import { toast } from "react-toastify";
import {
  API_BASE_URL,
  getAuthHeaders,
  getStoredUser,
  updateStoredUser
} from "../../utils/auth";
import "./Customer.css";

function CustomerProfile() {
  const storedUser = getStoredUser();

  const [loading, setLoading] = useState(true);
  const [savingProfile, setSavingProfile] = useState(false);
  const [savingPassword, setSavingPassword] = useState(false);

  const [profile, setProfile] = useState({
    name: storedUser?.name || "",
    email: storedUser?.email || ""
  });

  const [passwordData, setPasswordData] = useState({
    currentPassword: "",
    newPassword: "",
    confirmPassword: ""
  });

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const response = await axios.get(
          `${API_BASE_URL}/api/user/profile`,
          {
            headers: getAuthHeaders()
          }
        );

        setProfile({
          name: response.data.name || "",
          email: response.data.email || ""
        });

        updateStoredUser(response.data);
      } catch (error) {
        console.log(error);
        toast.error("Unable to load profile");
      } finally {
        setLoading(false);
      }
    };

    fetchProfile();
  }, []);

  const handleProfileChange = (event) => {
    setProfile({
      ...profile,
      [event.target.name]: event.target.value
    });
  };

  const handlePasswordChange = (event) => {
    setPasswordData({
      ...passwordData,
      [event.target.name]: event.target.value
    });
  };

  const handleProfileSubmit = async (event) => {
    event.preventDefault();
    setSavingProfile(true);

    try {
      const response = await axios.put(
        `${API_BASE_URL}/api/user/profile`,
        profile,
        {
          headers: getAuthHeaders()
        }
      );

      setProfile({
        name: response.data.name || "",
        email: response.data.email || ""
      });

      updateStoredUser(response.data);
      if (response.data.token) {
        localStorage.setItem("token", response.data.token);
      }
      toast.success("Profile updated successfully");
    } catch (error) {
      console.log(error);
      toast.error(
        error.response?.data?.message ||
          error.response?.data ||
          "Profile update failed"
      );
    } finally {
      setSavingProfile(false);
    }
  };

  const handlePasswordSubmit = async (event) => {
    event.preventDefault();

    if (passwordData.newPassword !== passwordData.confirmPassword) {
      toast.error("New passwords do not match");
      return;
    }

    if (passwordData.newPassword.length < 4) {
      toast.error("Password must be at least 4 characters");
      return;
    }

    setSavingPassword(true);

    try {
      await axios.put(
        `${API_BASE_URL}/api/user/password`,
        {
          currentPassword: passwordData.currentPassword,
          newPassword: passwordData.newPassword
        },
        {
          headers: getAuthHeaders()
        }
      );

      setPasswordData({
        currentPassword: "",
        newPassword: "",
        confirmPassword: ""
      });

      toast.success("Password updated successfully");
    } catch (error) {
      console.log(error);
      toast.error(
        error.response?.data?.message ||
          error.response?.data ||
          "Password update failed"
      );
    } finally {
      setSavingPassword(false);
    }
  };

  if (loading) {
    return (
      <div className="account-page">
        <h1 className="account-title">My Account</h1>
        <div className="account-card">Loading profile...</div>
      </div>
    );
  }

  return (
    <div className="account-page">
      <h1 className="account-title">My Account</h1>

      <div className="account-grid">
        <form
          className="account-card account-form"
          onSubmit={handleProfileSubmit}
        >
          <h2>Profile Details</h2>

          <label>
            Name
            <input
              type="text"
              name="name"
              value={profile.name}
              onChange={handleProfileChange}
              required
            />
          </label>

          <label>
            Email
            <input
              type="email"
              name="email"
              value={profile.email}
              onChange={handleProfileChange}
              required
            />
          </label>

          <button
            type="submit"
            className="account-primary-btn"
            disabled={savingProfile}
          >
            {savingProfile ? "Saving..." : "Update Profile"}
          </button>
        </form>

        <form
          className="account-card account-form"
          onSubmit={handlePasswordSubmit}
        >
          <h2>Change Password</h2>

          <label>
            Current Password
            <input
              type="password"
              name="currentPassword"
              value={passwordData.currentPassword}
              onChange={handlePasswordChange}
              required
            />
          </label>

          <label>
            New Password
            <input
              type="password"
              name="newPassword"
              value={passwordData.newPassword}
              onChange={handlePasswordChange}
              required
            />
          </label>

          <label>
            Confirm New Password
            <input
              type="password"
              name="confirmPassword"
              value={passwordData.confirmPassword}
              onChange={handlePasswordChange}
              required
            />
          </label>

          <button
            type="submit"
            className="account-primary-btn"
            disabled={savingPassword}
          >
            {savingPassword ? "Saving..." : "Update Password"}
          </button>
        </form>
      </div>
    </div>
  );
}

export default CustomerProfile;
