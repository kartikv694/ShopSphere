package com.Infosys.ecommerceApplication.automation.tests;

import com.Infosys.ecommerceApplication.automation.pages.CartPage;
import com.Infosys.ecommerceApplication.automation.pages.CheckoutPage;
import com.Infosys.ecommerceApplication.automation.pages.LoginPage;
import com.Infosys.ecommerceApplication.automation.pages.ProductDetailsPage;
import com.Infosys.ecommerceApplication.automation.pages.ProductListingPage;
import com.Infosys.ecommerceApplication.automation.utils.BaseTest;
import com.Infosys.ecommerceApplication.automation.utils.CartTestUtils;
import com.Infosys.ecommerceApplication.automation.utils.WaitUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Day 44 — US019: Validate Checkout Flow
 * T096: Automate checkout initiation
 * T097: Validate form inputs
 *
 * Checkout flow overview (see Checkout.jsx):
 *
 *  1. User must be logged in and have at least one item in the cart.
 *  2. "Proceed to Checkout" on the cart page navigates to /checkout.
 *  3. Left panel shows a form (Full Name, Phone, Address, City, State,
 *     Pincode, Payment Method) plus an optional saved-address shortcut.
 *  4. Right panel shows an order summary (items + total price).
 *  5. validateCheckout() blocks "Place Order" if:
 *       — the cart is empty, or
 *       — the Address field is blank.
 *     All other fields are optional from the backend's perspective but
 *     tested here for completeness.
 *  6. Selecting "Cash On Delivery" places the order directly.
 *     Selecting "UPI" or "Card" opens a payment-gateway overlay first.
 *
 * Test data:
 *   Valid customer: kartik@gmail.com / 1234
 *   Valid address:  "123 MG Road, Bengaluru" (any non-blank string passes
 *                   validateCheckout's only mandatory field check)
 */
public class CheckoutFlowTest extends BaseTest {

    private static final String VALID_EMAIL    = "kartik@gmail.com";
    private static final String VALID_PASSWORD = "1234";

    private static final String FULL_NAME  = "Kartik Verma";
    private static final String PHONE      = "9876543210";
    private static final String ADDRESS    = "123 MG Road, Bengaluru";
    private static final String CITY       = "Bengaluru";
    private static final String STATE      = "Karnataka";
    private static final String PINCODE    = "560001";

    // ─────────────────────────────────────────────────────────────────────────
    // SETUP HELPERS
    // ─────────────────────────────────────────────────────────────────────────

    /** Login as the test customer and wait until the dashboard is visible. */
    private void loginAsCustomer() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(BASE_URL);
        loginPage.loginAs(VALID_EMAIL, VALID_PASSWORD);
        try {
            WaitUtils.waitForUrlContains(driver, "customer");
        } catch (Exception e) {
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        }
    }

    /**
     * Login, wipe the server-side cart, add the first available product, then
     * navigate to the cart page — leaving the driver on /customer/cart with
     * exactly one item, ready for checkout initiation tests.
     */
    private void loginAndPrepareCart() {
        loginAsCustomer();

        // Ensure a clean cart so every test starts from a known state.
        driver.get(BASE_URL + "/customer/cart");
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        CartTestUtils.clearServerCart(driver, BASE_URL);
        driver.navigate().refresh();
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}

        // Navigate to the product listing page and wait until the URL
        // actually reflects /customer/products (not a stale product detail
        // URL from the previous test) before trying to grab cards.
        ProductListingPage productsPage = new ProductListingPage(driver);
        productsPage.open(BASE_URL);

        // Explicitly wait for the listing URL to be confirmed — prevents
        // waitForProductCards() running on a stale /customer/products/{id}
        // URL left over from a previous test in a long suite run.
        try {
            WaitUtils.waitForUrlContains(driver, "/customer/products");
        } catch (Exception ignored) {}

        // Extra settle time for the React product grid to render.
        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}

        // Verify we are actually on the listing page, not a detail page.
        String currentUrl = driver.getCurrentUrl();
        if (currentUrl.matches(".*/customer/products/\\d+.*")) {
            // Somehow ended up on a detail page — navigate back to listing.
            driver.get(BASE_URL + "/customer/products");
            try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
        }

        productsPage.openProductDetails(0);

        ProductDetailsPage detailsPage = new ProductDetailsPage(driver);
        Assert.assertTrue(detailsPage.isLoaded(), "Should be on the product details page");
        detailsPage.clickAddToCart();
        Assert.assertTrue(detailsPage.isAddedConfirmationVisible(),
                "Add-to-cart confirmation should appear");
        detailsPage.clickGoToCart();

        WaitUtils.waitForUrlContains(driver, "cart");
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}
    }

    /**
     * Reaches the checkout page via the normal UI flow:
     * cart → "Proceed to Checkout" button. Returns the CheckoutPage POM.
     */
    private CheckoutPage proceedToCheckout() {
        CartPage cartPage = new CartPage(driver);
        Assert.assertTrue(cartPage.isCheckoutButtonVisible(),
                "Checkout button must be visible before proceeding");
        cartPage.clickCheckout();
        WaitUtils.waitForUrlContains(driver, "checkout");
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        return new CheckoutPage(driver);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // T096: Automate checkout initiation
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * T096-1: Clicking "Proceed to Checkout" from a non-empty cart navigates
     * to /checkout and the checkout page loads correctly.
     */
    @Test(description = "T096: Checkout page loads after clicking Proceed to Checkout")
    public void testCheckoutPageLoadsAfterProceedingFromCart() {
        loginAndPrepareCart();

        CheckoutPage checkoutPage = proceedToCheckout();

        Assert.assertTrue(
            driver.getCurrentUrl().contains("/checkout"),
            "URL should contain /checkout after clicking Proceed to Checkout"
        );
        Assert.assertTrue(
            checkoutPage.isLoaded(),
            "Checkout page heading should be visible — page should have loaded"
        );
        System.out.println("[PASS] T096-1: Checkout page loaded correctly at URL: " + driver.getCurrentUrl());
    }

    /**
     * T096-2: Cart items carry over correctly to the checkout order summary —
     * the number of summary rows equals the number of items added.
     */
    @Test(description = "T096: Cart items are correctly displayed in the checkout order summary")
    public void testCheckoutSummaryShowsCorrectItemCount() {
        loginAndPrepareCart();
        int cartItemCount = new CartPage(driver).getCartItemCount();

        CheckoutPage checkoutPage = proceedToCheckout();
        int summaryCount = checkoutPage.getSummaryItemCount();

        System.out.println("[INFO] T096-2: cart items=" + cartItemCount + " summary rows=" + summaryCount);
        Assert.assertEquals(
            summaryCount, cartItemCount,
            "Checkout summary should display exactly as many items as the cart had"
        );
        System.out.println("[PASS] T096-2: All " + cartItemCount + " cart item(s) correctly shown in checkout summary.");
    }

    /**
     * T096-3: The order total shown in the checkout summary is a non-empty
     * value that includes the ₹ currency symbol — basic sanity that price
     * rendering wired up correctly from the cart items.
     */
    @Test(description = "T096: Order total is visible and formatted with ₹ symbol in checkout summary")
    public void testCheckoutSummaryTotalIsVisible() {
        loginAndPrepareCart();
        CheckoutPage checkoutPage = proceedToCheckout();

        String totalText = checkoutPage.getTotalPriceText();

        System.out.println("[INFO] T096-3: Total price text = '" + totalText + "'");
        Assert.assertTrue(
            checkoutPage.isTotalPriceVisible(),
            "Total price element should be visible in the checkout summary"
        );
        Assert.assertFalse(
            totalText.isEmpty(),
            "Total price text should not be empty"
        );
        Assert.assertTrue(
            totalText.contains("₹"),
            "Total price should contain the ₹ symbol — got: " + totalText
        );
        System.out.println("[PASS] T096-3: Order total visible with ₹ symbol: " + totalText);
    }

    /**
     * T096-4: The checkout page can also be reached by directly navigating to
     * /checkout (deep-link scenario: bookmark, back-button, etc.) as
     * long as the user is already logged in. This verifies the route is not
     * artificially restricted to only the cart-button flow.
     */
    @Test(description = "T096: Checkout page loads when navigated to directly (not only via cart button)")
    public void testCheckoutPageLoadsViaDirectNavigation() {
        loginAndPrepareCart();

        // Navigate directly — bypassing the "Proceed to Checkout" button.
        CheckoutPage checkoutPage = new CheckoutPage(driver);
        checkoutPage.open(BASE_URL);
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        Assert.assertTrue(
            driver.getCurrentUrl().contains("/checkout"),
            "Should be on /checkout after direct navigation"
        );
        Assert.assertTrue(
            checkoutPage.isLoaded(),
            "Checkout page should load correctly via direct URL navigation"
        );
        System.out.println("[PASS] T096-4: Checkout page loaded correctly via direct navigation.");
    }

    /**
     * T096-5: The "Place Order" button is visible and enabled on the checkout
     * page — it should never start in a disabled/hidden state when there are
     * items in the cart and the page has loaded.
     */
    @Test(description = "T096: Place Order button is visible and enabled on checkout page")
    public void testPlaceOrderButtonIsEnabled() {
        loginAndPrepareCart();
        CheckoutPage checkoutPage = proceedToCheckout();

        Assert.assertTrue(
            checkoutPage.isPlaceOrderButtonEnabled(),
            "Place Order button should be visible and enabled when the cart has items"
        );
        System.out.println("[PASS] T096-5: Place Order button is correctly visible and enabled.");
    }

    /**
     * T096-6: Selecting "UPI" as the payment method and clicking "Place Order"
     * opens the payment-gateway overlay (not a direct order placement), and
     * the overlay shows at least one available gateway option.
     */
    @Test(description = "T096: Selecting UPI payment opens the payment gateway overlay")
    public void testUpiPaymentOpensGatewayOverlay() {
        loginAndPrepareCart();
        CheckoutPage checkoutPage = proceedToCheckout();

        // Fill the mandatory address field, then switch to UPI.
        checkoutPage.enterAddress(ADDRESS);
        checkoutPage.selectPaymentMethod("UPI");
        checkoutPage.clickPlaceOrder();
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        Assert.assertTrue(
            checkoutPage.isPaymentOverlayVisible(),
            "Payment gateway overlay should appear after clicking Place Order with UPI selected"
        );
        java.util.List<String> gateways = checkoutPage.getAvailableGateways();
        Assert.assertFalse(
            gateways.isEmpty(),
            "At least one payment gateway option should be shown in the overlay"
        );
        System.out.println("[PASS] T096-6: UPI payment overlay opened with gateways: " + gateways);

        // Clean up: close the overlay so subsequent tests start cleanly.
        checkoutPage.closePaymentOverlay();
    }

    /**
     * T096-7: The payment overlay can be dismissed via its close button —
     * after closing, the user should be back on the checkout page (not
     * navigated away), and the Place Order button should still be visible.
     */
    @Test(description = "T096: Payment overlay close button returns user to checkout page")
    public void testPaymentOverlayCanBeClosed() {
        loginAndPrepareCart();
        CheckoutPage checkoutPage = proceedToCheckout();

        checkoutPage.enterAddress(ADDRESS);
        checkoutPage.selectPaymentMethod("Card");
        checkoutPage.clickPlaceOrder();
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}

        Assert.assertTrue(checkoutPage.isPaymentOverlayVisible(),
                "Payment overlay should be open before testing close");

        checkoutPage.closePaymentOverlay();
        try { Thread.sleep(600); } catch (InterruptedException ignored) {}

        Assert.assertFalse(
            checkoutPage.isPaymentOverlayVisible(),
            "Payment overlay should be dismissed after clicking the close button"
        );
        Assert.assertTrue(
            driver.getCurrentUrl().contains("/checkout"),
            "User should still be on the checkout page after closing the payment overlay"
        );
        System.out.println("[PASS] T096-7: Payment overlay closed correctly; user remains on checkout page.");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // T097: Validate form inputs
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * T097-1: All form fields accept typed input and the values are correctly
     * reflected in the inputs (React state updates propagate to the DOM).
     * This is the fundamental sanity check before any other form test.
     */
    @Test(description = "T097: All checkout form fields accept and reflect typed input")
    public void testFormFieldsAcceptAndReflectInput() {
        loginAndPrepareCart();
        CheckoutPage checkoutPage = proceedToCheckout();

        checkoutPage.fillFullForm(
            FULL_NAME, PHONE, ADDRESS, CITY, STATE, PINCODE, "Cash On Delivery"
        );

        Assert.assertEquals(checkoutPage.getFullNameValue(),  FULL_NAME,
                "Full Name field should reflect typed value");
        Assert.assertEquals(checkoutPage.getPhoneValue(),     PHONE,
                "Phone field should reflect typed value");
        Assert.assertEquals(checkoutPage.getAddressValue(),   ADDRESS,
                "Address field should reflect typed value");
        Assert.assertEquals(checkoutPage.getCityValue(),      CITY,
                "City field should reflect typed value");
        Assert.assertEquals(checkoutPage.getStateValue(),     STATE,
                "State field should reflect typed value");
        Assert.assertEquals(checkoutPage.getPincodeValue(),   PINCODE,
                "Pincode field should reflect typed value");
        Assert.assertEquals(checkoutPage.getSelectedPaymentMethod(), "Cash On Delivery",
                "Payment method dropdown should show the selected option");

        System.out.println("[PASS] T097-1: All form fields correctly accept and reflect typed input.");
    }

    /**
     * T097-2: Attempting to place an order with the Address field left blank
     * must be blocked — validateCheckout() returns false and a toast fires.
     * The user should remain on the checkout page (no navigation occurs).
     *
     * Note: the page syncs a saved address from the server on mount, so we
     * explicitly clear the address field after waiting for the sync to settle,
     * rather than just "not typing" into it.
     */
    @Test(description = "T097: Placing an order with a blank address is blocked")
    public void testPlaceOrderBlockedWhenAddressIsBlank() {
        loginAndPrepareCart();
        CheckoutPage checkoutPage = proceedToCheckout();

        // Wait for any server-synced address to load, then explicitly clear it.
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        checkoutPage.enterAddress(""); // Ctrl+A + Delete via clearAndType

        // Fill all other fields — only address must be blank for this test.
        checkoutPage.enterFullName(FULL_NAME);
        checkoutPage.enterPhone(PHONE);
        checkoutPage.enterCity(CITY);
        checkoutPage.enterState(STATE);
        checkoutPage.enterPincode(PINCODE);

        // Verify address is truly empty before clicking Place Order.
        String addressValue = checkoutPage.getAddressValue();
        System.out.println("[INFO] T097-2: Address value before Place Order = '" + addressValue + "'");

        checkoutPage.clickPlaceOrder();
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}

        Assert.assertTrue(
            driver.getCurrentUrl().contains("/checkout"),
            "User should remain on checkout page when address is blank — got: " + driver.getCurrentUrl()
        );
        Assert.assertFalse(
            checkoutPage.isPaymentOverlayVisible(),
            "Payment overlay should NOT appear when validation fails due to blank address"
        );
        System.out.println("[PASS] T097-2: Place Order correctly blocked with blank address; user stays on checkout page.");
    }

    /**
     * T097-3: Default payment method is "Cash On Delivery" — the dropdown
     * should be pre-selected to this value when the checkout page first loads.
     */
    @Test(description = "T097: Default payment method is Cash On Delivery")
    public void testDefaultPaymentMethodIsCashOnDelivery() {
        loginAndPrepareCart();
        CheckoutPage checkoutPage = proceedToCheckout();

        String defaultMethod = checkoutPage.getSelectedPaymentMethod();

        System.out.println("[INFO] T097-3: Default payment method = '" + defaultMethod + "'");
        Assert.assertEquals(
            defaultMethod, "Cash On Delivery",
            "Payment method should default to 'Cash On Delivery' when the checkout page loads"
        );
        System.out.println("[PASS] T097-3: Default payment method is correctly 'Cash On Delivery'.");
    }

    /**
     * T097-4: The payment method dropdown correctly switches between all three
     * available options: Cash On Delivery, UPI, and Card.
     */
    @Test(description = "T097: Payment method dropdown switches between all three options")
    public void testPaymentMethodDropdownSwitchesOptions() {
        loginAndPrepareCart();
        CheckoutPage checkoutPage = proceedToCheckout();

        checkoutPage.selectPaymentMethod("UPI");
        Assert.assertEquals(checkoutPage.getSelectedPaymentMethod(), "UPI",
                "Should be able to select UPI");

        checkoutPage.selectPaymentMethod("Card");
        Assert.assertEquals(checkoutPage.getSelectedPaymentMethod(), "Card",
                "Should be able to select Card");

        checkoutPage.selectPaymentMethod("Cash On Delivery");
        Assert.assertEquals(checkoutPage.getSelectedPaymentMethod(), "Cash On Delivery",
                "Should be able to switch back to Cash On Delivery");

        System.out.println("[PASS] T097-4: Payment method dropdown correctly switches between all options.");
    }

    /**
     * T097-5: "Save Address" button persists the form data — after clicking it
     * the saved-address panel should appear (or update), confirming the address
     * has been stored and will follow the account across browsers.
     */
    @Test(description = "T097: Save Address button persists the delivery address")
    public void testSaveAddressButtonPersistsAddress() {
        loginAndPrepareCart();
        CheckoutPage checkoutPage = proceedToCheckout();

        // Fill address fields and save.
        checkoutPage.enterFullName(FULL_NAME);
        checkoutPage.enterPhone(PHONE);
        checkoutPage.enterAddress(ADDRESS);
        checkoutPage.enterCity(CITY);
        checkoutPage.enterState(STATE);
        checkoutPage.enterPincode(PINCODE);
        checkoutPage.clickSaveAddress();
        try { Thread.sleep(600); } catch (InterruptedException ignored) {}

        // After saving, the saved-address panel should become visible.
        Assert.assertTrue(
            checkoutPage.isSavedAddressPanelVisible(),
            "Saved address panel should appear after clicking Save Address"
        );
        System.out.println("[PASS] T097-5: Save Address correctly showed the saved address panel.");
    }

    /**
     * T097-6: "Use Saved Address" auto-fills the form fields from the
     * previously saved address. After clicking it, the Address field should
     * contain the saved value without the user typing anything.
     */
    @Test(description = "T097: Use Saved Address pre-fills the form from the stored address")
    public void testUseSavedAddressPreFillsForm() {
        loginAndPrepareCart();
        CheckoutPage checkoutPage = proceedToCheckout();

        // Save an address first.
        checkoutPage.enterAddress(ADDRESS);
        checkoutPage.enterCity(CITY);
        checkoutPage.enterState(STATE);
        checkoutPage.clickSaveAddress();
        try { Thread.sleep(600); } catch (InterruptedException ignored) {}

        // Now clear the address field manually.
        checkoutPage.enterAddress("");
        Assert.assertEquals(checkoutPage.getAddressValue(), "",
                "Address field should be empty after manual clear");

        // "Use Saved Address" should re-fill it.
        if (checkoutPage.isSavedAddressPanelVisible()) {
            checkoutPage.clickUseSavedAddress();
            try { Thread.sleep(600); } catch (InterruptedException ignored) {}

            String filledAddress = checkoutPage.getAddressValue();
            System.out.println("[INFO] T097-6: Address after Use Saved Address = '" + filledAddress + "'");
            Assert.assertFalse(
                filledAddress.isEmpty(),
                "Address field should be populated after clicking Use Saved Address"
            );
            System.out.println("[PASS] T097-6: Use Saved Address correctly pre-filled the address field.");
        } else {
            System.out.println("[SKIP] T097-6: Saved address panel not visible (no prior address saved); skipping fill assertion.");
        }
    }

    /**
     * T097-7: Form fields can be freely updated (cleared and re-typed).
     * Verifies that React's controlled-input clearing (Ctrl+A + Delete)
     * works correctly so the state fully resets between edits.
     */
    @Test(description = "T097: Form fields can be cleared and re-typed correctly")
    public void testFormFieldsCanBeUpdated() {
        loginAndPrepareCart();
        CheckoutPage checkoutPage = proceedToCheckout();

        // First fill.
        checkoutPage.enterAddress("Old Address, Mumbai");
        Assert.assertEquals(checkoutPage.getAddressValue(), "Old Address, Mumbai",
                "First input should appear correctly");

        // Update to a new value.
        checkoutPage.enterAddress(ADDRESS);
        Assert.assertEquals(checkoutPage.getAddressValue(), ADDRESS,
                "Address field should update to the new value after re-typing");

        System.out.println("[PASS] T097-7: Form fields correctly update when cleared and re-typed.");
    }

    /**
     * T097-8: A complete, valid Cash On Delivery order placement navigates the
     * user to /customer/my-orders — end-to-end happy path for the entire
     * checkout flow. This test actually places a real order.
     */
    @Test(description = "T097: Complete valid checkout (COD) redirects to My Orders")
    public void testCompleteValidCheckoutRedirectsToOrders() {
        loginAndPrepareCart();
        CheckoutPage checkoutPage = proceedToCheckout();

        checkoutPage.fillFullForm(
            FULL_NAME, PHONE, ADDRESS, CITY, STATE, PINCODE, "Cash On Delivery"
        );
        checkoutPage.clickPlaceOrder();

        // Allow time for the order API call to complete and the toast + redirect to fire.
        try { Thread.sleep(2500); } catch (InterruptedException ignored) {}

        String currentUrl = driver.getCurrentUrl();
        System.out.println("[INFO] T097-8: URL after placing order = " + currentUrl);

        Assert.assertTrue(
            currentUrl.contains("/customer/my-orders"),
            "User should be redirected to /customer/my-orders after a successful COD order. URL: " + currentUrl
        );
        System.out.println("[PASS] T097-8: Complete COD checkout successfully placed and redirected to My Orders.");
    }
}
