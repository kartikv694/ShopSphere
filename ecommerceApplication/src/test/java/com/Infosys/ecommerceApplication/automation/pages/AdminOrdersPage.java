package com.Infosys.ecommerceApplication.automation.pages;

import com.Infosys.ecommerceApplication.automation.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Object for /admin/orders
 *
 * Actual AdminOrders.jsx DOM (verified against source):
 *   .admin-orders-page
 *     .admin-orders-header
 *       h1                — "Orders"
 *     .admin-orders-message — "Loading orders..." / "No orders found"
 *     .admin-orders-list
 *       .admin-order-card (×N)
 *         button.admin-order-main  — clickable, opens details overlay
 *           h2               — "Order #123"
 *           strong           — status text (PLACED / SHIPPED / DELIVERED)
 *           h3               — formatted total
 *         .admin-order-tracker (AdminOrderTracker component)
 *         .admin-order-footer
 *           button.admin-order-action — "Proceed to Shipping" (PLACED→SHIPPED)
 *                                        "Mark Delivered" (SHIPPED→DELIVERED)
 *           span.admin-order-complete — "Completed" (terminal/DELIVERED state)
 *
 *   Clicking .admin-order-main opens:
 *     .admin-order-overlay
 *       .admin-order-panel
 *         button.admin-order-close
 *         .admin-order-panel-actions
 *           (same renderAction() button/span as the card footer)
 *
 * IMPORTANT: clicking "Proceed to Shipping" starts a 30-SECOND client-side
 * timer that auto-advances PLACED→SHIPPED→DELIVERED — tests should not
 * assume DELIVERED is reachable by a second manual click within 30s.
 */
public class AdminOrdersPage {

    private final WebDriver driver;

    private final By pageHeading     = By.cssSelector(".admin-orders-header h1");
    private final By orderCards      = By.cssSelector(".admin-order-card");
    private final By orderMainBtns   = By.cssSelector("button.admin-order-main");
    private final By orderMessage    = By.cssSelector(".admin-orders-message");
    private final By actionButtons   = By.cssSelector("button.admin-order-action");
    private final By completeSpans   = By.cssSelector("span.admin-order-complete");

    private final By detailsOverlay  = By.cssSelector(".admin-order-overlay");
    private final By detailsPanel    = By.cssSelector(".admin-order-panel");
    private final By closeDetailsBtn = By.cssSelector("button.admin-order-close");

    public AdminOrdersPage(WebDriver driver) {
        this.driver = driver;
    }

    public void open(String baseUrl) {
        driver.get(baseUrl + "/admin/orders");
    }

    public boolean isLoaded() {
        try {
            WaitUtils.waitForVisible(driver, pageHeading);
            Thread.sleep(1500);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getHeadingText() {
        try {
            return WaitUtils.waitForVisible(driver, pageHeading).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public int getOrderCount() {
        return driver.findElements(orderCards).size();
    }

    public boolean hasOrders() {
        return !driver.findElements(orderCards).isEmpty();
    }

    public String getEmptyOrLoadingMessage() {
        try {
            return driver.findElement(orderMessage).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public List<String> getActionButtonTexts() {
        return driver.findElements(actionButtons)
                .stream()
                .map(el -> el.getText().trim())
                .collect(Collectors.toList());
    }

    public int getActionButtonCount() {
        return driver.findElements(actionButtons).size();
    }

    public int getCompletedOrderCount() {
        return driver.findElements(completeSpans).size();
    }

    /** Clicks the first available action button (e.g. "Proceed to Shipping"). */
    public String clickFirstActionButton() {
        List<WebElement> btns = driver.findElements(actionButtons);
        if (btns.isEmpty()) return "";
        String text = btns.get(0).getText().trim();
        clickRobustly(btns.get(0));
        try { Thread.sleep(1200); } catch (InterruptedException ignored) {}
        return text;
    }

    /**
     * Returns the status text (from the <strong> tag) of the Nth order
     * card's .admin-order-main button.
     */
    public String getOrderStatus(int index) {
        List<WebElement> mains = driver.findElements(orderMainBtns);
        if (index >= mains.size()) return "";
        try {
            return mains.get(index).findElement(By.tagName("strong")).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public String getFirstOrderStatus() {
        return getOrderStatus(0);
    }

    /** Clicks the Nth order's main button to open its details overlay. */
    public void clickOrderCard(int index) {
        List<WebElement> mains = driver.findElements(orderMainBtns);
        if (index < mains.size()) {
            clickRobustly(mains.get(index));
            try { Thread.sleep(700); } catch (InterruptedException ignored) {}
        }
    }

    public boolean isDetailsPanelVisible() {
        try {
            return WaitUtils.waitForVisible(driver, detailsPanel).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void closeDetailsPanel() {
        try {
            WebElement btn = WaitUtils.waitForClickable(driver, closeDetailsBtn);
            clickRobustly(btn);
        } catch (Exception e) {
            // Panel may have already closed
        }
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
    }

    private void clickRobustly(By locator) {
        WebElement el = WaitUtils.waitForClickable(driver, locator);
        clickRobustly(el);
    }

    private void clickRobustly(WebElement el) {
        try {
            el.click();
        } catch (org.openqa.selenium.ElementClickInterceptedException e) {
            try { Thread.sleep(400); } catch (InterruptedException ignored) {}
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        }
    }
}
