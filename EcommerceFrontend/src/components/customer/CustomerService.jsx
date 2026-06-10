import { Link } from "react-router-dom";
import {
  FaBoxOpen,
  FaCreditCard,
  FaHeadset,
  FaMapMarkerAlt,
  FaRedoAlt,
  FaShoppingBag
} from "react-icons/fa";
import "./Customer.css";

const supportCards = [
  {
    title: "Your Orders",
    text: "Track purchases, view order details, and check delivery status.",
    icon: <FaShoppingBag />,
    link: "/customer/my-orders",
    action: "View Orders"
  },
  {
    title: "Returns & Refunds",
    text: "Start a return request and review refund timelines.",
    icon: <FaRedoAlt />,
    link: "/customer/my-orders",
    action: "Manage Returns"
  },
  {
    title: "Delivery Address",
    text: "Update saved delivery information from your account.",
    icon: <FaMapMarkerAlt />,
    link: "/customer/profile",
    action: "Open Account"
  },
  {
    title: "Payment Help",
    text: "Get help with failed payments, COD, UPI, and card orders.",
    icon: <FaCreditCard />,
    link: "/checkout",
    action: "Payment Options"
  }
];

const faqs = [
  {
    question: "How can I track my order?",
    answer:
      "Open Your Orders and select the order you want to check. You can see status, order date, total, and products there."
  },
  {
    question: "Can I change my delivery address?",
    answer:
      "Yes. You can update your saved address during checkout or from the location selector in the top navigation."
  },
  {
    question: "What should I do if payment fails?",
    answer:
      "Try placing the order again after checking your payment method. If money was deducted, keep the order details ready for support."
  },
  {
    question: "How do I update my password?",
    answer:
      "Go to Your Account, enter your current password, then set and confirm the new password."
  }
];

function CustomerService() {
  return (
    <div className="service-page">
      <div className="service-header">
        <div>
          <h1>Customer Service</h1>
          <p>
            Fast help for orders, payments, address changes, returns, and your
            ShopSphere account.
          </p>
        </div>

        <div className="service-contact">
          <FaHeadset />
          <div>
            <span>Support Hours</span>
            <strong>9 AM - 9 PM</strong>
          </div>
        </div>
      </div>

      <div className="service-grid">
        {supportCards.map((card) => (
          <div
            className="service-card"
            key={card.title}
          >
            <div className="service-icon">{card.icon}</div>
            <h2>{card.title}</h2>
            <p>{card.text}</p>
            <Link to={card.link}>{card.action}</Link>
          </div>
        ))}
      </div>

      <div className="service-section">
        <div className="service-section-title">
          <FaBoxOpen />
          <h2>Popular Help Topics</h2>
        </div>

        <div className="faq-list">
          {faqs.map((faq) => (
            <details
              className="faq-item"
              key={faq.question}
            >
              <summary>{faq.question}</summary>
              <p>{faq.answer}</p>
            </details>
          ))}
        </div>
      </div>

      <div className="service-footer-box">
        <h2>Need more help?</h2>
        <p>
          Keep your order number, email address, and payment method ready before
          contacting support.
        </p>
        <Link to="/customer/profile">Check Account Details</Link>
      </div>
    </div>
  );
}

export default CustomerService;
