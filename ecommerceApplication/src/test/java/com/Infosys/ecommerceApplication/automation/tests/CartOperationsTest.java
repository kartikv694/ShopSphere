package com.Infosys.ecommerceApplication.automation.tests;

import com.Infosys.ecommerceApplication.automation.pages.CartPage;
import com.Infosys.ecommerceApplication.automation.pages.LoginPage;
import com.Infosys.ecommerceApplication.automation.pages.ProductDetailsPage;
import com.Infosys.ecommerceApplication.automation.pages.ProductListingPage;
import com.Infosys.ecommerceApplication.automation.utils.BaseTest;
import com.Infosys.ecommerceApplication.automation.utils.WaitUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Day 43 — US018: Validate Cart Operations
 * T092: Automate update cart
 * T093: Automate remove item
 * T094: Validate cart summary
 * T095: Handle edge cases
 *
 * Cart behavior under test (see CartPage.jsx):
 *  - "+" always increments quantity by 1.
 *  - "-" decrements quantity by 1, UNLESS quantity is already 1, in which
 *    case it does NOT decrement — it opens the "Remove Product" confirmation
 *    modal instead (decreaseQuantity() short-circuits before updateCart()).
 *  - Removing requires confirming in that same modal ("Yes Remove"); the
 *    modal can also be dismissed with "Cancel", leaving the cart unchanged.
 *  - Order total = sum(price * quantity) across all cart rows, recomputed
 *    on every render from cartItems state.
 */
public class CartOperationsTest extends BaseTest {

    private static final String VALID_EMAIL    = "kartik@gmail.com";
    private static final String VALID_PASSWORD  = "1234";

    /** Login, clear any existing cart, add the first product, land on /customer/cart. */
    private void loginAndAddOneProduct() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(BASE_URL);
        loginPage.loginAs(VALID_EMAIL, VALID_PASSWORD);
        try {
            WaitUtils.waitForUrlContains(driver, "customer");
        } catch (Exception e) {
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        }

        // Clear existing cart for a clean state
        driver.get(BASE_URL + "/customer/cart");
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        ((org.openqa.selenium.JavascriptExecutor) driver)
            .executeScript("window.localStorage.removeItem('cart');");
        driver.navigate().refresh();

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

    /** Empties the cart via localStorage and reloads, leaving the user on an empty /customer/cart. */
    private void clearCartAndReload() {
        ((org.openqa.selenium.JavascriptExecutor) driver)
            .executeScript("window.localStorage.removeItem('cart');");
        driver.navigate().refresh();
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
    }

    private double parseMoney(String text) {
        return Double.parseDouble(text.replace("₹", "").replace(",", "").trim());
    }

    // ─────────────────────────────────────────
    // T092: Automate update cart
    // ─────────────────────────────────────────

    /** T092: Clicking "+" increases quantity by exactly 1. */
    @Test(description = "T092: Clicking '+' increases item quantity by 1")
    public void testIncreaseQuantityByOne() {
        loginAndAddOneProduct();
        CartPage cartPage = new CartPage(driver);

        int before = cartPage.getFirstItemQuantity();
        cartPage.increaseFirstItemQuantity(1);
        int after = cartPage.getFirstItemQuantity();

        System.out.println("[INFO] T092: Quantity before=" + before + " after=" + after);
        Assert.assertEquals(after, before + 1, "Quantity should increase by exactly 1 after clicking '+'");
        System.out.println("[PASS] T092: Quantity correctly increased from " + before + " to " + after + ".");
    }

    /** T092: Clicking "+" multiple times accumulates correctly (not skipping/double counting). */
    @Test(description = "T092: Clicking '+' multiple times accumulates quantity correctly")
    public void testIncreaseQuantityMultipleTimes() {
        loginAndAddOneProduct();
        CartPage cartPage = new CartPage(driver);

        int before = cartPage.getFirstItemQuantity();
        cartPage.increaseFirstItemQuantity(3);
        int after = cartPage.getFirstItemQuantity();

        System.out.println("[INFO] T092: Quantity before=" + before + " after 3 clicks=" + after);
        Assert.assertEquals(after, before + 3, "Quantity should increase by exactly 3 after three '+' clicks");
        System.out.println("[PASS] T092: Quantity correctly accumulated to " + after + " after 3 increases.");
    }

