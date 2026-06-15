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

        // Disable Chrome's password manager / "password found in data breach" popups,
        // autofill suggestions, and save-password prompts — these steal focus and
        // block Selenium clicks on underlying elements.
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-features=PasswordLeakDetection,AutofillServerCommunication,PasswordManagerOnboarding");
        java.util.Map<String, Object> prefs = new java.util.HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        prefs.put("autofill.profile_enabled", false);
        options.setExperimentalOption("prefs", prefs);

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
