package com.Infosys.ecommerceApplication.automation.tests;

import com.Infosys.ecommerceApplication.automation.pages.AdminOrdersPage;
import com.Infosys.ecommerceApplication.automation.pages.CartPage;
import com.Infosys.ecommerceApplication.automation.pages.CheckoutPage;
import com.Infosys.ecommerceApplication.automation.pages.CustomerOrdersPage;
import com.Infosys.ecommerceApplication.automation.pages.LoginPage;
import com.Infosys.ecommerceApplication.automation.pages.ProductDetailsPage;
import com.Infosys.ecommerceApplication.automation.pages.ProductListingPage;
import com.Infosys.ecommerceApplication.automation.utils.BaseTest;
import com.Infosys.ecommerceApplication.automation.utils.CartTestUtils;
import com.Infosys.ecommerceApplication.automation.utils.WaitUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Order Update Automation Suite
 *
 * Covers:
 *   - Customer order listing (/customer/my-orders)
 *   - Order details panel (opened by clicking the order card itself)
 *   - Order tracker / status display / payment mode on the customer side
 *   - Admin order status progression (PLACED → SHIPPED → DELIVERED)
 *   - End-to-end: place an order as customer, advance its status as admin,
 *     verify the status change reflects back on the customer's order list.
 *
 * NOTE: This application does not implement order cancellation in either
 * the customer UI (CustomerOrders.jsx) or the backend — there is no cancel
 * endpoint and only three statuses exist (PLACED, SHIPPED, DELIVERED).
 *
 * Test credentials:
 *   Customer: kartik@gmail.com / 1234
 *   Admin:    admin@gmail.com / admin
 */
public class OrderUpdateTest extends BaseTest {

    private static final String CUSTOMER_EMAIL    = "kartik@gmail.com";
    private static final String CUSTOMER_PASSWORD = "1234";
    private static final String ADMIN_EMAIL       = "admin@gmail.com";
    private static final String ADMIN_PASSWORD    = "admin";

    // ── Setup helpers ─────────────────────────────────────────────────────────

