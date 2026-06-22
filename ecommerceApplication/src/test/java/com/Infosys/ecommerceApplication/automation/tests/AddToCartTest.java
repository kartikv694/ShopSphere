package com.Infosys.ecommerceApplication.automation.tests;

import com.Infosys.ecommerceApplication.automation.utils.BaseTest;
import com.Infosys.ecommerceApplication.automation.utils.CartTestUtils;
import com.Infosys.ecommerceApplication.automation.utils.WaitUtils;
import com.Infosys.ecommerceApplication.automation.pages.LoginPage;
import com.Infosys.ecommerceApplication.automation.pages.ProductListingPage;
import com.Infosys.ecommerceApplication.automation.pages.ProductDetailsPage;
import com.Infosys.ecommerceApplication.automation.pages.CartPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Day 41 — US017: Validate Add to Cart
 * T088: Automate add-to-cart functionality
 * T089: Validate product addition
 *
 * Flow under test: login -> open a product's details page -> click
 * "Add to Cart" -> confirm the "Product Added" modal appears -> navigate
 * to /customer/cart -> verify the product is actually present there.
 */
public class AddToCartTest extends BaseTest {

    private static final String VALID_EMAIL    = "kartik@gmail.com";
    private static final String VALID_PASSWORD = "1234";

    /** Logs in and opens the first product's details page. Reused by every test below. */
    private ProductDetailsPage loginAndOpenFirstProduct() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(BASE_URL);
        loginPage.loginAs(VALID_EMAIL, VALID_PASSWORD);

        try {
            WaitUtils.waitForUrlContains(driver, "customer");
        } catch (Exception e) {
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        }

        ProductListingPage productsPage = new ProductListingPage(driver);
        productsPage.open(BASE_URL);
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {} // allow product API call to complete

        productsPage.openProductDetails(0);

        ProductDetailsPage detailsPage = new ProductDetailsPage(driver);
        Assert.assertTrue(detailsPage.isLoaded(), "Should be on a product details page after clicking a card");
        return detailsPage;
    }

    /**
     * T088: Clicking "Add to Cart" triggers the add-to-cart action and
     * shows the "Product Added" confirmation modal.
     */
    @Test(description = "T088: Add to Cart button adds the product and shows confirmation")
    public void testAddToCartShowsConfirmation() {
        ProductDetailsPage detailsPage = loginAndOpenFirstProduct();

        detailsPage.clickAddToCart();

        boolean confirmed = detailsPage.isAddedConfirmationVisible();
        Assert.assertTrue(confirmed, "\"Product Added\" confirmation modal should appear after Add to Cart");

        System.out.println("[PASS] T088: Add to Cart triggered and confirmation modal displayed.");
    }

    /**
     * T089: Validate product addition — after adding to cart and
     * navigating to /customer/cart, the product actually appears there.
     */
    @Test(description = "T089: Added product is present in the cart page")
    public void testProductAppearsInCartAfterAdd() {
        ProductDetailsPage detailsPage = loginAndOpenFirstProduct();
        String productName = detailsPage.getProductName();

        detailsPage.clickAddToCart();
        Assert.assertTrue(detailsPage.isAddedConfirmationVisible(), "Confirmation modal should appear before navigating to cart");

        detailsPage.clickGoToCart();
        try { WaitUtils.waitForUrlContains(driver, "cart"); } catch (Exception ignored) {}

        CartPage cartPage = new CartPage(driver);
        Assert.assertTrue(cartPage.isLoaded(), "Should navigate to /customer/cart from the confirmation modal");

        boolean productInCart = cartPage.containsProduct(productName);
        Assert.assertTrue(
            productInCart,
            "Cart should contain '" + productName + "' after adding it. Items found: " + cartPage.getCartItemNames()
        );

        System.out.println("[PASS] T089: Product '" + productName + "' validated in cart after add-to-cart.");
    }

    /**
     * T089: Validate that the cart item count increases by exactly one
     * when a single product is added (starting from an empty cart).
     */
    @Test(description = "T089: Cart item count increases by one after adding a product")
    public void testCartCountIncreasesAfterAdd() {
        // Must be logged in first — /customer/cart and /customer/products
        // are protected routes that redirect to "/" for anonymous users.
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(BASE_URL);
        loginPage.loginAs(VALID_EMAIL, VALID_PASSWORD);
        try {
            WaitUtils.waitForUrlContains(driver, "customer");
        } catch (Exception e) {
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        }

        // Start from a clean cart state for a deterministic count check.
        // The cart is server-backed now, so localStorage alone can't clear
        // it — call the real /api/cart/clear endpoint.
        driver.get(BASE_URL + "/customer/cart");
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
        CartTestUtils.clearServerCart(driver, BASE_URL);
        driver.navigate().refresh();
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}

        CartPage cartPageBefore = new CartPage(driver);
        int countBefore = cartPageBefore.getCartItemCount();

        ProductListingPage productsPage = new ProductListingPage(driver);
        productsPage.open(BASE_URL);
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        productsPage.openProductDetails(0);

        ProductDetailsPage detailsPage = new ProductDetailsPage(driver);
        Assert.assertTrue(detailsPage.isLoaded(), "Should be on a product details page after clicking a card");
        detailsPage.clickAddToCart();
        Assert.assertTrue(detailsPage.isAddedConfirmationVisible(), "Confirmation modal should appear");
        detailsPage.clickGoToCart();

        CartPage cartPageAfter = new CartPage(driver);
        int countAfter = cartPageAfter.getCartItemCount();

        Assert.assertEquals(
            countAfter, countBefore + 1,
            "Cart item count should increase by exactly 1 after adding one product"
        );

        System.out.println("[PASS] T089: Cart count went from " + countBefore + " to " + countAfter + " after add-to-cart.");
    }

    /**
     * T088: "Continue Shopping" closes the confirmation modal without
     * navigating away from the product details page.
     */
    @Test(description = "T088: Continue Shopping dismisses modal and stays on the product page")
    public void testContinueShoppingStaysOnProductPage() {
        ProductDetailsPage detailsPage = loginAndOpenFirstProduct();
        String urlBeforeAdd = driver.getCurrentUrl();

        detailsPage.clickAddToCart();
        Assert.assertTrue(detailsPage.isAddedConfirmationVisible(), "Confirmation modal should appear");

        detailsPage.clickContinueShopping();
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}

        Assert.assertEquals(
            driver.getCurrentUrl(), urlBeforeAdd,
            "Continue Shopping should keep the user on the same product details page"
        );

        System.out.println("[PASS] T088: Continue Shopping correctly dismissed the modal without navigating away.");
    }
}
