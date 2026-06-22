package com.Infosys.ecommerceApplication.automation.tests;

import com.Infosys.ecommerceApplication.automation.utils.BaseTest;
import com.Infosys.ecommerceApplication.automation.utils.CartTestUtils;
import com.Infosys.ecommerceApplication.automation.utils.WaitUtils;
import com.Infosys.ecommerceApplication.automation.pages.LoginPage;
import com.Infosys.ecommerceApplication.automation.pages.ProductListingPage;
import com.Infosys.ecommerceApplication.automation.pages.ProductDetailsPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Day 42 — US017: Validate Add to Cart (continued)
 * T090: Validate cart items
 * T091: Verify quantity & price
 *
 * These tests validate that after adding products to the cart:
 *  - The correct items are present in the cart (T090)
 *  - Quantities are correct and prices are valid/non-zero (T091)
 *  - The order total matches expected calculation (T091)
 */
public class ValidateCartItemsTest extends BaseTest {

    private static final String VALID_EMAIL    = "kartik@gmail.com";
    private static final String VALID_PASSWORD = "1234";

    // Cart page locators (based on CartPage.jsx markup).
    // The page has multiple <h2> elements: the navbar logo "ShopSphere"
    // (class="logo"), each cart item name, "Order Summary", "Your Cart is
    // Empty", and "Remove Product" (hidden modal heading).
    // We scope specifically to the cart item rows by selecting h2 elements
    // that are direct descendants of the item detail <div> (which has
    // inline style flex:1), identified by the sibling <h3> price element
    // that starts with ₹ — this pinpoints only the actual item-name h2s.
    private final By cartItemNames   = By.xpath(
        "//h2[following-sibling::h3[starts-with(normalize-space(.),'₹')]]"
    );
    // Item price → h3 starting with ₹ (each item has one — scoped before qty controls)
    private final By cartItemPrices  = By.xpath(
        "//h3[starts-with(normalize-space(text()),'₹') and not(contains(.,'Total'))]"
    );
    // Item quantity → h3 between the '-' and '+' buttons
    private final By cartItemQtys    = By.xpath(
        "//button[normalize-space(text())='+']/preceding-sibling::h3"
    );
    // Order total → span inside the "Total:" h3
    private final By orderTotalSpan  = By.xpath("//h3[contains(.,'Total:')]/span");
    // Empty cart heading
    private final By emptyCartMsg    = By.xpath("//h2[contains(text(),'Your Cart is Empty')]");

    /** Login, clear cart, add first product, navigate to cart. */
    private void loginAndAddOneProduct() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(BASE_URL);
        loginPage.loginAs(VALID_EMAIL, VALID_PASSWORD);
        try {
            WaitUtils.waitForUrlContains(driver, "customer");
        } catch (Exception e) {
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        }

        // Clear existing cart for a clean state. The cart is server-backed,
        // so clearing localStorage alone is not enough.
        driver.get(BASE_URL + "/customer/cart");
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        CartTestUtils.clearServerCart(driver, BASE_URL);
        driver.navigate().refresh();

        // Add the first product via the real Add to Cart flow
        ProductListingPage productsPage = new ProductListingPage(driver);
        productsPage.open(BASE_URL);
        try { Thread.sleep(2500); } catch (InterruptedException ignored) {}
        productsPage.openProductDetails(0);

        ProductDetailsPage detailsPage = new ProductDetailsPage(driver);
        Assert.assertTrue(detailsPage.isLoaded(), "Should be on a product details page");
        detailsPage.clickAddToCart();
        Assert.assertTrue(detailsPage.isAddedConfirmationVisible(), "Confirmation modal should appear");
        detailsPage.clickGoToCart();

