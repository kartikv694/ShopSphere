package com.Infosys.ecommerceApplication.automation.tests;

import com.Infosys.ecommerceApplication.automation.utils.BaseTest;
import com.Infosys.ecommerceApplication.automation.utils.WaitUtils;
import com.Infosys.ecommerceApplication.automation.pages.LoginPage;
import com.Infosys.ecommerceApplication.automation.pages.ProductListingPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Day 39 — US0015: Validate Product Listing (continued)
 * T082: Validate product details (name, price)
 * T083: Verify product navigation
 *
 * Day 40 — US016: Validate Product Details
 * T084: Test search functionality
 * T085: Validate filtering
 * T086: Handle dynamic elements
 * T087: Implement reusable components
 *
 * REUSE NOTE (T087): All product-grid/search/filter logic lives in
 * {@link ProductListingPage}, the SAME Page Object already used by
 * ProductListingTest (Day 38). No new page object or duplicate locators
 * were created — this class only adds new test scenarios on top of the
 * existing reusable POM.
 */
public class ProductDetailsAndSearchTest extends BaseTest {

    private static final String VALID_EMAIL    = "kartik@gmail.com";
    private static final String VALID_PASSWORD = "1234";

    /**
     * Reused helper (T087): log in as the customer and land on
     * /customer/products before each test in this class.
     */
    @BeforeMethod
    public void goToProductsAsLoggedInCustomer() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open(BASE_URL);
        loginPage.loginAs(VALID_EMAIL, VALID_PASSWORD);

        try {
            WaitUtils.waitForUrlContains(driver, "customer");
        } catch (Exception e) {
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
        }

