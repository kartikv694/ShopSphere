package com.Infosys.ecommerceApplication.automation.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

/**
 * BaseTest: Sets up and tears down a ChromeDriver instance for every test method.
 * WebDriverManager automatically downloads the correct ChromeDriver binary.
 */
public class BaseTest {

    protected WebDriver driver;

    // Change this to your local frontend URL if different
    protected static final String BASE_URL = "http://localhost:5173";

    @BeforeMethod
    public void setUp() {
        // T063: Setup Maven project for automation
        // T064: Add Selenium + TestNG dependencies  (done in pom.xml)
        // T064: Configure WebDriver (ChromeDriver)
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        // Run headless in CI; comment out for visual debugging locally
        // options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-notifications");

        driver = new ChromeDriver(options);

        // T068: Implement waits (implicit)
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().window().maximize();
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