    /** T092: Clicking "-" on a quantity > 1 decreases it by exactly 1. */
    @Test(description = "T092: Clicking '-' decreases item quantity by 1 when quantity > 1")
    public void testDecreaseQuantityByOne() {
        loginAndAddOneProduct();
        CartPage cartPage = new CartPage(driver);

        // Bring quantity to 2 first so decreasing doesn't trigger the remove modal
        cartPage.increaseFirstItemQuantity(1);
        int before = cartPage.getFirstItemQuantity();
        Assert.assertEquals(before, 2, "Quantity should be 2 before testing decrease");

        cartPage.decreaseFirstItemQuantity(1);
        int after = cartPage.getFirstItemQuantity();

        System.out.println("[INFO] T092: Quantity before=" + before + " after=" + after);
        Assert.assertEquals(after, before - 1, "Quantity should decrease by exactly 1 after clicking '-'");
        Assert.assertFalse(
            cartPage.isRemoveConfirmationModalVisible(),
            "Remove modal should NOT appear when decreasing from a quantity above 1"
        );
        System.out.println("[PASS] T092: Quantity correctly decreased from " + before + " to " + after + ".");
    }

    /** T092: Quantity updates persist after a page refresh (cart is backed by localStorage). */
    @Test(description = "T092: Updated quantity persists after page refresh")
    public void testQuantityPersistsAfterRefresh() {
        loginAndAddOneProduct();
        CartPage cartPage = new CartPage(driver);

        cartPage.increaseFirstItemQuantity(2);
        int beforeRefresh = cartPage.getFirstItemQuantity();

        driver.navigate().refresh();
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}
        int afterRefresh = cartPage.getFirstItemQuantity();

