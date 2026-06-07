import { Link, useParams } from "react-router-dom";
import "./Customer.css";

const pageContent = {
  "about-us": {
    title: "About Us",
    subtitle: "ShopSphere brings products, payments, orders, and support into one smooth shopping experience.",
    points: [
<<<<<<< HEAD
      "Curated electronics, fashion, accessories, toys, and daily essentials.",
=======
       "Curated electronics, foods, beauty products, toys, and daily essentials.",
>>>>>>> bd2e608 (Project Completed Deployment Pending)
      "Secure checkout with multiple payment options.",
      "Order tracking from placement to delivery."
    ]
  },
  careers: {
    title: "Careers",
    subtitle: "Build customer-first shopping tools with the ShopSphere team.",
    points: [
      "Frontend, backend, operations, and support roles.",
      "Product-focused work across ecommerce features.",
      "A practical environment for learning and shipping."
    ]
  },
  press: {
    title: "Press Releases",
    subtitle: "Latest announcements and updates from ShopSphere.",
    points: [
      "New shopping features and service updates.",
      "Payment and delivery improvements.",
      "Product catalog and partner announcements."
    ]
  },
  services: {
    title: "Our Services",
    subtitle: "Everything customers need before, during, and after purchase.",
    points: [
      "Product browsing and recommendations.",
      "Cart, checkout, and secure payments.",
      "Order history, tracking, returns, and support."
    ]
  },
  instagram: {
    title: "Instagram",
    subtitle: "Follow ShopSphere for product highlights and offer updates.",
    points: [
      "Trending products.",
      "Seasonal deals.",
      "Shopping inspiration."
    ]
  },
  facebook: {
    title: "Facebook",
    subtitle: "Connect with ShopSphere customers and announcements.",
    points: [
      "Store updates.",
      "Support announcements.",
      "Community posts."
    ]
  },
  twitter: {
    title: "Twitter",
    subtitle: "Quick updates, launches, and service notices from ShopSphere.",
    points: [
      "Fast announcements.",
      "Offer alerts.",
      "Service status updates."
    ]
  },
  linkedin: {
    title: "LinkedIn",
    subtitle: "Business updates and partnership information from ShopSphere.",
    points: [
      "Company news.",
      "Hiring updates.",
      "Partner opportunities."
    ]
  },
  sell: {
    title: "Sell Products",
    subtitle: "List products on ShopSphere and reach more customers.",
    points: [
      "Add product images and details.",
      "Manage listings from the admin panel.",
      "Track orders and fulfilment."
    ]
  },
  affiliate: {
    title: "Affiliate Program",
    subtitle: "Partner with ShopSphere and promote selected products.",
    points: [
      "Product promotion opportunities.",
      "Campaign-based collaboration.",
      "Simple partner onboarding."
    ]
  },
  advertise: {
    title: "Advertise Products",
    subtitle: "Promote products to customers browsing relevant categories.",
    points: [
      "Category-focused visibility.",
      "Campaign planning.",
      "Performance-focused placements."
    ]
  },
  partner: {
    title: "Become Partner",
    subtitle: "Work with ShopSphere across products, logistics, or customer service.",
    points: [
      "Seller partnerships.",
      "Service partnerships.",
      "Growth-focused collaboration."
    ]
  },
  account: {
    title: "Your Account",
    subtitle: "Manage profile details, password, orders, and saved addresses.",
    points: [
      "Update profile information.",
      "Change password securely.",
      "Review order history."
    ],
    cta: "/customer/profile",
    ctaLabel: "Open Account"
  },
  returns: {
    title: "Returns Centre",
    subtitle: "Get help with returns, refunds, and order issues.",
    points: [
      "Review orders eligible for return.",
      "Check refund status.",
      "Contact customer service for assistance."
    ],
    cta: "/customer/my-orders",
    ctaLabel: "View Orders"
  },
  help: {
    title: "Help",
    subtitle: "Find support for orders, delivery, payments, and account settings.",
    points: [
      "Order and delivery support.",
      "Payment help.",
      "Account assistance."
    ],
    cta: "/customer/service",
    ctaLabel: "Customer Service"
  },
  support: {
    title: "24x7 Support",
    subtitle: "ShopSphere support is available for urgent customer issues.",
    points: [
      "Order tracking help.",
      "Payment issue support.",
      "Account and profile assistance."
    ],
    cta: "/customer/service",
    ctaLabel: "Get Support"
  }
};

function CustomerInfoPage() {
  const { slug } = useParams();
  const page = pageContent[slug] || pageContent["about-us"];

  return (
    <div className="info-page">
      <div className="info-panel">
        <h1>{page.title}</h1>
        <p>{page.subtitle}</p>

        <div className="info-points">
          {page.points.map((point) => (
            <div
              className="info-point"
              key={point}
            >
              <span />
              <p>{point}</p>
            </div>
          ))}
        </div>

        {page.cta && (
          <Link
            className="info-cta"
            to={page.cta}
          >
            {page.ctaLabel}
          </Link>
        )}
      </div>
    </div>
  );
}

export default CustomerInfoPage;
