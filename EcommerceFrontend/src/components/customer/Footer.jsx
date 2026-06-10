import { useNavigate } from "react-router-dom";

const footerSections = [
  {
    title: "Get to Know Us",
    links: [
      { label: "About Us", path: "/customer/info/about-us" },
      { label: "Careers", path: "/customer/info/careers" },
      { label: "Press Releases", path: "/customer/info/press-releases" },
      { label: "Our Services", path: "/customer/service" },
    ],
  },
  {
    title: "Connect With Us",
    links: [
      { label: "Instagram", path: "/customer/info/instagram" },
      { label: "Facebook", path: "/customer/info/facebook" },
      { label: "Twitter", path: "/customer/info/twitter" },
      { label: "LinkedIn", path: "/customer/info/linkedin" },
    ],
  },
  {
    title: "Make Money With Us",
    links: [
      { label: "Sell Products", path: "/customer/info/sell-products" },
      { label: "Affiliate Program", path: "/customer/info/affiliate-program" },
      { label: "Advertise Products", path: "/customer/info/advertise-products" },
      { label: "Become Partner", path: "/customer/info/become-partner" },
    ],
  },
  {
    title: "Let Us Help You",
    links: [
      { label: "Your Account", path: "/customer/profile" },
      { label: "Returns Centre", path: "/customer/info/returns-centre" },
      { label: "Help", path: "/customer/service" },
      { label: "24x7 Support", path: "/customer/service" },
    ],
  },
];

function Footer() {
  const navigate = useNavigate();

  return (
    <footer style={styles.footer}>
      <div style={styles.grid}>
        {footerSections.map((section) => (
          <div key={section.title}>
            <h3 style={styles.heading}>{section.title}</h3>

            {section.links.map((link) => (
              <button
                key={link.label}
                type="button"
                style={styles.link}
                onClick={() => navigate(link.path)}
              >
                {link.label}
              </button>
            ))}
          </div>
        ))}
      </div>

      <div style={styles.bottom}>
        (C) 2026 ShopSphere. All Rights Reserved.
      </div>
    </footer>
  );
}

const styles = {
  footer: {
    background: "#131A22",
    color: "white",
    marginTop: 0,
  },
  grid: {
    borderBottom: "1px solid #3a4553",
    display: "grid",
    gap: "36px",
    gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))",
    padding: "46px clamp(24px, 5vw, 64px)",
  },
  heading: {
    fontSize: "18px",
    margin: "0 0 16px",
  },
  link: {
    background: "transparent",
    border: "none",
    color: "#DDD",
    cursor: "pointer",
    display: "block",
    fontSize: "15px",
    marginBottom: "11px",
    padding: 0,
    textAlign: "left",
  },
  bottom: {
    color: "#DDD",
    fontSize: "14px",
    padding: "22px",
    textAlign: "center",
  },
};

export default Footer;