        System.out.println("[INFO] T092: Quantity before refresh=" + beforeRefresh + " after refresh=" + afterRefresh);
        Assert.assertEquals(afterRefresh, beforeRefresh, "Quantity should remain unchanged after a page refresh");
        System.out.println("[PASS] T092: Quantity correctly persisted (" + afterRefresh + ") across refresh.");
    }

    // ─────────────────────────────────────────
    // T093: Automate remove item
    // ─────────────────────────────────────────

    /** T093: Confirming removal via "Remove" -> "Yes Remove" removes the item and shows the empty cart message. */
    @Test(description = "T093: Confirming 'Remove' deletes the item from the cart")
    public void testRemoveItemViaRemoveButton() {
        loginAndAddOneProduct();
        CartPage cartPage = new CartPage(driver);

        Assert.assertEquals(cartPage.getCartItemCount(), 1, "Cart should have exactly 1 item before removal");

        cartPage.clickRemoveFirstItem();
        Assert.assertTrue(cartPage.isRemoveConfirmationModalVisible(), "Remove confirmation modal should appear");
        cartPage.confirmRemove();
        try { Thread.sleep(600); } catch (InterruptedException ignored) {}

        Assert.assertTrue(cartPage.isEmptyCartMessageShown(), "Cart should show empty state after item is removed");
        Assert.assertEquals(cartPage.getCartItemCount(), 0, "Cart should have 0 items after removal");
        System.out.println("[PASS] T093: Item correctly removed via Remove -> Yes Remove.");
    }

    /** T093: Decreasing quantity from 1 also opens the remove confirmation modal (per app's decreaseQuantity logic). */
    @Test(description = "T093: Decreasing quantity from 1 opens the remove confirmation modal")
    public void testDecreaseFromOneOpensRemoveModal() {
        loginAndAddOneProduct();
        CartPage cartPage = new CartPage(driver);

        Assert.assertEquals(cartPage.getFirstItemQuantity(), 1, "Newly added item should start at quantity 1");

        cartPage.decreaseFirstItemQuantity(1);
        try { Thread.sleep(400); } catch (InterruptedException ignored) {}

        Assert.assertTrue(
            cartPage.isRemoveConfirmationModalVisible(),
            "Clicking '-' at quantity 1 should open the Remove Product confirmation modal"
        );
        // Quantity itself must remain 1 — the modal intercepts before any state change.
        cartPage.cancelRemove();
        try { Thread.sleep(400); } catch (InterruptedException ignored) {}
        Assert.assertEquals(cartPage.getFirstItemQuantity(), 1, "Quantity should remain 1 after cancelling the remove modal");
        System.out.println("[PASS] T093: '-' at quantity 1 correctly opened the remove modal without changing quantity.");
    }

    /** T093: Cancelling the remove confirmation modal leaves the cart item intact. */
    @Test(description = "T093: Cancelling removal keeps the item in the cart")
    public void testCancelRemoveKeepsItem() {
        loginAndAddOneProduct();
        CartPage cartPage = new CartPage(driver);

        List<String> beforeNames = cartPage.getCartItemNames();
        cartPage.clickRemoveFirstItem();
        Assert.assertTrue(cartPage.isRemoveConfirmationModalVisible(), "Remove confirmation modal should appear");

        cartPage.cancelRemove();
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}

        Assert.assertFalse(cartPage.isEmptyCartMessageShown(), "Cart should NOT be empty after cancelling removal");
        List<String> afterNames = cartPage.getCartItemNames();
        Assert.assertEquals(afterNames, beforeNames, "Cart contents should be unchanged after cancelling removal");
        System.out.println("[PASS] T093: Cancel correctly preserved the cart item: " + afterNames);
    }

    // ─────────────────────────────────────────
    // T094: Validate cart summary
    // ─────────────────────────────────────────

    /** T094: Order Summary total equals price × quantity for a single-item cart. */
    @Test(description = "T094: Cart summary total matches price x quantity")
    public void testCartSummaryTotalMatchesPriceTimesQuantity() {
        loginAndAddOneProduct();
        CartPage cartPage = new CartPage(driver);

        List<Double> prices = cartPage.getCartItemPrices();
        Assert.assertFalse(prices.isEmpty(), "Cart should show at least one item price");
        double itemPrice = prices.get(0);
        int quantity = cartPage.getFirstItemQuantity();

        double expectedTotal = itemPrice * quantity;
        double actualTotal = parseMoney(cartPage.getOrderTotalText());

        System.out.println("[INFO] T094: price=" + itemPrice + " qty=" + quantity
            + " expectedTotal=" + expectedTotal + " actualTotal=" + actualTotal);
        Assert.assertEquals(actualTotal, expectedTotal, 0.01,
            "Order Summary total should equal price × quantity");
        System.out.println("[PASS] T094: Cart summary total ₹" + actualTotal + " correctly matches price × quantity.");
    }

    /** T094: Cart summary total recalculates correctly after a quantity change. */
    @Test(description = "T094: Cart summary recalculates after quantity update")
    public void testCartSummaryUpdatesAfterQuantityChange() {
        loginAndAddOneProduct();
        CartPage cartPage = new CartPage(driver);

        double totalBefore = parseMoney(cartPage.getOrderTotalText());
        cartPage.increaseFirstItemQuantity(2); // qty 1 -> 3
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        double totalAfter = parseMoney(cartPage.getOrderTotalText());

        System.out.println("[INFO] T094: totalBefore=" + totalBefore + " totalAfter=" + totalAfter);
        Assert.assertEquals(totalAfter, totalBefore * 3, 0.01,
            "Total should be exactly 3x the original after increasing quantity from 1 to 3");
        System.out.println("[PASS] T094: Cart summary correctly recalculated to ₹" + totalAfter + " after quantity change.");
    }

    /** T094: Cart summary total recalculates correctly after removing the only item. */
    @Test(description = "T094: Cart summary shows zero/empty state after removing the only item")
    public void testCartSummaryAfterRemovingOnlyItem() {
        loginAndAddOneProduct();
        CartPage cartPage = new CartPage(driver);

        cartPage.clickRemoveFirstItem();
        cartPage.confirmRemove();
        try { Thread.sleep(600); } catch (InterruptedException ignored) {}

        Assert.assertTrue(cartPage.isEmptyCartMessageShown(),
            "Empty cart message should show once the only item is removed");
        Assert.assertFalse(cartPage.isCheckoutButtonVisible(),
            "Checkout button (part of Order Summary) should not be visible when cart is empty");
        System.out.println("[PASS] T094: Cart summary correctly reflects empty state after removing the only item.");
    }

    /** T094: The checkout button is visible and enabled whenever the cart has at least one item. */
    @Test(description = "T094: Checkout button is available when cart has items")
    public void testCheckoutButtonVisibleWithItems() {
        loginAndAddOneProduct();
        CartPage cartPage = new CartPage(driver);

        Assert.assertTrue(cartPage.isCheckoutButtonVisible(),
            "Checkout button should be visible in Order Summary when the cart has items");
        System.out.println("[PASS] T094: Checkout button correctly visible with items in cart.");
    }

    // ─────────────────────────────────────────
    // T095: Handle edge cases
    // ─────────────────────────────────────────

    /** T095: Visiting the cart directly with nothing added shows the empty cart message, not an error. */
    @Test(description = "T095: Empty cart (never added anything) shows empty message cleanly")
    public void testEmptyCartShowsEmptyMessageWithNoErrors() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(BASE_URL);
        loginPage.loginAs(VALID_EMAIL, VALID_PASSWORD);
        try {
            WaitUtils.waitForUrlContains(driver, "customer");
        } catch (Exception e) {
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        }

        CartPage cartPage = new CartPage(driver);
        cartPage.open(BASE_URL);
        // Ensure a clean slate regardless of leftover state from earlier runs
        clearCartAndReload();

        Assert.assertTrue(cartPage.isEmptyCartMessageShown(), "Empty cart message should be shown");
        Assert.assertEquals(cartPage.getCartItemCount(), 0, "Item count should be 0 on an empty cart");
        Assert.assertFalse(cartPage.isCheckoutButtonVisible(), "Checkout should not be offered on an empty cart");
        System.out.println("[PASS] T095: Empty cart correctly shows empty state with no checkout option.");
    }

    /** T095: Quantity never silently drops to 0 — decreasing at 1 always routes through the remove modal. */
    @Test(description = "T095: Quantity never reaches 0 without explicit removal confirmation")
    public void testQuantityNeverSilentlyReachesZero() {
        loginAndAddOneProduct();
        CartPage cartPage = new CartPage(driver);

        Assert.assertEquals(cartPage.getFirstItemQuantity(), 1, "Item should start at quantity 1");

        // Attempt to decrease below 1 several times without confirming removal.
        // Each iteration verifies state immediately, rather than only checking
        // at the end — so if one iteration breaks, we know exactly which one.
        for (int i = 0; i < 3; i++) {
            cartPage.decreaseFirstItemQuantity(1);

            boolean modalAppeared = cartPage.isRemoveConfirmationModalVisible();
            Assert.assertTrue(modalAppeared,
                "Iteration " + i + ": clicking '-' at quantity 1 should open the remove modal");

            cartPage.cancelRemove();

            // Wait for the modal to actually disappear (not just for the click to fire)
            // before trusting any subsequent read of quantity/cart state.
            boolean modalClosed = false;
            for (int wait = 0; wait < 10; wait++) {
                if (!cartPage.isRemoveConfirmationModalVisible()) {
                    modalClosed = true;
                    break;
                }
                try { Thread.sleep(300); } catch (InterruptedException ignored) {}
            }
            Assert.assertTrue(modalClosed, "Iteration " + i + ": remove modal should close after clicking Cancel");

            int qtyAfterCancel = cartPage.getFirstItemQuantity();
            System.out.println("[INFO] T095: After cancel on iteration " + i + ", quantity=" + qtyAfterCancel);
            Assert.assertEquals(qtyAfterCancel, 1,
                "Iteration " + i + ": quantity should still be 1 immediately after cancelling removal");
        }

        Assert.assertFalse(cartPage.isEmptyCartMessageShown(), "Item should still be in the cart");
        Assert.assertEquals(cartPage.getFirstItemQuantity(), 1,
            "Quantity should remain exactly 1 — it must never silently drop to 0 or negative");
        System.out.println("[PASS] T095: Quantity correctly stayed at 1 despite repeated '-' attempts without confirming removal.");
    }

    /** T095: Removing an item then re-adding the same product starts a fresh quantity of 1, not a stale value. */
    @Test(description = "T095: Re-adding a removed product resets quantity to 1")
    public void testReAddingRemovedProductResetsQuantity() {
        loginAndAddOneProduct();
        CartPage cartPage = new CartPage(driver);

        cartPage.increaseFirstItemQuantity(2); // qty -> 3
        Assert.assertEquals(cartPage.getFirstItemQuantity(), 3, "Quantity should be 3 before removal");

        cartPage.clickRemoveFirstItem();
        cartPage.confirmRemove();
        try { Thread.sleep(600); } catch (InterruptedException ignored) {}
        Assert.assertTrue(cartPage.isEmptyCartMessageShown(), "Cart should be empty after removal");

        // Re-add the same product fresh
        ProductListingPage productsPage = new ProductListingPage(driver);
        productsPage.open(BASE_URL);
        try { Thread.sleep(2500); } catch (InterruptedException ignored) {}
        productsPage.openProductDetails(0);

        ProductDetailsPage detailsPage = new ProductDetailsPage(driver);
        detailsPage.clickAddToCart();
        Assert.assertTrue(detailsPage.isAddedConfirmationVisible(), "Confirmation modal should appear on re-add");
        detailsPage.clickGoToCart();
        WaitUtils.waitForUrlContains(driver, "cart");
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}

        int freshQuantity = cartPage.getFirstItemQuantity();
        System.out.println("[INFO] T095: Quantity after re-adding a previously removed product: " + freshQuantity);
        Assert.assertEquals(freshQuantity, 1,
            "Re-added product should start at quantity 1, not carry over the old quantity of 3");
        System.out.println("[PASS] T095: Re-added product correctly reset to quantity 1.");
    }

    /** T095: Rapid repeated '+' clicks all register — no clicks are dropped under quick succession. */
    @Test(description = "T095: Rapid successive '+' clicks all register correctly")
    public void testRapidIncreaseClicksAllRegister() {
        loginAndAddOneProduct();
        CartPage cartPage = new CartPage(driver);

        int before = cartPage.getFirstItemQuantity();
        // 5 increments back-to-back with only a short pause between each
        cartPage.increaseFirstItemQuantity(5);
        int after = cartPage.getFirstItemQuantity();

        System.out.println("[INFO] T095: Quantity before=" + before + " after 5 rapid '+' clicks=" + after);
        Assert.assertEquals(after, before + 5,
            "All 5 rapid '+' clicks should register; none should be dropped or double-counted");
        System.out.println("[PASS] T095: All 5 rapid increase clicks correctly registered.");
    }
}