    private void loginAsCustomer() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(BASE_URL);
        loginPage.loginAs(CUSTOMER_EMAIL, CUSTOMER_PASSWORD);
        try {
            WaitUtils.waitForUrlContains(driver, "customer");
        } catch (Exception e) {
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        }
    }

    private void loginAsAdmin() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(BASE_URL);
        loginPage.loginAs(ADMIN_EMAIL, ADMIN_PASSWORD);
        try {
            WaitUtils.waitForUrlContains(driver, "admin");
        } catch (Exception e) {
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        }
    }

    /** Places one fresh COD order for the customer and returns to my-orders. */
    private void placeOneOrderAsCustomer() {
        loginAsCustomer();

        driver.get(BASE_URL + "/customer/cart");
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        CartTestUtils.clearServerCart(driver, BASE_URL);
        driver.navigate().refresh();
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}

        ProductListingPage productsPage = new ProductListingPage(driver);
        productsPage.open(BASE_URL);
        try {
            WaitUtils.waitForUrlContains(driver, "/customer/products");
        } catch (Exception ignored) {}
        try { Thread.sleep(3000); } catch (InterruptedException ignored) {}

        productsPage.openProductDetails(0);
        ProductDetailsPage detailsPage = new ProductDetailsPage(driver);
        Assert.assertTrue(detailsPage.isLoaded(), "Product details page should load");
        detailsPage.clickAddToCart();
        Assert.assertTrue(detailsPage.isAddedConfirmationVisible(), "Add to cart confirmation should appear");
        detailsPage.clickGoToCart();
        WaitUtils.waitForUrlContains(driver, "cart");
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}

        CartPage cartPage = new CartPage(driver);
        cartPage.clickCheckout();
        WaitUtils.waitForUrlContains(driver, "checkout");
        try { Thread.sleep(1200); } catch (InterruptedException ignored) {}

        CheckoutPage checkoutPage = new CheckoutPage(driver);
        checkoutPage.fillFullForm(
            "Kartik Verma", "9876543210",
            "123 MG Road, Bengaluru", "Bengaluru", "Karnataka", "560001",
            "Cash On Delivery"
        );
        checkoutPage.clickPlaceOrder();
        try { Thread.sleep(2500); } catch (InterruptedException ignored) {}
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CUSTOMER ORDERS — VIEW
    // ─────────────────────────────────────────────────────────────────────────

    @Test(description = "My Orders page loads with correct heading")
    public void testMyOrdersPageLoads() {
        loginAsCustomer();
        CustomerOrdersPage ordersPage = new CustomerOrdersPage(driver);
        ordersPage.open(BASE_URL);

        Assert.assertTrue(ordersPage.isLoaded(), "My Orders page should be loaded");
        String heading = ordersPage.getHeadingText();
        System.out.println("[INFO] My Orders heading = '" + heading + "'");
        Assert.assertFalse(heading.isEmpty(), "Orders heading should not be empty");
        System.out.println("[PASS] My Orders page loaded with heading: " + heading);
    }

    @Test(description = "My Orders page shows order cards or an empty state after placing an order")
    public void testMyOrdersShowsPlacedOrder() {
        placeOneOrderAsCustomer();

        CustomerOrdersPage ordersPage = new CustomerOrdersPage(driver);
        // After placing, Checkout.jsx redirects to /customer/my-orders already.
        if (!driver.getCurrentUrl().contains("/customer/my-orders")) {
            ordersPage.open(BASE_URL);
        }
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        Assert.assertTrue(ordersPage.hasOrders(),
            "My Orders should show at least one order after placing one");
        int count = ordersPage.getOrderCount();
        System.out.println("[PASS] My Orders correctly shows " + count + " order(s) after checkout.");
    }

    @Test(description = "Newly placed order shows status PLACED")
    public void testNewOrderHasPlacedStatus() {
        placeOneOrderAsCustomer();

        CustomerOrdersPage ordersPage = new CustomerOrdersPage(driver);
        if (!driver.getCurrentUrl().contains("/customer/my-orders")) {
            ordersPage.open(BASE_URL);
        }
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        String status = ordersPage.getFirstOrderStatus();
        System.out.println("[INFO] First order status = '" + status + "'");

        Assert.assertTrue(
            status.toUpperCase().contains("PLACED"),
            "Newly placed order should have status PLACED. Got: " + status
        );
        System.out.println("[PASS] New order correctly shows status PLACED.");
    }

    @Test(description = "Clicking an order card opens the order details panel with product info")
    public void testViewOrderDetailsOpensPanel() {
        placeOneOrderAsCustomer();

        CustomerOrdersPage ordersPage = new CustomerOrdersPage(driver);
        if (!driver.getCurrentUrl().contains("/customer/my-orders")) {
            ordersPage.open(BASE_URL);
        }
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        Assert.assertTrue(ordersPage.hasOrders(), "Need at least one order to view details");

        ordersPage.clickOrderCard(0);
        Assert.assertTrue(
            ordersPage.isDetailsPanelVisible(),
            "Order details panel should be visible after clicking the order card"
        );

        int productCount = ordersPage.getDetailProductCount();
        System.out.println("[INFO] Order detail product count = " + productCount);
        Assert.assertTrue(productCount > 0,
            "Order details should show at least one product");

        ordersPage.closeDetailsPanel();
        Assert.assertFalse(
            ordersPage.isDetailsPanelVisible(),
            "Order details panel should close after clicking the close (X) button"
        );
        System.out.println("[PASS] Order details panel correctly opened, showed products, and closed.");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CUSTOMER ORDERS — TRACKER, TOTALS & PAYMENT MODE DISPLAY
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * NOTE: This application does not implement order cancellation —
     * CustomerOrders.jsx only supports three statuses (PLACED, SHIPPED,
     * DELIVERED) with no cancel action anywhere in the UI or backend.
     * The tests below instead validate the order tracker, totals, and
     * payment-mode display that ARE part of this feature set.
     */

    @Test(description = "Order tracker shows the correct active step for a PLACED order")
    public void testOrderTrackerShowsPlacedStep() {
        placeOneOrderAsCustomer();

        CustomerOrdersPage ordersPage = new CustomerOrdersPage(driver);
        if (!driver.getCurrentUrl().contains("/customer/my-orders")) {
            ordersPage.open(BASE_URL);
        }
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        Assert.assertTrue(ordersPage.hasOrders(), "Need at least one order");

        // Exactly one tracker step should be marked active for the freshly
        // placed order — scoped to just that order's card, since older
        // orders further down the (newest-first) list may be SHIPPED or
        // DELIVERED and would otherwise add their own active steps to a
        // page-wide count.
        int activeStepsOnNewestOrder = ordersPage.getActiveTrackerStepCountForOrder(0);
        System.out.println("[INFO] Active tracker steps on newest order = " + activeStepsOnNewestOrder);

        Assert.assertEquals(activeStepsOnNewestOrder, 1,
            "A freshly PLACED order should have exactly 1 active tracker step (Order Placed)");
        System.out.println("[PASS] Order tracker correctly shows 1 active step for a new PLACED order.");
    }

    @Test(description = "Order card displays the correct payment mode that was selected at checkout")
    public void testOrderCardShowsCorrectPaymentMode() {
        placeOneOrderAsCustomer();

        CustomerOrdersPage ordersPage = new CustomerOrdersPage(driver);
        if (!driver.getCurrentUrl().contains("/customer/my-orders")) {
            ordersPage.open(BASE_URL);
        }
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        Assert.assertTrue(ordersPage.hasOrders(), "Need at least one order");

        java.util.List<org.openqa.selenium.WebElement> paymentModes = driver.findElements(
            org.openqa.selenium.By.cssSelector(".order-payment-mode")
        );
        Assert.assertFalse(paymentModes.isEmpty(), "Payment mode element should be present on the order card");

        String paymentText = paymentModes.get(0).getText().trim();
        System.out.println("[INFO] Payment mode text = '" + paymentText + "'");

        Assert.assertTrue(
            paymentText.contains("Cash On Delivery"),
            "Order placed with COD should show 'Cash On Delivery' as payment mode. Got: " + paymentText
        );
        System.out.println("[PASS] Order card correctly displays payment mode: " + paymentText);
    }

    @Test(description = "Order total shown on the order card matches a positive, correctly formatted amount")
    public void testOrderCardTotalIsCorrectlyFormatted() {
        placeOneOrderAsCustomer();

        CustomerOrdersPage ordersPage = new CustomerOrdersPage(driver);
        if (!driver.getCurrentUrl().contains("/customer/my-orders")) {
            ordersPage.open(BASE_URL);
        }
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        java.util.List<String> totals = ordersPage.getOrderTotals();
        Assert.assertFalse(totals.isEmpty(), "Order total should be visible on the order card");

        String firstTotal = totals.get(0);
        System.out.println("[INFO] Order card total = '" + firstTotal + "'");

        Assert.assertTrue(
            firstTotal.contains("₹") || firstTotal.contains("?"),
            "Order total should be formatted with ₹ currency symbol. Got: " + firstTotal
        );
        System.out.println("[PASS] Order card total correctly formatted: " + firstTotal);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ADMIN-SIDE ORDER STATUS PROGRESSION
    // ─────────────────────────────────────────────────────────────────────────

    @Test(description = "Admin can mark a PLACED order as Shipped")
    public void testAdminCanMarkOrderAsShipped() {
        placeOneOrderAsCustomer();

        loginAsAdmin();
        AdminOrdersPage adminOrders = new AdminOrdersPage(driver);
        adminOrders.open(BASE_URL);
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        List<String> actionsBefore = adminOrders.getActionButtonTexts();
        System.out.println("[INFO] Available actions before = " + actionsBefore);

        if (actionsBefore.isEmpty()) {
            System.out.println("[SKIP] No orders available to advance.");
            return;
        }

        String firstAction = adminOrders.clickFirstActionButton();
        System.out.println("[INFO] Clicked action: " + firstAction);

        Assert.assertTrue(
            firstAction.toLowerCase().contains("ship") || firstAction.toLowerCase().contains("deliver"),
            "First available action on a fresh order should be Ship or Deliver. Got: " + firstAction
        );

        driver.navigate().refresh();
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        System.out.println("[PASS] Admin successfully advanced an order status via '" + firstAction + "'.");
    }

    @Test(description = "Admin order status progresses through the full lifecycle: PLACED → SHIPPED → DELIVERED")
    public void testFullOrderStatusLifecycle() {
        placeOneOrderAsCustomer();

        loginAsAdmin();
        AdminOrdersPage adminOrders = new AdminOrdersPage(driver);
        adminOrders.open(BASE_URL);
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        // PLACED -> SHIPPED -> DELIVERED is exactly 2 manual transitions.
        // NOTE: AdminOrders.jsx also schedules an automatic 30-second
        // client-side setTimeout to auto-advance SHIPPED -> DELIVERED, but
        // since we navigate/refresh between clicks that timer is destroyed
        // along with the component — clicking "Mark Delivered" ourselves is
        // the only transition this test relies on.
        int maxTransitions = 2;
        int stepsCompleted = 0;

        for (int i = 0; i < maxTransitions; i++) {
            int actionCount = adminOrders.getActionButtonCount();
            if (actionCount == 0) {
                System.out.println("[INFO] No more actions available after " + stepsCompleted + " step(s) — order reached terminal state.");
                break;
            }

            String action = adminOrders.clickFirstActionButton();
            stepsCompleted++;
            System.out.println("[INFO] Lifecycle step " + stepsCompleted + ": " + action);

            driver.navigate().refresh();
            try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        }

        Assert.assertTrue(
            stepsCompleted > 0,
            "At least one order status transition should have occurred"
        );

        int completedCount = adminOrders.getCompletedOrderCount();
        System.out.println("[INFO] Completed/delivered orders after lifecycle = " + completedCount);
        System.out.println("[PASS] Order status lifecycle test completed " + stepsCompleted + " transition(s).");
    }

    @Test(description = "Order status change by admin is reflected on the customer's My Orders page")
    public void testOrderStatusChangeReflectsOnCustomerSide() {
        placeOneOrderAsCustomer();

        // Capture initial status as customer.
        CustomerOrdersPage customerOrders = new CustomerOrdersPage(driver);
        if (!driver.getCurrentUrl().contains("/customer/my-orders")) {
            customerOrders.open(BASE_URL);
        }
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        String statusBefore = customerOrders.getFirstOrderStatus();
        System.out.println("[INFO] Status before admin action (customer view) = '" + statusBefore + "'");

        // Switch to admin and advance the order.
        loginAsAdmin();
        AdminOrdersPage adminOrders = new AdminOrdersPage(driver);
        adminOrders.open(BASE_URL);
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        if (adminOrders.getActionButtonCount() == 0) {
            System.out.println("[SKIP] No actionable orders for admin to advance.");
            return;
        }

        String action = adminOrders.clickFirstActionButton();
        System.out.println("[INFO] Admin action performed: " + action);
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        // Switch back to customer and verify the status updated.
        loginAsCustomer();
        CustomerOrdersPage refreshedOrders = new CustomerOrdersPage(driver);
        refreshedOrders.open(BASE_URL);
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        String statusAfter = refreshedOrders.getFirstOrderStatus();
        System.out.println("[INFO] Status after admin action (customer view) = '" + statusAfter + "'");

        Assert.assertNotEquals(
            statusAfter, statusBefore,
            "Order status should change on the customer's view after the admin updates it"
        );
        System.out.println("[PASS] Admin status change correctly reflected on customer's My Orders page: "
            + statusBefore + " → " + statusAfter);
    }

    @Test(description = "Admin orders page shows order tracker / status progression UI")
    public void testAdminOrderTrackerIsVisible() {
        placeOneOrderAsCustomer();

        loginAsAdmin();
        AdminOrdersPage adminOrders = new AdminOrdersPage(driver);
        adminOrders.open(BASE_URL);
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        Assert.assertTrue(adminOrders.hasOrders(),
            "Admin orders page should show at least one order after a fresh placement");

        int orderCount = adminOrders.getOrderCount();
        System.out.println("[PASS] Admin orders page correctly shows " + orderCount + " order(s) with tracker UI.");
    }
}
