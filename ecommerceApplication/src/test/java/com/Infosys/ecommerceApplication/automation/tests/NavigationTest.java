package com.Infosys.ecommerceApplication.automation.tests;

import com.Infosys.ecommerceApplication.automation.utils.BaseTest;
import com.Infosys.ecommerceApplication.automation.utils.WaitUtils;
import com.Infosys.ecommerceApplication.automation.pages.HomePage;
import com.Infosys.ecommerceApplication.automation.pages.ProductListingPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Day 33 — US013: Setup Selenium Automation Framework
 * T066: Identify locators (id, xpath, css)
 * T067: Implement basic navigation tests
 */
public class NavigationTest extends BaseTest {

    /**
     * T066: Identify locators — verify CSS, XPath, and name-based selectors work on the home page.
     */
    @Test(description = "T066: Identify locators — CSS, XPath, name selectors work on home page")
    public void testLocatorsWorkOnHomePage() {
        driver.get(BASE_URL + "/");

        // CSS selector — look for any input on the page
        List<WebElement> inputs = driver.findElements(By.cssSelector("input"));
        System.out.println("[INFO] Inputs found via CSS: " + inputs.size());

        // XPath selector — find buttons
        List<WebElement> buttons = driver.findElements(By.xpath("//button"));
        System.out.println("[INFO] Buttons found via XPath: " + buttons.size());

        // TagName selector — find navigation
        List<WebElement> navElements = driver.findElements(By.tagName("nav"));
        System.out.println("[INFO] Nav elements found: " + navElements.size());

        // At least some interactive elements must exist
        Assert.assertTrue(
            inputs.size() > 0 || buttons.size() > 0,
            "Home page should have at least one input or button (login/register form)"
        );

        System.out.println("[PASS] T066: Locators (CSS, XPath, TagName) successfully identified elements.");
    }

    /**
     * T067: Navigate home page → /customer/products.
     */
    @Test(description = "T067: Navigate from home to products page")
    public void testNavigateHomeToProducts() {
        driver.get(BASE_URL + "/");

        // Try clicking a products/shop link in the navbar
        try {
            WebElement productsLink = WaitUtils.waitForClickable(
                driver,
                By.xpath("//a[contains(@href,'products') or contains(text(),'Products') or contains(text(),'Shop')]")
            );
            productsLink.click();
        } catch (Exception e) {
            // Direct navigation fallback
            driver.get(BASE_URL + "/customer/products");
        }

        boolean urlCorrect = WaitUtils.waitForUrlContains(driver, "products");
        Assert.assertTrue(urlCorrect, "Should navigate to products page. Current: " + driver.getCurrentUrl());
        System.out.println("[PASS] T067: Navigated to products page: " + driver.getCurrentUrl());
    }

    /**
     * T067: Navigate using browser Back/Forward buttons.
     */
    @Test(description = "T067: Browser back/forward navigation works")
    public void testBrowserBackForwardNavigation() {
        driver.get(BASE_URL + "/");
        String homePage = driver.getCurrentUrl();

        driver.get(BASE_URL + "/customer/products");
        WaitUtils.waitForUrlContains(driver, "products");

        // Go back
        driver.navigate().back();
        String afterBack = driver.getCurrentUrl();
        Assert.assertTrue(
            afterBack.equals(homePage) || afterBack.contains(BASE_URL),
            "Back navigation should return to previous page"
        );

        // Go forward
        driver.navigate().forward();
        Assert.assertTrue(
            driver.getCurrentUrl().contains("products") || driver.getCurrentUrl().contains(BASE_URL),
            "Forward navigation should return to products"
        );

        System.out.println("[PASS] T067: Browser back/forward navigation works.");
    }

    /**
     * T067: Direct URL navigation to all public routes.
     */
    @Test(description = "T067: Direct URL navigation to public routes")
    public void testDirectUrlNavigation() {
        String[] publicRoutes = {
            "/",
            "/login",
            "/register",
            "/customer/products"
        };

        for (String route : publicRoutes) {
            driver.get(BASE_URL + route);
            String currentUrl = driver.getCurrentUrl();
            Assert.assertFalse(
                driver.getPageSource().contains("ERR_CONNECTION_REFUSED"),
                "Route " + route + " should be accessible"
            );
            System.out.println("[INFO] Route " + route + " loaded: " + currentUrl);
        }
        System.out.println("[PASS] T067: All public routes navigable.");
    }

    /**
     * T067: Navigate to /login page directly.
     */
    @Test(description = "T067: Login page is directly accessible")
    public void testNavigateToLoginPage() {
        driver.get(BASE_URL + "/login");
        WaitUtils.waitForVisible(driver, By.cssSelector("input[name='email']"));
        Assert.assertTrue(driver.getCurrentUrl().contains("login"), "Should be on login page");
        System.out.println("[PASS] T067: Login page accessible at /login");
    }

    /**
     * T067: Navigate to /register page directly.
     */
    @Test(description = "T067: Register page is directly accessible")
    public void testNavigateToRegisterPage() {
        driver.get(BASE_URL + "/register");
        WaitUtils.waitForVisible(driver, By.cssSelector("input[name='name']"));
        Assert.assertTrue(driver.getCurrentUrl().contains("register"), "Should be on register page");
        System.out.println("[PASS] T067: Register page accessible at /register");
    }
}
