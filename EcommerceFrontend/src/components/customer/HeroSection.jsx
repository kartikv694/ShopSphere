import { useCallback, useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { FaChevronLeft, FaChevronRight } from "react-icons/fa";
import "./Customer.css";

const slides = [

  {
    title: "Welcome to ShopSphere",
    subtitle:
      "Discover trending products, unbeatable prices and a premium shopping experience.",
    cta: "Shop Now",
    route: "all",
    image:
      "https://images.unsplash.com/photo-1607082348824-0a96f2a4b9da?auto=format&fit=crop&w=1600&q=80"
  },

  {
    title: "Power Up With Electronics",
    subtitle:
      "Laptops, gadgets and accessories built for the way you live and work.",
    cta: "Explore Electronics",
    route: "electronics",
    image:
      "https://images.unsplash.com/photo-1498049794561-7780e7231661?auto=format&fit=crop&w=1600&q=80"
  },

  {
    title: "Fresh Picks, Every Day",
    subtitle:
      "Quality foods sourced for freshness, delivered straight to your door.",
    cta: "Shop Foods",
    route: "foods",
    image:
      "https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=1600&q=80"
  },

  {
    title: "Beauty That Fits You",
    subtitle:
      "Skincare, makeup and self-care essentials curated just for you.",
    cta: "Shop Beauty",
    route: "beauty",
    image:
      "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?auto=format&fit=crop&w=1600&q=80"
  },

  {
    title: "Playtime Starts Here",
    subtitle:
      "Toys and games that spark imagination for every age.",
    cta: "Shop Toys",
    route: "toys",
    image:
      "https://images.unsplash.com/photo-1545558014-8692077e9b5c?auto=format&fit=crop&w=1600&q=80"
  }

];

const AUTO_PLAY_INTERVAL = 5000;

function HeroSection() {

  const navigate = useNavigate();

  const [activeSlide, setActiveSlide] = useState(0);

  const [isPaused, setIsPaused] = useState(false);

  const timerRef = useRef(null);

  const categories = [

    {
      title: "Electronics",
      route: "electronics",
      image:
        "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9"
    },

    {
      title: "Laptops",
      route: "laptops",
      image:
        "https://images.unsplash.com/photo-1496181133206-80ce9b88a853"
    },

    {
      title: "Mobiles",
      route: "mobiles",
      image:
        "https://images.unsplash.com/photo-1512941937669-90a1b58e7e9c"
    },

    {
      title: "Accessories",
      route: "accessories",
      image:
        "https://images.unsplash.com/photo-1505740420928-5e560c06d30e"
    }

  ];

  const goToSlide = useCallback((index) => {

    setActiveSlide(
      (index + slides.length) % slides.length
    );

  }, []);

  const goToNext = useCallback(() => {

    setActiveSlide((prev) => (prev + 1) % slides.length);

  }, []);

  const goToPrev = useCallback(() => {

    setActiveSlide(
      (prev) => (prev - 1 + slides.length) % slides.length
    );

  }, []);

  // AUTO-PLAY THE CAROUSEL, PAUSING WHILE THE USER IS HOVERING
  useEffect(() => {

    if (isPaused) return;

    timerRef.current = setInterval(() => {

      goToNext();

    }, AUTO_PLAY_INTERVAL);

    return () => clearInterval(timerRef.current);

  }, [isPaused, goToNext]);

  return (

    <div className="hero-wrapper">

      {/* HERO CAROUSEL */}
      <div
        className="hero-banner hero-carousel"
        onMouseEnter={() => setIsPaused(true)}
        onMouseLeave={() => setIsPaused(false)}
      >

        <div
          className="hero-slide-track"
          style={{
            transform: `translateX(-${activeSlide * 100}%)`
          }}
        >

          {slides.map((slide, index) => (

            <div
              className="hero-slide"
              key={slide.route}
              style={{
                backgroundImage: `linear-gradient(120deg, rgba(15,23,42,0.65), rgba(15,23,42,0.2)), url(${slide.image})`
              }}
              aria-hidden={activeSlide !== index}
            >

              <div
                className={
                  "hero-content" +
                  (activeSlide === index
                    ? " hero-content-active"
                    : "")
                }
              >

                <h1>{slide.title}</h1>

                <p>{slide.subtitle}</p>

                <button
                  onClick={() =>
                    navigate(
                      `/customer/category/${slide.route}`
                    )
                  }
                >
                  {slide.cta}
                </button>

              </div>

            </div>

          ))}

        </div>

        {/* PREV / NEXT ARROWS */}
        <button
          className="hero-arrow hero-arrow-left"
          onClick={goToPrev}
          aria-label="Previous slide"
        >
          <FaChevronLeft />
        </button>

        <button
          className="hero-arrow hero-arrow-right"
          onClick={goToNext}
          aria-label="Next slide"
        >
          <FaChevronRight />
        </button>

        {/* DOTS */}
        <div className="hero-dots">

          {slides.map((slide, index) => (

            <button
              key={slide.route}
              className={
                "hero-dot" +
                (activeSlide === index
                  ? " hero-dot-active"
                  : "")
              }
              onClick={() => goToSlide(index)}
              aria-label={`Go to slide ${index + 1}`}
            />

          ))}

        </div>

      </div>

      {/* CATEGORY SECTION */}
      <div className="hero-category-section">

        {categories.map((category, index) => (

          <div
            className="hero-category-card"
            key={index}
            onClick={() =>
              navigate(
                `/customer/category/${category.route}`
              )
            }
          >

            <img
              src={category.image}
              alt={category.title}
            />

            <h3>
              {category.title}
            </h3>

          </div>

        ))}

      </div>

    </div>

  );
}

export default HeroSection;
