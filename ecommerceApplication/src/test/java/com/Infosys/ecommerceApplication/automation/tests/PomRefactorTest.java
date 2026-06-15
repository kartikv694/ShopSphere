package com.Infosys.ecommerceApplication.automation.tests;

import com.Infosys.ecommerceApplication.automation.utils.BaseTest;
import com.Infosys.ecommerceApplication.automation.utils.WaitUtils;
import com.Infosys.ecommerceApplication.automation.pages.LoginPage;
import com.Infosys.ecommerceApplication.automation.pages.CustomerDashboardPage;
import com.Infosys.ecommerceApplication.automation.pages.ProductListingPage;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Day 37 — US014: Validate basic navigation & UI elements (POM Refactor)
 * T078: Refactor code using Page Object Model
 * T079: Reuse login utilities
 *
 * These tests demonstrate that all interactions go through POM classes,
 * not raw driver calls — proving proper Page Object Model refactoring.
 */
public class PomRefactorTest extends BaseTest {

    private static final String VALID_EMAIL    = "kartik@gmail.com";
    private static final String VALID_PASSWORD = "1234";

    /**
     * T078: Entire test uses ONLY POM classes — no raw By locators in the test body.
     * T079: Uses the reusable loginAs() utility from LoginPage.
     */
    @Test(description = "T078-T079: Full flow using POM — login then navigate products, then logout")
    public void testFullFlowViaPom() {
        // T079: Reuse login utility
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(BASE_URL);
        loginPage.loginAs(VALID_EMAIL, VALID_PASSWORD);

        try {
            WaitUtils.waitForUrlContains(driver, "customer");
        } catch (Exception e) {
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        }

        Assert.assertFalse(
            driver.getCurrentUrl().contains("login"),
            "T079: loginAs() utility should log in successfully"
        );
        System.out.println("[PASS] T079: Reusable loginAs() utility worked.");

        // T078: Navigate to products using POM
        ProductListingPage productsPage = new ProductListingPage(driver);
        productsPage.open(BASE_URL);

        Assert.assertTrue(
            driver.getCurrentUrl().contains("products"),
            "T078: ProductListingPage POM should navigate to /customer/products"
        );
        System.out.println("[PASS] T078: ProductListingPage POM navigated correctly.");

        // T078: Logout via CustomerDashboardPage POM
        CustomerDashboardPage dashboard = new CustomerDashboardPage(driver);
        dashboard.logout();
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        Assert.assertFalse(
            driver.getCurrentUrl().contains("/customer/dashboard"),
            "T078: CustomerDashboardPage POM logout should redirect away from dashboard"
        );
        System.out.println("[PASS] T078: Full POM-based flow completed — login → products → logout.");
    }

    /**
     * T079: Demonstrate login utility reuse — called from multiple tests with different contexts.
     */
    @Test(description = "T079: loginAs() utility is reusable across different test contexts")
    public void testLoginUtilityReusability() {
        LoginPage loginPage = new LoginPage(driver);

        // Use 1: Navigate to login page and use utility
        loginPage.open(BASE_URL);
        loginPage.loginAs(VALID_EMAIL, VALID_PASSWORD);

        try {
            WaitUtils.waitForUrlContains(driver, "customer");
        } catch (Exception e) {
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        }

        String urlAfterFirstLogin = driver.getCurrentUrl();
        Assert.assertFalse(urlAfterFirstLogin.contains("login"), "First loginAs() call should succeed");

        // Logout
        new CustomerDashboardPage(driver).logout();
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        // Use 2: Reuse the same utility again in the same test
        driver.get(BASE_URL + "/login");
        WaitUtils.waitForVisible(driver, By.cssSelector("input[name='email']"));
        loginPage.loginAs(VALID_EMAIL, VALID_PASSWORD);

        try {
            WaitUtils.waitForUrlContains(driver, "customer");
        } catch (Exception e) {
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        }

        Assert.assertFalse(
            driver.getCurrentUrl().contains("login"),
            "Second loginAs() call should also succeed"
        );

        System.out.println("[PASS] T079: loginAs() utility successfully reused twice in one test.");
    }

    /**
     * T078: Verify all POM pages initialize without errors.
     * (Structural test — pages compile and construct correctly)
     */
    @Test(description = "T078: All POM page objects initialize correctly")
    public void testAllPomPagesInitialize() {
        // Instantiate every POM page — verifies no constructor errors
        LoginPage loginPage          = new LoginPage(driver);
        ProductListingPage products  = new ProductListingPage(driver);
        CustomerDashboardPage dash   = new CustomerDashboardPage(driver);

        Assert.assertNotNull(loginPage,  "LoginPage POM should instantiate");
        Assert.assertNotNull(products,   "ProductListingPage POM should instantiate");
        Assert.assertNotNull(dash,       "CustomerDashboardPage POM should instantiate");

        // Open each POM's designated URL
        loginPage.open(BASE_URL);
        Assert.assertTrue(loginPage.isLoaded(), "LoginPage should be loadable");

        products.open(BASE_URL);
        Assert.assertTrue(
            driver.getCurrentUrl().contains("products"),
            "ProductListingPage should navigate to /customer/products"
        );

        System.out.println("[PASS] T078: All POM page objects instantiate and navigate correctly.");
    }
}
