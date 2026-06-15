package com.Infosys.ecommerceApplication.automation.pages;

import com.Infosys.ecommerceApplication.automation.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * T065: POM — Dashboard page object.
 * T076: Automate logout functionality
 * T077: Validate session handling
 *
 * Reused for BOTH the Customer Dashboard (/customer/dashboard) and the
 * Admin Dashboard (/admin/dashboard):
 *  - Customer (CustomerNavbar.jsx): "Logout" is inside a user-menu
 *    dropdown that must first be opened via the user icon button
 *    (".user-menu-btn").
 *  - Admin (Sidebar.jsx): "Logout" is a directly clickable button
 *    (".sidebar-logout") — no dropdown needed.
 *
 * logout() handles both cases automatically.
 */
public class CustomerDashboardPage {

    private final WebDriver driver;

    // Customer: user-menu trigger (FaUser icon button in CustomerNavbar.jsx)
    private final By userMenuButton = By.cssSelector(".user-menu-btn");

    // Admin: direct logout button in Sidebar.jsx
    private final By sidebarLogout  = By.cssSelector(".sidebar-logout");

    // Generic: any element whose text says Logout / Sign Out (covers both
    // the customer dropdown item and the admin sidebar button)
    private final By logoutButton   = By.xpath(
        "//button[contains(.,'Logout') or contains(.,'logout') or contains(.,'Sign Out') or contains(.,'Sign out')]"
        + " | //a[contains(.,'Logout') or contains(.,'logout') or contains(.,'Sign Out') or contains(.,'Sign out')]"
    );

    private final By profileLink   = By.xpath("//a[contains(@href,'/customer/profile')]");

    public CustomerDashboardPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isLoaded() {
        return WaitUtils.waitForUrlContains(driver, "dashboard");
    }

    /**
     * T076/T078/T079: Click logout — reusable across Customer and Admin
     * dashboards.
     */
    public void logout() {
        // Case 1: Admin sidebar — logout button is directly visible/clickable
        try {
            WebElement directLogout = driver.findElement(sidebarLogout);
            if (directLogout.isDisplayed()) {
                clickViaJsIfNeeded(WaitUtils.waitForClickable(driver, sidebarLogout));
                return;
            }
        } catch (Exception ignored) {
            // not the admin sidebar layout — continue
        }

        // Case 2: Generic visible "Logout" button/link already on screen
        try {
            WebElement generic = driver.findElement(logoutButton);
            if (generic.isDisplayed()) {
                clickViaJsIfNeeded(WaitUtils.waitForClickable(driver, logoutButton));
                return;
            }
        } catch (Exception ignored) {
            // not directly visible — likely hidden inside a dropdown
        }

        // Case 3: Customer navbar — open the user-menu dropdown first, then click Logout
        try {
            clickViaJsIfNeeded(WaitUtils.waitForClickable(driver, userMenuButton));
            clickViaJsIfNeeded(WaitUtils.waitForClickable(driver, logoutButton));
            return;
        } catch (Exception ignored) {
            // fall through to last resort
        }

        // Last resort: try profile link then logout
        try {
            clickViaJsIfNeeded(WaitUtils.waitForClickable(driver, profileLink));
            clickViaJsIfNeeded(WaitUtils.waitForClickable(driver, logoutButton));
        } catch (Exception e) {
            throw new RuntimeException("Could not find logout button", e);
        }
    }

    private void clickViaJsIfNeeded(WebElement element) {
        try {
            element.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    /** T077: After logout, user should be redirected away from protected dashboards */
    public boolean isRedirectedToPublicPage() {
        String url = driver.getCurrentUrl();
        return !url.contains("/customer/dashboard") && !url.contains("/admin/dashboard");
    }

    /** T077: Attempt to navigate to a protected route directly */
    public void attemptAccessProtectedRoute(String baseUrl, String path) {
        driver.get(baseUrl + path);
    }

    public boolean isOnLoginPage() {
        return driver.getCurrentUrl().contains("/login") || driver.getCurrentUrl().endsWith("/");
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
