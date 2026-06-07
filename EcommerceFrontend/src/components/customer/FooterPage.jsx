import { useMemo } from "react";
import { useNavigate, useParams } from "react-router-dom";

const pageContent = {
  "about-us": {
    title: "About ShopSphere",
    intro:
      "ShopSphere brings everyday shopping, electronics, accessories, and lifestyle products into one clean customer experience.",
    points: [
      "Curated product collections across high-demand categories.",
      "Secure checkout with supported online payment options.",
      "Order tracking from placement to delivery.",
    ],
  },
  careers: {
    title: "Careers",
    intro:
      "We are building a practical ecommerce platform focused on reliable shopping and smooth operations.",
    points: [
      "Frontend, backend, operations, and customer experience roles.",
      "Growth-focused work across product, service, and platform quality.",
      "Use the support page to contact the team for current openings.",
    ],
  },
  "press-releases": {
    title: "Press Releases",
    intro:
      "Find updates about ShopSphere product launches, service improvements, and business announcements.",
    points: [
      "New category and collection announcements.",
      "Payment, delivery, and customer service updates.",
      "Platform milestones and operational improvements.",
    ],
  },
  instagram: {
    title: "Instagram",
    intro:
      "Follow ShopSphere for new arrivals, offers, and product highlights.",
    points: [
      "Daily product discovery posts.",
      "New release highlights.",
      "Shopping inspiration across categories.",
    ],
  },
  facebook: {
    title: "Facebook",
    intro:
      "Connect with ShopSphere for announcements, product updates, and customer support news.",
    points: [
      "Offer updates and category launches.",
      "Customer help announcements.",
      "Community shopping updates.",
    ],
  },
  twitter: {
    title: "Twitter",
    intro:
      "Get quick ShopSphere updates for launches, service notices, and shopping events.",
    points: [
      "Fast platform announcements.",
      "Order and service update notices.",
      "New collection alerts.",
    ],
  },
  linkedin: {
    title: "LinkedIn",
    intro:
      "Connect with ShopSphere for company updates, partnerships, and professional announcements.",
    points: [
      "Business and platform updates.",
      "Partnership announcements.",
      "Career and hiring notices.",
    ],
  },
  "sell-products": {
    title: "Sell Products",
    intro:
      "Partner with ShopSphere to list products and reach customers through the marketplace.",
    points: [
      "List products with images, descriptions, and pricing.",
      "Manage inventory through admin workflows.",
      "Use order updates to keep customers informed.",
    ],
  },
  "affiliate-program": {
    title: "Affiliate Program",
    intro:
      "Promote ShopSphere products and help customers discover useful items.",
    points: [
      "Promote curated product collections.",
      "Share category and product recommendations.",
      "Contact support for affiliate onboarding.",
    ],
  },
  "advertise-products": {
    title: "Advertise Products",
    intro:
      "Showcase your products through ShopSphere category pages and promotional collections.",
    points: [
      "Highlight bestsellers and new releases.",
      "Reach customers browsing by category.",
      "Coordinate product campaigns with the ShopSphere team.",
    ],
  },
  "become-partner": {
    title: "Become Partner",
    intro:
      "Work with ShopSphere as a product, logistics, service, or technology partner.",
    points: [
      "Support product availability and customer delivery.",
      "Build marketplace and service partnerships.",
      "Use the support page to start a partnership request.",
    ],
  },
  "returns-centre": {
    title: "Returns Centre",
    intro:
      "Start return-related support for eligible orders from your order history or customer service page.",
    points: [
      "Open your order details before requesting a return.",
      "Keep product condition and order information ready.",
      "Support will guide the next steps based on the order status.",
    ],
  },
};

function FooterPage() {
  const { slug } = useParams();
  const navigate = useNavigate();

  const content = useMemo(
    () => pageContent[slug] || pageContent["about-us"],
    [slug]
  );

  return (
    <main style={styles.page}>
      <section style={styles.hero}>
        <p style={styles.eyebrow}>ShopSphere</p>
        <h1 style={styles.title}>{content.title}</h1>
        <p style={styles.intro}>{content.intro}</p>
      </section>

      <section style={styles.panel}>
        {content.points.map((point) => (
          <div key={point} style={styles.row}>
            <span style={styles.marker}></span>
            <p style={styles.text}>{point}</p>
          </div>
        ))}
      </section>

      <div style={styles.actions}>
        <button
          type="button"
          style={styles.primaryButton}
          onClick={() => navigate("/customer/products")}
        >
          Browse Products
        </button>
        <button
          type="button"
          style={styles.secondaryButton}
          onClick={() => navigate("/customer/service")}
        >
          Contact Support
        </button>
      </div>
    </main>
  );
}

const styles = {
  page: {
    background: "#f4f6f8",
    color: "#111827",
    minHeight: "60vh",
    padding: "42px clamp(22px, 5vw, 72px) 70px",
  },
  hero: {
    maxWidth: "780px",
  },
  eyebrow: {
    color: "#0f766e",
    fontSize: "14px",
    fontWeight: 800,
    letterSpacing: "0.08em",
    margin: "0 0 10px",
    textTransform: "uppercase",
  },
  title: {
    fontSize: "clamp(34px, 5vw, 56px)",
    fontWeight: 850,
    lineHeight: 1.05,
    margin: "0 0 18px",
  },
  intro: {
    color: "#374151",
    fontSize: "18px",
    lineHeight: 1.65,
    margin: 0,
  },
  panel: {
    background: "#fff",
    border: "1px solid #e5e7eb",
    borderRadius: "8px",
    boxShadow: "0 10px 28px rgba(15,23,42,0.08)",
    display: "grid",
    gap: "16px",
    marginTop: "34px",
    maxWidth: "860px",
    padding: "26px",
  },
  row: {
    alignItems: "flex-start",
    display: "flex",
    gap: "12px",
  },
  marker: {
    background: "#ffd814",
    borderRadius: "50%",
    flex: "0 0 10px",
    height: "10px",
    marginTop: "9px",
    width: "10px",
  },
  text: {
    color: "#1f2937",
    fontSize: "16px",
    lineHeight: 1.6,
    margin: 0,
  },
  actions: {
    display: "flex",
    flexWrap: "wrap",
    gap: "12px",
    marginTop: "28px",
  },
  primaryButton: {
    background: "#111827",
    border: "none",
    borderRadius: "999px",
    color: "#fff",
    cursor: "pointer",
    fontSize: "15px",
    fontWeight: 800,
    padding: "12px 20px",
  },
  secondaryButton: {
    background: "#ffd814",
    border: "none",
    borderRadius: "999px",
    color: "#111827",
    cursor: "pointer",
    fontSize: "15px",
    fontWeight: 800,
    padding: "12px 20px",
  },
};

export default FooterPage;
