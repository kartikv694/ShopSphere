import "./customer.css";

function BackToTop() {

  const scrollToTop = () => {

    window.scrollTo({
      top: 0,
      behavior: "smooth"
    });

  };

  return (

    <div
      style={{
        width: "100%",
        marginTop: "40px"
      }}
    >

      <button
        onClick={scrollToTop}
        style={{
          width: "100%",
          background: "#37475a",
          color: "white",
          border: "none",
          padding: "16px 0",
          fontSize: "15px",
          fontWeight: "600",
          cursor: "pointer",
          display: "block"
        }}
      >
        Back to top
      </button>

    </div>

  );

}

export default BackToTop;