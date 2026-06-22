package com.Infosys.ecommerceApplication.automation.pages;

import com.Infosys.ecommerceApplication.automation.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * T065: POM — Page Object for /customer/cart
 * T088/T089 (Day 41, US017): Validate Add to Cart
 * T090-T093 (Day 42, US018): Validate Cart Management
 *
 * The cart is now persisted server-side (MySQL "cart" table, tied to the
 * logged-in user via /api/cart/**), not in browser localStorage. The
 * frontend still keeps a localStorage["cart"] mirror purely as a read
 * cache for the navbar badge etc., but it is re-synced from the server on
 * every CartPage mount — so tests must clear/seed the cart through the
 * real /api/cart endpoints (see CartTestUtils) rather than by poking
 * localStorage directly. This page object itself only reads from the
 * rendered DOM, which is unaffected by where the data ultimately lives.
 */
public class CartPage {

    private final WebDriver driver;

    // Each cart row is built as: <div> [<img>, <div>[<h2>name</h2>, <p>category</p>,
    // <h3>price</h3>, qty controls, remove button]]. A text-exclusion locator
    // (excluding "Order Summary"/"Your Cart is Empty") is NOT safe here — the
    // navbar logo is also an <h2> (e.g. <h2 class="logo">ShopSphere</h2>) and
    // slips through any text-based exclusion. Instead, scope structurally: an
    // item-name <h2> always has a sibling <h3> price starting with ₹ in the
    // same row div. No other <h2> on the page (navbar logo, "Order Summary",
    // "Your Cart is Empty", "Remove Product" modal heading) has that sibling.
    private final By cartItemNames    = By.xpath(
        "//h2[following-sibling::h3[starts-with(normalize-space(.),'\u20B9')]]"
    );
    // Structural locator (mirrors the fix used for cartItemNames elsewhere): each
    // item price <h3> sits right after the item's name <h2> in the same row div,
    // and starts with ₹ but is never the "Total:" h3 in the Order Summary panel.
    private final By cartItemPrices   = By.xpath(
        "//h3[starts-with(normalize-space(.),'\u20B9') and not(contains(.,'Total'))]"
    );
    private final By emptyCartHeading = By.xpath("//h2[contains(text(),'Your Cart is Empty')]");
    private final By checkoutButton   = By.xpath("//button[contains(text(),'Proceed to Checkout')]");
    private final By orderTotal       = By.xpath("//h3[contains(.,'Total')]/span");

    // Quantity controls / remove button live inside the same row as the item's <h2>
    private final By increaseQtyButton = By.xpath("//button[normalize-space(text())='+']");
    private final By decreaseQtyButton = By.xpath("//button[normalize-space(text())='-']");
    private final By quantityValue     = By.xpath("//button[normalize-space(text())='+']/preceding-sibling::h3");
    private final By removeButton      = By.xpath("//button[contains(text(),'Remove')]");

    // "Remove Product" confirmation modal
    private final By removeModalHeading = By.xpath("//h2[contains(text(),'Remove Product')]");
    private final By yesRemoveButton    = By.xpath("//button[contains(text(),'Yes Remove')]");
    private final By cancelRemoveButton = By.xpath("//button[contains(text(),'Cancel')]");

    public CartPage(WebDriver driver) {
        this.driver = driver;
    }

    public void open(String baseUrl) {
        driver.get(baseUrl + "/customer/cart");
    }

    public boolean isLoaded() {
        return driver.getCurrentUrl().contains("/customer/cart");
    }

    /** T089: Returns the list of product names currently shown in the cart. */
    public List<String> getCartItemNames() {
        List<WebElement> names = driver.findElements(cartItemNames);
        return names.stream().map(WebElement::getText).filter(t -> !t.isBlank()).toList();
    }

    /** T089: Checks whether a specific product name appears in the cart. */
    public boolean containsProduct(String productName) {
        return getCartItemNames().stream()
            .anyMatch(name -> name.trim().equalsIgnoreCase(productName.trim()));
    }

    public int getCartItemCount() {
        return getCartItemNames().size();
    }

    /** T094: Returns the raw price text (e.g. "₹85000") for every cart item, in row order. */
    public List<String> getCartItemPriceTexts() {
        List<WebElement> prices = driver.findElements(cartItemPrices);
        return prices.stream().map(WebElement::getText).map(String::trim).toList();
    }

    /** T094: Returns each cart item's price parsed as a number (₹ symbol and commas stripped). */
    public List<Double> getCartItemPrices() {
        return getCartItemPriceTexts().stream()
            .map(t -> Double.parseDouble(t.replace("\u20B9", "").replace(",", "").trim()))
            .toList();
    }

    public boolean isEmptyCartMessageShown() {
        try {
            return WaitUtils.waitForVisible(driver, emptyCartHeading).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isCheckoutButtonVisible() {
        try {
            return WaitUtils.waitForVisible(driver, checkoutButton).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void clickCheckout() {
        clickRobustly(checkoutButton);
    }

    /** T090: Returns the quantity shown for the first cart item. */
    public int getFirstItemQuantity() {
        return Integer.parseInt(driver.findElement(quantityValue).getText().trim());
    }

    /** T090: Clicks the "+" button for the first cart item N times. */
    public void increaseFirstItemQuantity(int times) {
        for (int i = 0; i < times; i++) {
            WaitUtils.waitForClickable(driver, increaseQtyButton).click();
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
        }
    }

    /** T090: Clicks the "-" button for the first cart item N times. */
    public void decreaseFirstItemQuantity(int times) {
        for (int i = 0; i < times; i++) {
            WaitUtils.waitForClickable(driver, decreaseQtyButton).click();
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
        }
    }

    /** T091: Reads the order total amount (numeric value after the ₹ symbol). */
    public String getOrderTotalText() {
        return WaitUtils.waitForVisible(driver, orderTotal).getText().trim();
    }

    /** T092: Clicks "Remove" on the first cart item, opening the confirmation modal. */
    public void clickRemoveFirstItem() {
        WaitUtils.dismissBrowserPasswordDialogIfPresent(driver);
        clickRobustly(removeButton);
    }

    public boolean isRemoveConfirmationModalVisible() {
        try {
            return WaitUtils.waitForVisible(driver, removeModalHeading).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /** T092: Confirms removal in the "Remove Product" modal. */
    public void confirmRemove() {
        clickRobustly(yesRemoveButton);
    }

    /** T092: Cancels removal in the "Remove Product" modal. */
    public void cancelRemove() {
        clickRobustly(cancelRemoveButton);
    }

    /**
     * Flowbite's modal overlay can momentarily intercept clicks even after
     * Selenium reports the underlying element as clickable. Retry with a
     * JS click (bypasses the overlay hit-test) if intercepted.
     */
    private void clickRobustly(By locator) {
        WebElement el = WaitUtils.waitForClickable(driver, locator);
        try {
            el.click();
        } catch (org.openqa.selenium.ElementClickInterceptedException e) {
            try { Thread.sleep(400); } catch (InterruptedException ignored) {}
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        }
    }
}
