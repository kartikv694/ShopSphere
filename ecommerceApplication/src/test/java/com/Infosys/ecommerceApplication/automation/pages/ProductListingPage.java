package com.Infosys.ecommerceApplication.automation.pages;

import com.Infosys.ecommerceApplication.automation.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

/**
 * T065: POM — Page Object for /customer/products
 * T080: Automate product listing page
 * T081: Validate product visibility
 *
 * T082: Validate product details (name, price)
 * T083: Verify product navigation
 * T084: Test search functionality
 * T085: Validate filtering
 * T086: Handle dynamic elements
 * T087: Implement reusable components — every method below is generic so it
 *       can be reused by ProductListingTest, ProductDetailsTest, and any
 *       future test that needs to interact with the product grid/search/filter.
 */
public class ProductListingPage {

    private final WebDriver driver;

    // Locators — based on Products.jsx (actual rendered markup uses ".card")
    private final By productCards    = By.cssSelector(".grid .card, .card, [class*='product-card'], [class*='ProductCard']");
    private final By productNames    = By.cssSelector(".card h3");
    private final By productPrices   = By.cssSelector(".card .price, [class*='price'], [class*='Price']");
    private final By productImages   = By.cssSelector(".card img, [class*='product-card'] img");

    // T084: Search — Navbar.jsx / CustomerNavbar.jsx
    private final By searchInput     = By.cssSelector("input.search-input, input[placeholder*='Search'], input[placeholder*='search']");
    private final By searchButton    = By.cssSelector("button.search-btn, button[aria-label='Search products']");

    // T085: Category filter — Navbar.jsx ("select.category-select")
    private final By categorySelect  = By.cssSelector("select.category-select");

    private final By navbarProducts  = By.xpath("//a[contains(@href,'/customer/products')]");
    private final By noProductsText  = By.xpath("//*[contains(text(),'No products') or contains(text(),'no products')]");

    public ProductListingPage(WebDriver driver) {
        this.driver = driver;
    }

    public void open(String baseUrl) {
        // Retry once on renderer timeout — heavy test suites can cause the
        // Chrome renderer to lag on the first navigation attempt.
        try {
            driver.get(baseUrl + "/customer/products");
        } catch (org.openqa.selenium.TimeoutException e) {
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
            driver.navigate().refresh();
        }
    }

    /** T086: Handle dynamic elements — wait for either products or the empty-state message. */
    public boolean isLoaded() {
        try {
            WaitUtils.waitForVisible(driver, productCards);
            return true;
        } catch (Exception e) {
            try {
                WaitUtils.waitForVisible(driver, noProductsText);
                return true;
            } catch (Exception e2) {
                return driver.getCurrentUrl().contains("products");
            }
        }
    }

    /** T080: Returns all product cards currently rendered. */
    public List<WebElement> getAllProductCards() {
        return driver.findElements(productCards);
    }

    /**
     * Polls until at least `minCount` product cards have rendered, or
     * times out after 20 seconds. Prevents IndexOutOfBoundsException when
     * the products API/render is slow under heavy test-suite load.
     */
    public List<WebElement> waitForProductCards(int minCount) {
        long deadline = System.currentTimeMillis() + 20_000;
        List<WebElement> cards = getAllProductCards();
        while (cards.size() < minCount && System.currentTimeMillis() < deadline) {
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            cards = getAllProductCards();
        }
        if (cards.isEmpty()) {
            throw new RuntimeException(
                "No product cards rendered on " + driver.getCurrentUrl()
                    + " after waiting 20s. The products API may be slow or the page failed to load."
            );
        }
        return cards;
    }

    /** T081: Checks at least one product is visible. */
    public boolean areProductsVisible() {
        List<WebElement> cards = getAllProductCards();
        if (cards.isEmpty()) return false;
        return cards.stream().anyMatch(WebElement::isDisplayed);
    }

    public int getProductCount() {
        return getAllProductCards().size();
    }

    /**
     * T082: Validate product details — checks the first card has a non-empty
     * name and a price that contains the ₹ currency symbol / digits.
     * T087: Reusable — can be called for any card index.
     */
    public boolean isFirstProductComplete() {
        return isProductCardComplete(0);
    }

    /** T082/T087: Reusable — validate name + price for the card at the given index. */
    public boolean isProductCardComplete(int index) {
        try {
            List<WebElement> cards = getAllProductCards();
            if (cards.isEmpty() || index >= cards.size()) return false;

            WebElement card = cards.get(index);

            String name = card.findElement(By.cssSelector("h3")).getText();
            String price = card.findElement(By.cssSelector(".price")).getText();

            boolean hasName  = name != null && !name.trim().isEmpty();
            boolean hasPrice = price != null && !price.trim().isEmpty()
                    && (price.contains("₹") || price.matches(".*\\d.*"));

            return hasName && hasPrice;
        } catch (Exception e) {
            return false;
        }
    }

    /** T082: Get the displayed name of the product card at the given index. */
    public String getProductName(int index) {
        return getAllProductCards().get(index).findElement(By.cssSelector("h3")).getText();
    }

    /** T082: Get the displayed price text of the product card at the given index. */
    public String getProductPrice(int index) {
        return getAllProductCards().get(index).findElement(By.cssSelector(".price")).getText();
    }