        WaitUtils.waitForUrlContains(driver, "cart");
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
    }

    // ─────────────────────────────────────────
    // T090: Validate cart items
    // ─────────────────────────────────────────

    /**
     * T090: Cart is not empty after adding a product — at least one item
     * is listed.
     */
    @Test(description = "T090: Cart contains at least one item after adding a product")
    public void testCartIsNotEmpty() {
        loginAndAddOneProduct();

        List<WebElement> items = driver.findElements(cartItemNames);
        Assert.assertFalse(
            items.isEmpty(),
            "Cart should not be empty after adding a product"
        );
        System.out.println("[PASS] T090: Cart contains " + items.size() + " item(s).");
    }

    /**
     * T090: The product name displayed in the cart matches the name shown
     * on the product details page before adding to cart.
     */
    @Test(description = "T090: Cart item name matches the product that was added")
    public void testCartItemNameIsCorrect() {
        // Login and navigate to first product
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(BASE_URL);
        loginPage.loginAs(VALID_EMAIL, VALID_PASSWORD);
        try {
            WaitUtils.waitForUrlContains(driver, "customer");
        } catch (Exception e) {
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        }

        // Clear existing cart
        driver.get(BASE_URL + "/customer/cart");
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        CartTestUtils.clearServerCart(driver, BASE_URL);
        driver.navigate().refresh();

        // Open product details and record the product name before adding
        ProductListingPage productsPage = new ProductListingPage(driver);
        productsPage.open(BASE_URL);
        try { Thread.sleep(2500); } catch (InterruptedException ignored) {}
        productsPage.openProductDetails(0);

        ProductDetailsPage detailsPage = new ProductDetailsPage(driver);
        Assert.assertTrue(detailsPage.isLoaded(), "Should be on product details page");
        String productName = detailsPage.getProductName();
        System.out.println("[INFO] T090: Product added: " + productName);

        detailsPage.clickAddToCart();
        Assert.assertTrue(detailsPage.isAddedConfirmationVisible(), "Confirmation modal should appear");
        detailsPage.clickGoToCart();
        WaitUtils.waitForUrlContains(driver, "cart");
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}

        // T090: Verify the item name in the cart
        List<WebElement> cartNames = driver.findElements(cartItemNames);
        Assert.assertFalse(cartNames.isEmpty(), "Cart should have at least one item");

        boolean nameFound = cartNames.stream()
            .anyMatch(el -> el.getText().trim().equalsIgnoreCase(productName.trim()));
        Assert.assertTrue(
            nameFound,
            "Cart should contain '" + productName + "'. Items found: "
                + cartNames.stream().map(WebElement::getText).toList()
        );
        System.out.println("[PASS] T090: Cart item name '" + productName + "' correctly validated.");
    }

    /**
     * T090: Each cart item has all expected elements — name, price, and
     * quantity controls.
     */
    @Test(description = "T090: Each cart item shows name, price, and quantity controls")
    public void testCartItemsHaveRequiredElements() {
        loginAndAddOneProduct();

        List<WebElement> names = driver.findElements(cartItemNames);
        List<WebElement> prices = driver.findElements(cartItemPrices);
        List<WebElement> qtys   = driver.findElements(cartItemQtys);

        System.out.println("[INFO] T090: Names=" + names.size()
            + " Prices=" + prices.size() + " Qtys=" + qtys.size());

        Assert.assertFalse(names.isEmpty(),  "Cart should show at least one item name");
        Assert.assertFalse(prices.isEmpty(), "Cart should show at least one item price");
        Assert.assertFalse(qtys.isEmpty(),   "Cart should show at least one quantity value");

        // All three counts should match — one row per item
        Assert.assertEquals(
            names.size(), prices.size(),
            "Each cart item should have exactly one price. Names=" + names.size()
                + " Prices=" + prices.size()
        );
        Assert.assertEquals(
            names.size(), qtys.size(),
            "Each cart item should have exactly one quantity. Names=" + names.size()
                + " Qtys=" + qtys.size()
        );
        System.out.println("[PASS] T090: All required cart item elements (name, price, quantity) are present.");
    }

    /**
     * T090: The empty cart message is NOT visible when there are items in
     * the cart.
     */
    @Test(description = "T090: Empty cart message is hidden when cart has items")
    public void testEmptyCartMessageHiddenWhenItemsPresent() {
        loginAndAddOneProduct();

        List<WebElement> emptyMsgs = driver.findElements(emptyCartMsg);
        boolean emptyShown = emptyMsgs.stream().anyMatch(WebElement::isDisplayed);

        Assert.assertFalse(
            emptyShown,
            "\"Your Cart is Empty\" should NOT be shown when the cart has items"
        );
        System.out.println("[PASS] T090: Empty cart message correctly hidden when items are present.");
    }

    // ─────────────────────────────────────────
    // T091: Verify quantity & price
    // ─────────────────────────────────────────

    /**
     * T091: Default quantity for a newly added item is 1.
     */
    @Test(description = "T091: Newly added item has default quantity of 1")
    public void testDefaultQuantityIsOne() {
        loginAndAddOneProduct();

        List<WebElement> qtys = driver.findElements(cartItemQtys);
        Assert.assertFalse(qtys.isEmpty(), "At least one quantity element should exist");

        int qty = Integer.parseInt(qtys.get(0).getText().trim());
        Assert.assertEquals(qty, 1, "Default quantity for a newly added item should be 1");
        System.out.println("[PASS] T091: Default quantity is 1.");
    }

    /**
     * T091: Item price is a valid non-zero numeric value displayed with ₹.
     */
    @Test(description = "T091: Item price is a valid non-zero number starting with ₹")
    public void testItemPriceIsValidNumber() {
        loginAndAddOneProduct();

        List<WebElement> prices = driver.findElements(cartItemPrices);
        Assert.assertFalse(prices.isEmpty(), "At least one item price should be shown");

        for (WebElement priceEl : prices) {
            String raw = priceEl.getText().trim();
            System.out.println("[INFO] T091: Price text found: " + raw);

            Assert.assertTrue(
                raw.startsWith("₹"),
                "Price should start with ₹ symbol. Found: " + raw
            );

            // Strip ₹ and any commas, then parse as number
            String numericPart = raw.replace("₹", "").replace(",", "").trim();
            double price = Double.parseDouble(numericPart);
            Assert.assertTrue(
                price > 0,
                "Item price should be greater than 0. Found: " + price
            );
        }
        System.out.println("[PASS] T091: All item prices are valid non-zero numbers with ₹ symbol.");
    }

    /**
     * T091: Order total = item price × quantity (for a single item at qty 1).
     */
    @Test(description = "T091: Order total matches price × quantity for a single item")
    public void testOrderTotalMatchesPriceTimesQuantity() {
        loginAndAddOneProduct();

        // Get item price
        List<WebElement> prices = driver.findElements(cartItemPrices);
        Assert.assertFalse(prices.isEmpty(), "Item price should be present");
        double itemPrice = Double.parseDouble(
            prices.get(0).getText().trim().replace("₹", "").replace(",", "")
        );

        // Get item quantity
        List<WebElement> qtys = driver.findElements(cartItemQtys);
        Assert.assertFalse(qtys.isEmpty(), "Item quantity should be present");
        int quantity = Integer.parseInt(qtys.get(0).getText().trim());

        // Get order total
        WebElement totalEl = WaitUtils.waitForVisible(driver, orderTotalSpan);
        double orderTotal = Double.parseDouble(
            totalEl.getText().trim().replace("₹", "").replace(",", "")
        );

        double expectedTotal = itemPrice * quantity;

        System.out.println("[INFO] T091: Price=" + itemPrice
            + " Qty=" + quantity
            + " Expected total=" + expectedTotal
            + " Actual total=" + orderTotal);

        Assert.assertEquals(
            orderTotal, expectedTotal, 0.01,
            "Order total should equal price × quantity. Expected=" + expectedTotal
                + " Actual=" + orderTotal
        );
        System.out.println("[PASS] T091: Order total ₹" + orderTotal
            + " correctly equals price ₹" + itemPrice + " × qty " + quantity + ".");
    }

    /**
     * T091: Order total updates correctly when quantity is increased
     * (total should double when qty goes from 1 to 2).
     */
    @Test(description = "T091: Order total updates correctly when quantity changes")
    public void testOrderTotalUpdatesWithQuantity() {
        loginAndAddOneProduct();

        // Read total at qty=1
        WebElement totalEl = WaitUtils.waitForVisible(driver, orderTotalSpan);
        double totalAtQtyOne = Double.parseDouble(
            totalEl.getText().trim().replace("₹", "").replace(",", "")
        );
        System.out.println("[INFO] T091: Total at qty=1: ₹" + totalAtQtyOne);

        // Click "+" once to increase quantity to 2
        // Use JS click to avoid any Flowbite overlay interception
        WebElement plusBtn = WaitUtils.waitForClickable(driver,
            By.xpath("//button[normalize-space(text())='+']"));
        try {
            plusBtn.click();
        } catch (org.openqa.selenium.ElementClickInterceptedException e) {
            ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].click();", plusBtn);
        }
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}

        // Re-read total at qty=2
        double totalAtQtyTwo = Double.parseDouble(
            driver.findElement(orderTotalSpan).getText().trim()
                .replace("₹", "").replace(",", "")
        );
        System.out.println("[INFO] T091: Total at qty=2: ₹" + totalAtQtyTwo);

        Assert.assertNotEquals(
            totalAtQtyTwo, totalAtQtyOne,
            "Total at qty=2 should be different from total at qty=1"
        );
        Assert.assertEquals(
            totalAtQtyTwo, totalAtQtyOne * 2, 0.01,
            "Total at qty=2 should be exactly double the total at qty=1. "
                + "Expected=" + (totalAtQtyOne * 2) + " Actual=" + totalAtQtyTwo
        );
        System.out.println("[PASS] T091: Total correctly doubled when quantity increased from 1 to 2.");
    }
}
