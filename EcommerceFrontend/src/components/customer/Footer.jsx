function Footer() {

  return (

    <footer
      style={{
        background: "#131A22",
        color: "white",
        marginTop: "0px"
      }}
    >

      {/* TOP LINKS */}

      <div
        style={{
          display: "grid",
          gridTemplateColumns:
            "repeat(auto-fit, minmax(220px, 1fr))",
          gap: "40px",
          padding: "50px 60px",
          borderBottom: "1px solid #3a4553"
        }}
      >

        {/* COLUMN 1 */}

        <div>

          <h3
            style={{
              marginBottom: "18px",
              fontSize: "18px"
            }}
          >
            Get to Know Us
          </h3>

          <p style={styles.link}>
            About Us
          </p>

          <p style={styles.link}>
            Careers
          </p>

          <p style={styles.link}>
            Press Releases
          </p>

          <p style={styles.link}>
            Our Services
          </p>

        </div>

        {/* COLUMN 2 */}

        <div>

          <h3
            style={{
              marginBottom: "18px",
              fontSize: "18px"
            }}
          >
            Connect With Us
          </h3>

          <p style={styles.link}>
            Instagram
          </p>

          <p style={styles.link}>
            Facebook
          </p>

          <p style={styles.link}>
            Twitter
          </p>

          <p style={styles.link}>
            LinkedIn
          </p>

        </div>

        {/* COLUMN 3 */}

        <div>

          <h3
            style={{
              marginBottom: "18px",
              fontSize: "18px"
            }}
          >
            Make Money With Us
          </h3>

          <p style={styles.link}>
            Sell Products
          </p>

          <p style={styles.link}>
            Affiliate Program
          </p>

          <p style={styles.link}>
            Advertise Products
          </p>

          <p style={styles.link}>
            Become Partner
          </p>

        </div>

        {/* COLUMN 4 */}

        <div>

          <h3
            style={{
              marginBottom: "18px",
              fontSize: "18px"
            }}
          >
            Let Us Help You
          </h3>

          <p style={styles.link}>
            Your Account
          </p>

          <p style={styles.link}>
            Returns Centre
          </p>

          <p style={styles.link}>
            Help
          </p>

          <p style={styles.link}>
            24×7 Support
          </p>

        </div>

      </div>

      {/* BOTTOM */}

      <div
        style={{
          textAlign: "center",
          padding: "25px",
          fontSize: "14px",
          color: "#DDD"
        }}
      >

        © 2026 ShopSphere.
        All Rights Reserved.

      </div>

    </footer>

  );

}

const styles = {

  link: {

    marginBottom: "12px",

    color: "#DDD",

    cursor: "pointer",

    fontSize: "15px"
  }

};

export default Footer;