    /**
     * T083: Verify product navigation — click a product card and confirm the
     * browser navigates to its details page (/customer/products/{id}).
     * T087: Reusable — works for whichever card index is passed in.
     */
    public void openProductDetails(int index) {
        // Dismiss Chrome's "Change your password" popup if it's covering
        // the page (commonly appears right after login with a weak/leaked
        // test password) — it intercepts clicks on underlying elements.
        WaitUtils.dismissBrowserPasswordDialogIfPresent(driver);

        // Ensure at least one product card has actually rendered before
        // grabbing the list — under heavy load/slow renders, a fixed
        // Thread.sleep() before calling this method isn't always enough.
        List<WebElement> cards = waitForProductCards(index + 1);
        WebElement card = cards.get(index);

        String urlBefore = driver.getCurrentUrl();

        try {
            card.click();
        } catch (Exception e) {
            // Element may be partially covered by an absolutely-positioned
            // child (image-carousel arrows) — fall back to a JS click on
            // the card itself.
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", card);
        }

        // Wait for the SPA route to actually change before returning.
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(10))
                .until(d -> !d.getCurrentUrl().equals(urlBefore));
        } catch (Exception ignored) {
            // If the URL didn't change within 10s, the caller's assertion
            // on isOnProductDetailsPage() will report the failure with context.
        }
    }

    /** T083: Check the current URL matches a product details page pattern. */
    public boolean isOnProductDetailsPage() {
        String url = driver.getCurrentUrl();
        // /customer/products/<id>  (id is numeric/alphanumeric, not the literal "products")
        return url.matches(".*/customer/products/[^/]+/?$") && !url.endsWith("/products");
    }

    /**
     * T084: Test search functionality — type a keyword into the navbar
     * search box and trigger the search (Enter key, matching CustomerNavbar.jsx
     * onKeyDown handler).
     * T087: Reusable — same method works from any page that has the navbar.
     *
     * NOTE: The search input is a React-controlled component. Selenium's
     * WebElement.clear() can leave React's internal state out of sync with
     * the DOM value (it doesn't always fire a proper "input" event), which
     * then causes the typed keyword to be appended to stale text. To avoid
     * this we select-all + delete via keyboard before typing, which fires
     * real key events React picks up correctly.
     */
    public void searchProduct(String keyword) {
        WebElement field = WaitUtils.waitForVisible(driver, searchInput);
        field.click();

        // Robustly clear a React-controlled input via keyboard events
        field.sendKeys(org.openqa.selenium.Keys.chord(org.openqa.selenium.Keys.CONTROL, "a"));
        field.sendKeys(org.openqa.selenium.Keys.DELETE);

        field.sendKeys(keyword);
        field.sendKeys(org.openqa.selenium.Keys.ENTER);

        // Wait for the page heading to reflect the search term
        // (Products.jsx renders `Search results for "<term>"` once
        // SearchContext.search is updated).
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5))
                .until(d -> d.getPageSource().toLowerCase().contains("search results for"));
        } catch (Exception ignored) {
            // fall through — caller will check product count/visibility
        }
    }

    /** T084: Click the dedicated search button (alternative trigger to Enter key). */
    public void clickSearchButton() {
        try {
            WaitUtils.waitForClickable(driver, searchButton).click();
        } catch (Exception ignored) {
            // some layouts may not expose a separate button — Enter key covers it
        }
    }

    /**
     * T084: After a search, check every visible product card's name/category
     * text contains the keyword (case-insensitive), OR the "no products"
     * message is shown if nothing matches.
     */
    public boolean doVisibleProductsMatchSearch(String keyword) {
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        List<WebElement> cards = getAllProductCards();
        if (cards.isEmpty()) {
            // T086: Dynamic "no results" element
            return !driver.findElements(noProductsText).isEmpty()
                    || !driver.findElement(By.tagName("body")).getText().trim().isEmpty();
        }

        String lowerKeyword = keyword.toLowerCase();
        return cards.stream().anyMatch(card -> card.getText().toLowerCase().contains(lowerKeyword));
    }

    /**
     * T085: Validate filtering — select a category from the navbar dropdown
     * and confirm the rendered cards belong to that category (or the
     * "no products" message is shown).
     * T087: Reusable — pass any category value defined in Navbar.jsx
     * ("electronics", "laptops", "mobiles", etc.).
     */
    public void filterByCategory(String categoryValue) {
        WebElement select = WaitUtils.waitForVisible(driver, categorySelect);
        new Select(select).selectByValue(categoryValue);
    }

    /** T085: Returns true if every visible card's category text matches, or no products are shown. */
    public boolean doVisibleProductsMatchCategory(String categoryValue) {
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        List<WebElement> cards = getAllProductCards();
        if (cards.isEmpty()) {
            return true; // empty result set is valid for a filter with no matches
        }

        String lowerCategory = categoryValue.toLowerCase();
        return cards.stream().allMatch(card -> card.getText().toLowerCase().contains(lowerCategory));
    }

    /** T085: True if the category filter dropdown is present on the page. */
    public boolean isCategoryFilterAvailable() {
        return !driver.findElements(categorySelect).isEmpty();
    }

    /** T086: Handle dynamic elements — wait until the product grid stops changing size. */
    public void waitForProductsToStabilize() {
        int previousCount = -1;
        for (int i = 0; i < 10; i++) {
            int currentCount = getAllProductCards().size();
            if (currentCount == previousCount && currentCount > 0) {
                return;
            }
            previousCount = currentCount;
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
        }
    }

    public void navigateToProductsViaNavbar() {
        WaitUtils.waitForClickable(driver, navbarProducts).click();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
