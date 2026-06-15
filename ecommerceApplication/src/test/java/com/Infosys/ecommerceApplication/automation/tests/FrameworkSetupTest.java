package com.Infosys.ecommerceApplication.automation.tests;

import com.Infosys.ecommerceApplication.automation.utils.BaseTest;
import com.Infosys.ecommerceApplication.automation.pages.HomePage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Day 31 & 32 — US013: Setup Selenium Automation Framework
 * T063: Setup Maven project for automation
 * T064: Add Selenium + TestNG dependencies / Configure WebDriver (Chrome Driver)
 * T065: Setup project structure (POM – Page Object Model)
 */
public class FrameworkSetupTest extends BaseTest {

    /**
     * T063 / T064: Verify that Chrome launches and the app URL is reachable.
     * If this test passes, the Maven project, Selenium, TestNG, and ChromeDriver
     * are all wired up correctly.
     */
    @Test(description = "T063-T064: ChromeDriver launches and ShopSphere home page loads")
    public void testChromeDriverLaunchesSuccessfully() {
        driver.get(BASE_URL);

        String currentUrl = driver.getCurrentUrl();
        Assert.assertNotNull(currentUrl, "URL should not be null after navigation");
        Assert.assertFalse(currentUrl.isEmpty(), "URL should not be empty");

        // Check we are not on an error/404 page
        String pageSource = driver.getPageSource();
        Assert.assertFalse(
            pageSource.contains("This site can't be reached") ||
            pageSource.contains("ERR_CONNECTION_REFUSED"),
            "App should be running on " + BASE_URL + " — start the Vite dev server first."
        );

        System.out.println("[PASS] T063/T064: ChromeDriver launched and connected to " + currentUrl);
    }

    /**
     * T065: Verify Page Object Model is working — HomePage POM can navigate.
     */
    @Test(description = "T065: HomePage POM navigates to root correctly")
    public void testPageObjectModelStructure() {
        HomePage homePage = new HomePage(driver);
        homePage.open(BASE_URL);

        boolean loaded = homePage.isLoaded();
        Assert.assertTrue(loaded, "HomePage POM should successfully load " + BASE_URL);

        System.out.println("[PASS] T065: POM structure verified. Page title: " + homePage.getTitle());
    }

    /**
     * T064: Verify WebDriver implicit wait is configured (non-zero timeout).
     * Indirectly verified by successfully waiting for an element on the page.
     */
    @Test(description = "T064: Implicit wait is configured — page elements load within timeout")
    public void testImplicitWaitIsConfigured() {
        driver.get(BASE_URL);

        // If implicit wait is configured, this will wait up to 10s for the body
        org.openqa.selenium.WebElement body = driver.findElement(
            org.openqa.selenium.By.tagName("body")
        );
        Assert.assertNotNull(body, "Body element should be found with implicit wait");
        Assert.assertTrue(body.isDisplayed(), "Body element should be visible");

        System.out.println("[PASS] T064: Implicit wait working — body element found on home page.");
    }
}
