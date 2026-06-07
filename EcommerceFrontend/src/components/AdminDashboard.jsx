import { useEffect, useMemo, useRef, useState } from "react";
import axios from "axios";
import "./Dashboard.css";
import { API_BASE_URL, getAuthHeaders } from "../utils/auth";

const months = [
  "Jan",
  "Feb",
  "Mar",
  "Apr",
  "May",
  "Jun",
  "Jul",
  "Aug",
  "Sep",
  "Oct",
  "Nov",
  "Dec"
];

const createMonthlyData = () =>
  months.map((month, index) => ({
    month,
    pageViews: 1200 + index * 260,
    sessions: 650 + index * 130,
    orders: 45 + index * 11,
    newUsers: 24 + index * 7
  }));

const defaultChartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      labels: {
        boxWidth: 12,
        color: "#475569",
        font: {
          size: 12,
          weight: "600"
        }
      }
    },
    tooltip: {
      backgroundColor: "#111827",
      borderColor: "#374151",
      borderWidth: 1,
      padding: 12,
      titleColor: "#d1d5db",
      bodyColor: "#ffffff"
    }
  },
  scales: {
    x: {
      grid: {
        display: false
      },
      ticks: {
        color: "#94a3b8"
      }
    },
    y: {
      beginAtZero: true,
      grid: {
        color: "#eef2f7"
      },
      ticks: {
        color: "#94a3b8"
      }
    }
  }
};

function DashboardChart({ config }) {
  const canvasRef = useRef(null);

  useEffect(() => {
    if (!canvasRef.current) {
      return undefined;
    }

    let chart;
    let isMounted = true;

    import("chart.js/auto").then((chartModule) => {
      if (!isMounted || !canvasRef.current) {
        return;
      }

      chart = new chartModule.default(canvasRef.current, config);
    });

    return () => {
      isMounted = false;

      if (chart) {
        chart.destroy();
      }
    };
  }, [config]);

  return (
    <div className="chart-canvas-wrap">
      <canvas ref={canvasRef} />
    </div>
  );
}

function AdminDashboard() {
  const [totalUsers, setTotalUsers] = useState(0);
  const [totalProducts, setTotalProducts] = useState(0);
  const [loading, setLoading] = useState(true);

  const chartData = useMemo(() => createMonthlyData(), []);

  useEffect(() => {
    const loadDashboard = async () => {
      try {
        const response = await axios.get(`${API_BASE_URL}/api/dashboard`, {
          headers: getAuthHeaders()
        });

        setTotalUsers(response.data.totalUsers || 0);
        setTotalProducts(response.data.totalProducts || 0);
      } catch (error) {
        console.log(error);
      } finally {
        setLoading(false);
      }
    };

    const loadId = setTimeout(loadDashboard, 0);

    return () => clearTimeout(loadId);
  }, []);

  const totalOrders = chartData.reduce((sum, item) => sum + item.orders, 0);
  const averageSessions = Math.floor(
    chartData.reduce((sum, item) => sum + item.sessions, 0) / chartData.length
  );

  const labels = chartData.map((item) => item.month);

  const engagementChart = useMemo(
    () => ({
      type: "line",
      data: {
        labels,
        datasets: [
          {
            label: "Page Views",
            data: chartData.map((item) => item.pageViews),
            borderColor: "#2563eb",
            backgroundColor: "rgba(37, 99, 235, 0.12)",
            borderWidth: 3,
            fill: true,
            tension: 0.35,
            pointRadius: 3
          },
          {
            label: "Sessions",
            data: chartData.map((item) => item.sessions),
            borderColor: "#059669",
            backgroundColor: "rgba(5, 150, 105, 0.1)",
            borderWidth: 3,
            fill: true,
            tension: 0.35,
            pointRadius: 3
          }
        ]
      },
      options: defaultChartOptions
    }),
    [chartData, labels]
  );

  const registrationsChart = useMemo(
    () => ({
      type: "bar",
      data: {
        labels,
        datasets: [
          {
            label: "New Users",
            data: chartData.map((item) => item.newUsers),
            backgroundColor: "#7c3aed",
            borderRadius: 8
          }
        ]
      },
      options: defaultChartOptions
    }),
    [chartData, labels]
  );

  const ordersChart = useMemo(
    () => ({
      type: "line",
      data: {
        labels,
        datasets: [
          {
            label: "Orders",
            data: chartData.map((item) => item.orders),
            borderColor: "#d97706",
            backgroundColor: "rgba(217, 119, 6, 0.12)",
            borderWidth: 3,
            fill: true,
            tension: 0.35,
            pointRadius: 4
          }
        ]
      },
      options: defaultChartOptions
    }),
    [chartData, labels]
  );

  const stats = [
    {
      label: "Total Users",
      value: totalUsers,
      icon: "Users",
      color: "#2563eb",
      bg: "#eff6ff"
    },
    {
      label: "Total Products",
      value: totalProducts,
      icon: "Items",
      color: "#059669",
      bg: "#ecfdf5"
    },
    {
      label: "Total Orders",
      value: totalOrders,
      icon: "Orders",
      color: "#d97706",
      bg: "#fffbeb"
    },
    {
      label: "Avg Sessions/Mo",
      value: averageSessions,
      icon: "Visits",
      color: "#7c3aed",
      bg: "#f5f3ff"
    }
  ];

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <div>
          <h1 className="dashboard-title">Admin Dashboard</h1>
          <p className="dashboard-subtitle">
            Store performance, customer activity, and order trends.
          </p>
        </div>
        <div className="dashboard-date">
          {new Date().toLocaleDateString("en-IN", {
            weekday: "long",
            year: "numeric",
            month: "long",
            day: "numeric"
          })}
        </div>
      </div>

      <div className="stat-cards">
        {stats.map((stat) => (
          <div
            className="stat-card"
            key={stat.label}
          >
            <div
              className="stat-icon"
              style={{
                background: stat.bg,
                color: stat.color
              }}
            >
              {stat.icon}
            </div>
            <div className="stat-info">
              <p className="stat-label">{stat.label}</p>
              <h2
                className="stat-value"
                style={{ color: stat.color }}
              >
                {loading ? "--" : stat.value.toLocaleString()}
              </h2>
            </div>
          </div>
        ))}
      </div>

      <div className="charts-row">
        <div className="chart-card wide">
          <div className="chart-header">
            <div>
              <h3>User Engagement</h3>
              <p>Monthly page views and sessions across the store</p>
            </div>
          </div>
          <DashboardChart config={engagementChart} />
        </div>
      </div>

      <div className="charts-row">
        <div className="chart-card">
          <div className="chart-header">
            <div>
              <h3>New Registrations</h3>
              <p>Users registered per month</p>
            </div>
          </div>
          <DashboardChart config={registrationsChart} />
        </div>

        <div className="chart-card">
          <div className="chart-header">
            <div>
              <h3>Monthly Orders</h3>
              <p>Order volume trend</p>
            </div>
          </div>
          <DashboardChart config={ordersChart} />
        </div>
      </div>
    </div>
  );
}

export default AdminDashboard;
