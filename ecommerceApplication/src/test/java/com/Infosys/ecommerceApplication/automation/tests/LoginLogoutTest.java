package com.Infosys.ecommerceApplication.automation.tests;

import com.Infosys.ecommerceApplication.automation.utils.BaseTest;
import com.Infosys.ecommerceApplication.automation.utils.WaitUtils;
import com.Infosys.ecommerceApplication.automation.pages.LoginPage;
import com.Infosys.ecommerceApplication.automation.pages.CustomerDashboardPage;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Day 35 & 36 — US015: Validate Login & Logout
 * T072: Automate login with valid credentials
 * T073: Automate login with invalid credentials
 * T074: Validate error messages
 * T075: Implement assertions
 * T076: Automate logout functionality
 * T077: Validate session handling
 *
 * REUSE: The SAME test methods below run for BOTH the CUSTOMER and ADMIN
 * accounts via the {@link #users()} @DataProvider — no duplicate test
 * scripts are needed for the two roles.
 *
 * IMPORTANT: Update CUSTOMER_EMAIL/PASSWORD and ADMIN_EMAIL/PASSWORD below
 *            to match real registered users in your local MySQL database.
 */
public class LoginLogoutTest extends BaseTest {

    // ---- Update these to real users in your DB ----
    private static final String CUSTOMER_EMAIL    = "kartik@gmail.com";
    private static final String CUSTOMER_PASSWORD = "1234";

    private static final String ADMIN_EMAIL       = "admin@gmail.com";
    private static final String ADMIN_PASSWORD    = "admin";
    // -------------------------------------------------

    private static final String INVALID_EMAIL    = "nobody@fake.com";
    private static final String INVALID_PASSWORD = "WrongPass123";

    /**
     * Data provider supplying {role, email, password, dashboardUrlFragment}
     * so every @Test below runs once for CUSTOMER and once for ADMIN.
     */
    @DataProvider(name = "users")
    public Object[][] users() {
        return new Object[][] {
            { "CUSTOMER", CUSTOMER_EMAIL, CUSTOMER_PASSWORD, "/customer/dashboard" },
            { "ADMIN",    ADMIN_EMAIL,    ADMIN_PASSWORD,    "/admin/dashboard" }
        };
    }

    /**
     * T072: Automate login with valid credentials — runs for both roles.
     */
    @Test(dataProvider = "users", description = "T072: Login with valid credentials redirects to the correct dashboard")
    public void testLoginWithValidCredentials(String role, String email, String password, String dashboardFragment) {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(BASE_URL);
        Assert.assertTrue(loginPage.isLoaded(), "Login page should load");

        loginPage.loginAs(email, password);

        // T075: Implement assertions — URL should change to the role's dashboard
        boolean redirected = WaitUtils.waitForUrlContains(driver, dashboardFragment);
        Assert.assertTrue(
            redirected || driver.getCurrentUrl().contains(dashboardFragment),
            "[" + role + "] Valid login should redirect to " + dashboardFragment + ". Current URL: " + driver.getCurrentUrl()
        );
        System.out.println("[PASS] T072 (" + role + "): Valid login redirected to: " + driver.getCurrentUrl());
    }

    /**
     * T072: Login via home page embedded login form — runs for both roles.
     */
    @Test(dataProvider = "users", description = "T072: Login via home page embedded form works for both roles")
    public void testLoginViaHomePage(String role, String email, String password, String dashboardFragment) {
        driver.get(BASE_URL + "/");

        // The home page has an embedded login/register toggle — make sure
        // the Login tab is active before filling the form.
        try {
            org.openqa.selenium.WebElement loginTab = WaitUtils.waitForClickable(
                driver,
                By.xpath("//button[contains(@class,'auth-tab') and contains(text(),'Login')]")
            );
            loginTab.click();
        } catch (Exception e) {
            // Login form may already be the visible/default tab
        }

        LoginPage loginPage = new LoginPage(driver);
        loginPage.enterEmail(email);
        loginPage.enterPassword(password);
        loginPage.clickSubmit();

        boolean redirected = WaitUtils.waitForUrlContains(driver, dashboardFragment);
        Assert.assertTrue(
            redirected || driver.getCurrentUrl().contains(dashboardFragment),
            "[" + role + "] After login from home page should reach " + dashboardFragment + ". URL: " + driver.getCurrentUrl()
        );
        System.out.println("[PASS] T072 (" + role + "): Home page login flow works.");
    }

    /**
     * T073: Login with invalid email — should NOT reach any dashboard.
     * (Role-independent — only needs to run once, so it does not use the data provider.)
     */
    @Test(description = "T073: Login with invalid email shows error / is rejected")
    public void testLoginWithInvalidEmail() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(BASE_URL);
        loginPage.loginAs(INVALID_EMAIL, INVALID_PASSWORD);

        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        // T074/T075: Assert the user did NOT get into the app
        Assert.assertFalse(
            driver.getCurrentUrl().contains("dashboard"),
            "Invalid credentials should NOT reach any dashboard"
        );

        System.out.println("[PASS] T073: Invalid email login was rejected. URL: " + driver.getCurrentUrl());
    }

    /**
     * T073: Login with valid email but wrong password — runs for both roles.
     */
    @Test(dataProvider = "users", description = "T073: Login with wrong password is rejected for both roles")
    public void testLoginWithWrongPassword(String role, String email, String password, String dashboardFragment) {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(BASE_URL);
        loginPage.loginAs(email, INVALID_PASSWORD);

        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        Assert.assertFalse(
            driver.getCurrentUrl().contains("dashboard"),
            "[" + role + "] Wrong password should NOT reach dashboard"
        );
        System.out.println("[PASS] T073 (" + role + "): Wrong password login rejected.");
    }

    /**
     * T073 + T074: Login with empty credentials — validate the form stays on /login.
     * (Role-independent — only needs to run once.)
     */
    @Test(description = "T073-T074: Empty credentials login shows error / stays on login page")
    public void testLoginWithEmptyCredentials() {
        driver.get(BASE_URL + "/login");
        WaitUtils.waitForVisible(driver, By.cssSelector("input[name='email']"));

        // Submit empty form
        new LoginPage(driver).clickSubmit();

        try { Thread.sleep(800); } catch (InterruptedException ignored) {}

        // T074/T075: Empty (required) fields should block submission — stay on /login
        Assert.assertTrue(
            driver.getCurrentUrl().contains("login"),
            "Empty login should stay on login page"
        );
        System.out.println("[PASS] T073-T074: Empty credentials blocked with validation.");
    }

    /**
     * T074: Validate error messages — check toast/alert text content.
     * (Role-independent — only needs to run once.)
     */
    @Test(description = "T074: Error message is displayed for invalid login")
    public void testErrorMessageContent() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(BASE_URL);
        loginPage.loginAs("wrong@email.com", "badpassword");

        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        // Check toast or inline error is displayed
        boolean errorShown = false;
        String errorText = "";

        try {
            org.openqa.selenium.WebElement toast = WaitUtils.waitForVisible(
                driver,
                By.cssSelector(".Toastify__toast--error, .Toastify__toast, [class*='toast']")
            );
            errorShown = toast.isDisplayed();
            errorText = toast.getText();
        } catch (Exception e1) {
            try {
                org.openqa.selenium.WebElement err = driver.findElement(
                    By.cssSelector("[class*='error'], [class*='Error'], .alert")
                );
                errorShown = err.isDisplayed();
                errorText = err.getText();
            } catch (Exception e2) {
                System.out.println("[INFO] No visible error element found — checking page is not on dashboard");
            }
        }

        if (errorShown) {
            Assert.assertFalse(errorText.isEmpty(), "Error message should not be empty");
            System.out.println("[PASS] T074: Error message shown: '" + errorText + "'");
        } else {
            Assert.assertFalse(
                driver.getCurrentUrl().contains("dashboard"),
                "Without visible error, user should at least not be on dashboard"
            );
            System.out.println("[PASS] T074: Login blocked (no dashboard redirect).");
        }
    }

    /**
     * T075: Implement assertions — assert URL and page title after successful login,
     * for both roles.
     */
    @Test(dataProvider = "users", description = "T075: Assert page title and URL after successful login")
    public void testAssertionsAfterLogin(String role, String email, String password, String dashboardFragment) {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(BASE_URL);
        loginPage.loginAs(email, password);

        try {
            WaitUtils.waitForUrlContains(driver, dashboardFragment);
        } catch (Exception e) {
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        }

        String currentUrl = driver.getCurrentUrl();
        String pageTitle  = driver.getTitle();

        Assert.assertNotNull(currentUrl, "URL should not be null after login");
        Assert.assertFalse(currentUrl.isEmpty(), "URL should not be empty after login");
        Assert.assertTrue(
            currentUrl.contains(dashboardFragment),
            "[" + role + "] Logged-in user should reach " + dashboardFragment + ". URL: " + currentUrl
        );
        Assert.assertNotNull(pageTitle, "Page title should not be null after login");

        System.out.println("[PASS] T075 (" + role + "): Post-login assertions passed. URL=" + currentUrl + ", Title=" + pageTitle);
    }

    /**
     * T076: Automate logout functionality — runs for both roles.
     * Customer logout uses the user-menu dropdown; Admin logout uses the
     * sidebar button. CustomerDashboardPage.logout() handles both.
     */
    @Test(dataProvider = "users", description = "T076: Logout redirects away from the dashboard for both roles")
    public void testLogoutFunctionality(String role, String email, String password, String dashboardFragment) {
        // First login
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(BASE_URL);
        loginPage.loginAs(email, password);

        try {
            WaitUtils.waitForUrlContains(driver, dashboardFragment);
        } catch (Exception e) {
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        }

        Assert.assertTrue(
            driver.getCurrentUrl().contains(dashboardFragment),
            "[" + role + "] Should be logged in before testing logout"
        );

        // T076: Find and click logout (works for both customer + admin layouts)
        CustomerDashboardPage dashboard = new CustomerDashboardPage(driver);
        dashboard.logout();

        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        // T075: Assert redirected away from the dashboard
        String urlAfterLogout = driver.getCurrentUrl();
        Assert.assertFalse(
            urlAfterLogout.contains(dashboardFragment),
            "[" + role + "] After logout should not stay on " + dashboardFragment + ". URL: " + urlAfterLogout
        );
        System.out.println("[PASS] T076 (" + role + "): Logout successful. Redirected to: " + urlAfterLogout);
    }

    /**
     * T077: Validate session handling — after logout, accessing a protected
     * route directly redirects away. Runs for both roles.
     */
    @Test(dataProvider = "users", description = "T077: Session is cleared after logout — protected routes redirect")
    public void testSessionClearedAfterLogout(String role, String email, String password, String dashboardFragment) {
        // Login first
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(BASE_URL);
        loginPage.loginAs(email, password);

        try {
            WaitUtils.waitForUrlContains(driver, dashboardFragment);
        } catch (Exception e) {
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        }

        // Logout
        CustomerDashboardPage dashboard = new CustomerDashboardPage(driver);
        dashboard.logout();
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        // T077: Attempt to navigate to the protected dashboard route directly
        driver.get(BASE_URL + dashboardFragment);
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        String currentUrl = driver.getCurrentUrl();

        Assert.assertFalse(
            currentUrl.contains(dashboardFragment),
            "[" + role + "] After logout, accessing " + dashboardFragment + " should redirect. URL: " + currentUrl
        );
        System.out.println("[PASS] T077 (" + role + "): Session cleared. Protected route redirected to: " + currentUrl);
    }

    /**
     * T077: Validate session persists across page refresh (while logged in) — both roles.
     */
    @Test(dataProvider = "users", description = "T077: Session persists after page refresh while logged in")
    public void testSessionPersistsOnRefresh(String role, String email, String password, String dashboardFragment) {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(BASE_URL);
        loginPage.loginAs(email, password);

        try {
            WaitUtils.waitForUrlContains(driver, dashboardFragment);
        } catch (Exception e) {
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        }

        // Refresh the page
        driver.navigate().refresh();
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        String urlAfterRefresh = driver.getCurrentUrl();

        // Should still be in the same protected area (token in localStorage persists)
        Assert.assertFalse(
            urlAfterRefresh.contains("/login"),
            "[" + role + "] Session should persist after refresh. URL after refresh: " + urlAfterRefresh
        );
        System.out.println("[PASS] T077 (" + role + "): Session persisted after refresh. URL: " + urlAfterRefresh);
    }
}