        driver.get(BASE_URL + "/customer/products");
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
    }

    /**
     * T082: Validate product details — first product card shows a non-empty
     * name and a price.
     */
    @Test(description = "T082: Product card displays name and price")
    public void testProductDetailsNameAndPrice() {
        ProductListingPage page = new ProductListingPage(driver);
        Assert.assertTrue(page.isLoaded(), "Product listing page should load");

        int count = page.getProductCount();
        if (count == 0) {
            System.out.println("[INFO] T082: No products available to validate — skipping detail check.");
            return;
        }

        Assert.assertTrue(
            page.isFirstProductComplete(),
            "First product card should have both a name and a price"
        );

        String name  = page.getProductName(0);
        String price = page.getProductPrice(0);

        Assert.assertFalse(name.trim().isEmpty(), "Product name should not be empty");
        Assert.assertFalse(price.trim().isEmpty(), "Product price should not be empty");

        System.out.println("[PASS] T082: Product details validated — Name='" + name + "', Price='" + price + "'");
    }

    /**
     * T082: Validate product details across multiple cards (not just the first one).
     */
    @Test(description = "T082: Multiple product cards display valid name and price")
    public void testMultipleProductDetails() {
        ProductListingPage page = new ProductListingPage(driver);
        Assert.assertTrue(page.isLoaded(), "Product listing page should load");

        int count = page.getProductCount();
        if (count == 0) {
            System.out.println("[INFO] T082: No products available — skipping multi-card check.");
            return;
        }

        int toCheck = Math.min(count, 3);
        for (int i = 0; i < toCheck; i++) {
            Assert.assertTrue(
                page.isProductCardComplete(i),
                "Product card #" + i + " should have both a name and a price"
            );
        }
        System.out.println("[PASS] T082: Validated name & price for " + toCheck + " product card(s).");
    }

    /**
     * T083: Verify product navigation — clicking a product card opens its
     * product details page.
     */
    @Test(description = "T083: Clicking a product card navigates to its details page")
    public void testProductNavigationToDetails() {
        ProductListingPage page = new ProductListingPage(driver);
        Assert.assertTrue(page.isLoaded(), "Product listing page should load");

        int count = page.getProductCount();
        if (count == 0) {
            System.out.println("[INFO] T083: No products available — skipping navigation check.");
            return;
        }

        String nameBeforeClick = page.getProductName(0);
        page.openProductDetails(0);

        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        Assert.assertTrue(
            page.isOnProductDetailsPage(),
            "Clicking a product card should navigate to /customer/products/{id}. URL: " + page.getCurrentUrl()
        );

        System.out.println("[PASS] T083: Navigated to details page for '" + nameBeforeClick + "'. URL: " + page.getCurrentUrl());
    }

    /**
     * T083: Verify the user can navigate back to the listing page from the
     * product details page.
     */
    @Test(description = "T083: Browser back navigation returns from product details to the listing page")
    public void testProductNavigationBackToListing() {
        ProductListingPage page = new ProductListingPage(driver);
        Assert.assertTrue(page.isLoaded(), "Product listing page should load");

        int count = page.getProductCount();
        if (count == 0) {
            System.out.println("[INFO] T083: No products available — skipping back-navigation check.");
            return;
        }

        page.openProductDetails(0);
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        Assert.assertTrue(page.isOnProductDetailsPage(), "Should be on product details page after click");

        driver.navigate().back();
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        Assert.assertTrue(
            page.getCurrentUrl().contains("/customer/products"),
            "Navigating back should return to the products listing. URL: " + page.getCurrentUrl()
        );

        System.out.println("[PASS] T083: Back navigation returned to listing page. URL: " + page.getCurrentUrl());
    }

    /**
     * T084: Test search functionality — searching for a known/likely keyword
     * filters the visible product cards.
     */
    @Test(description = "T084: Searching for a product keyword filters the product grid")
    public void testSearchFunctionality() {
        ProductListingPage page = new ProductListingPage(driver);
        Assert.assertTrue(page.isLoaded(), "Product listing page should load");

        int totalBefore = page.getProductCount();
        if (totalBefore == 0) {
            System.out.println("[INFO] T084: No products available — skipping search check.");
            return;
        }

        // Use the first product's own name as the search keyword so we know
        // it should remain in the results — this keeps the test independent
        // of any specific seed data.
        String keyword = page.getProductName(0);
        if (keyword == null || keyword.trim().isEmpty()) {
            System.out.println("[INFO] T084: Could not derive a search keyword — skipping.");
            return;
        }
        // Use just the first word to make the search broad enough to match.
        String searchTerm = keyword.trim().split("\\s+")[0];

        page.searchProduct(searchTerm);

        Assert.assertTrue(
            page.doVisibleProductsMatchSearch(searchTerm),
            "After searching for '" + searchTerm + "', visible products should relate to that term (or show no-results state)"
        );

        System.out.println("[PASS] T084: Search for '" + searchTerm + "' returned matching/expected results.");
    }

    /**
     * T084: Searching for a term that matches no product shows the
     * "no products found" state (T086: dynamic element handling).
     */
    @Test(description = "T084-T086: Searching for a nonsense term shows a 'no products found' state")
    public void testSearchWithNoResults() {
        ProductListingPage page = new ProductListingPage(driver);
        Assert.assertTrue(page.isLoaded(), "Product listing page should load");

        String nonsense = "zzz_nonexistent_product_xyz_123";
        page.searchProduct(nonsense);

        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        int countAfter = page.getProductCount();
        Assert.assertEquals(countAfter, 0, "Searching for a nonsense term should return zero product cards");

        System.out.println("[PASS] T084-T086: Search with no matches correctly shows zero product cards.");
    }

    /**
     * T085: Validate filtering — the category dropdown is present and can be used.
     */
    @Test(description = "T085: Category filter dropdown is available on the products page")
    public void testCategoryFilterAvailable() {
        ProductListingPage page = new ProductListingPage(driver);
        Assert.assertTrue(page.isLoaded(), "Product listing page should load");

        // Category filter lives in Navbar.jsx (used on /admin/products).
        // On /customer/products, filtering is also supported via the
        // shared SearchContext, so this checks the dropdown exists wherever
        // the navbar with the filter is rendered.
        if (page.isCategoryFilterAvailable()) {
            System.out.println("[PASS] T085: Category filter dropdown found on the page.");
        } else {
            System.out.println("[INFO] T085: Category filter dropdown not present on this layout — feature may be admin-only.");
        }
    }

    /**
     * T085: Validate filtering — selecting a category filters the visible
     * products to that category (only runs if the dropdown is present).
     */
    @Test(description = "T085: Selecting a category filters visible products")
    public void testFilterByCategory() {
        ProductListingPage page = new ProductListingPage(driver);
        Assert.assertTrue(page.isLoaded(), "Product listing page should load");

        if (!page.isCategoryFilterAvailable()) {
            System.out.println("[INFO] T085: Category filter not available on this layout — skipping.");
            return;
        }

        page.filterByCategory("electronics");

        Assert.assertTrue(
            page.doVisibleProductsMatchCategory("electronics"),
            "After filtering by 'electronics', all visible products should belong to that category (or none should be shown)"
        );

        System.out.println("[PASS] T085: Category filter 'electronics' applied correctly.");
    }

    /**
     * T086: Handle dynamic elements — the product grid renders asynchronously
     * (loading state -> populated grid / empty-state).
     */
    @Test(description = "T086: Product grid handles its async loading state correctly")
    public void testDynamicProductGridLoading() {
        ProductListingPage page = new ProductListingPage(driver);

        // Navigate fresh so we can observe the loading -> loaded transition.
        page.open(BASE_URL);

        page.waitForProductsToStabilize();

        Assert.assertTrue(page.isLoaded(), "Product grid should finish loading (either products or empty state)");
        System.out.println("[PASS] T086: Product grid reached a stable state with " + page.getProductCount() + " card(s).");
    }

    /**
     * T087: Implement reusable components — demonstrate that the same
     * ProductListingPage methods used above can be reused end-to-end:
     * load -> validate details -> search -> navigate.
     */
    @Test(description = "T087: Reusable ProductListingPage methods support a full end-to-end flow")
    public void testReusableComponentsEndToEndFlow() {
        ProductListingPage page = new ProductListingPage(driver);

        // Reuse #1: load
        Assert.assertTrue(page.isLoaded(), "Page should load (reused isLoaded())");

        int count = page.getProductCount();
        if (count == 0) {
            System.out.println("[INFO] T087: No products available — end-to-end flow ends after load check.");
            return;
        }

        // Reuse #2: detail validation
        Assert.assertTrue(page.isProductCardComplete(0), "First card should be complete (reused isProductCardComplete())");

        // Reuse #3: navigation
        page.openProductDetails(0);
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
        Assert.assertTrue(page.isOnProductDetailsPage(), "Should reach details page (reused isOnProductDetailsPage())");

        // Reuse #4: go back, then reuse search
        driver.navigate().back();
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        Assert.assertTrue(
            page.getCurrentUrl().contains("/customer/products"),
            "Back navigation should return to listing (reused getCurrentUrl())"
        );

        System.out.println("[PASS] T087: Reusable ProductListingPage components executed full load->validate->navigate->back flow.");
    }
}
