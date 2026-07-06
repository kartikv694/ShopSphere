package com.Infosys.ecommerceApplication.automation.pages;

import com.Infosys.ecommerceApplication.automation.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Object for /customer/my-orders
 *
 * Actual CustomerOrders.jsx DOM (verified against source):
 *   .orders-page
 *     h1.orders-title          — "My Orders"
 *     .orders-message          — "Loading orders..." / "No orders found"
 *     button.order-card.order-card-clickable (×N) — the ENTIRE card is the
 *       clickable trigger to open details — there is no separate
 *       "View Details" button.
 *       .order-header
 *         h3                   — "Order #123"
 *         p                    — order date
 *         h3.order-status      — PLACED / SHIPPED / DELIVERED (normalized)
 *         p.order-payment-mode — "Payment: Cash On Delivery"
 *         h2.order-total       — formatted total
 *       .order-tracker (OrderTracker component)
 *         .tracker-step / .tracker-step-active (×4)
 *       .order-summary-line    — "N item(s)"
 *
 *   When a card is clicked, selectedOrder state opens an overlay:
 *     .order-details-overlay
 *       .order-details-panel
 *         button.order-details-close — "X"
 *         .order-details-header
 *         .order-details-meta
 *         .order-products
 *           .order-product (×N) — img, h3 name, p qty, p price, p subtotal
 *
 * NOTE: This component has NO cancel-order button — order cancellation is
 * not part of CustomerOrders.jsx's feature set as currently implemented.
 */
public class CustomerOrdersPage {

    private final WebDriver driver;

    private final By pageHeading      = By.cssSelector("h1.orders-title");
    private final By ordersMessage    = By.cssSelector(".orders-message");
    private final By orderCards       = By.cssSelector("button.order-card");
    private final By orderStatuses    = By.cssSelector(".order-status");
    private final By orderTotals      = By.cssSelector(".order-total");
    private final By detailsOverlay   = By.cssSelector(".order-details-overlay");
    private final By detailsPanel     = By.cssSelector(".order-details-panel");
    private final By closeDetailsBtn  = By.cssSelector("button.order-details-close");
    private final By detailsProducts  = By.cssSelector(".order-product");

    public CustomerOrdersPage(WebDriver driver) {
        this.driver = driver;
    }

    public void open(String baseUrl) {
        driver.get(baseUrl + "/customer/my-orders");
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
            return driver.findElement(pageHeading).getText().trim();
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

    public boolean isEmptyOrLoadingMessageVisible() {
        try {
            return driver.findElement(ordersMessage).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getMessageText() {
        try {
            return driver.findElement(ordersMessage).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public List<String> getOrderStatuses() {
        return driver.findElements(orderStatuses)
                .stream()
                .map(el -> el.getText().trim())
                .collect(Collectors.toList());
    }

    public String getFirstOrderStatus() {
        List<String> statuses = getOrderStatuses();
        return statuses.isEmpty() ? "" : statuses.get(0);
    }

    public List<String> getOrderTotals() {
        return driver.findElements(orderTotals)
                .stream()
                .map(el -> el.getText().trim())
                .collect(Collectors.toList());
    }

    /**
     * Clicks the Nth order card (0-based) — the entire card is the click
     * target that opens the details overlay (no separate button).
     */
    public void clickOrderCard(int index) {
        List<WebElement> cards = driver.findElements(orderCards);
        if (index < cards.size()) {
            clickRobustly(cards.get(index));
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
        clickRobustly(closeDetailsBtn);
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
    }

    public int getDetailProductCount() {
        return driver.findElements(detailsProducts).size();
    }

    /**
     * Counts active tracker steps scoped to a single order card (0-based
     * index), rather than across the whole page. With order history
     * accumulating across test runs, older orders further down the list
     * can be SHIPPED/DELIVERED and contribute their own active steps —
     * counting `.tracker-step-active` globally conflates all of them.
     */
    public int getActiveTrackerStepCountForOrder(int index) {
        List<WebElement> cards = driver.findElements(orderCards);
        if (index >= cards.size()) {
            return 0;
        }
        return cards.get(index)
                .findElements(By.cssSelector(".tracker-step-active"))
                .size();
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
